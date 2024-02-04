package com.tkzc00.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tkzc00.usercenter.common.BaseResponse;
import com.tkzc00.usercenter.common.ErrorCode;
import com.tkzc00.usercenter.common.ResultUtils;
import com.tkzc00.usercenter.exception.BusinessException;
import com.tkzc00.usercenter.model.domain.User;
import com.tkzc00.usercenter.model.request.UserLoginRequest;
import com.tkzc00.usercenter.model.request.UserRegisterRequest;
import com.tkzc00.usercenter.model.vo.UserVO;
import com.tkzc00.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.tkzc00.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author tkzc00
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        long userId = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword))
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        // 仅管理员可查询
        if (!userService.isAdmin(request)) throw new BusinessException(ErrorCode.NO_AUTH);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.eq("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> safetyUsers = userList.stream()
                .map(user -> userService.getSafetyUser(user))
                .collect(Collectors.toList());
        return ResultUtils.success(safetyUsers);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long page, long pageSize, HttpServletRequest request) {
        // 如果有缓存，直接读缓存
        User loginUser = userService.getLoginUser(request);
        String key = String.format("yupao:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Page<User> userPage = (Page<User>) valueOperations.get(key);
        if (userPage != null) return ResultUtils.success(userPage);
        // 无缓存，查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(page, pageSize), queryWrapper);
        // 缓存结果
        try {
            valueOperations.set(key, userPage, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis set error: {}", e.getMessage());
        }
        return ResultUtils.success(userPage);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        // 仅管理员可查询
        if (!userService.isAdmin(request)) throw new BusinessException(ErrorCode.NO_AUTH);
        if (id <= 0) throw new BusinessException(ErrorCode.PARAMS_ERROR);
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 获取当前用户登录态
     *
     * @param request 请求
     * @return 当前用户信息
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null)
            throw new BusinessException(ErrorCode.NOT_LOGIN, "当前用户未登录");
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList 标签列表
     * @return 用户列表
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        List<User> users = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(users);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 校验参数是否为空
        if (user == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 根据标签的相似度匹配用户
     *
     * @param request 请求
     * @param num     匹配数量
     * @return 用户列表
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不合法");
        User loginUser = userService.getLoginUser(request);
        List<User> users = userService.matchUsers(loginUser, num);
        return ResultUtils.success(users);
    }
}
