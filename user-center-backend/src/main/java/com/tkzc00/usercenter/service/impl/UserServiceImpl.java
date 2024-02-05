package com.tkzc00.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tkzc00.usercenter.common.ErrorCode;
import com.tkzc00.usercenter.exception.BusinessException;
import com.tkzc00.usercenter.mapper.UserMapper;
import com.tkzc00.usercenter.model.domain.User;
import com.tkzc00.usercenter.model.vo.UserVO;
import com.tkzc00.usercenter.service.UserService;
import com.tkzc00.usercenter.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tkzc00.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.tkzc00.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author tkzc00
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "tkzc00";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度不足");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不足");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号长度超过限制");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        // 密码和确认密码必须一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和确认密码不一致");
        }
        // 账户不能重复
        long count = userMapper.selectCount(new QueryWrapper<User>().eq("userAccount", userAccount));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户已存在");
        }
        // 星球编号不能重复
        count = userMapper.selectCount(new QueryWrapper<User>().eq("planetCode", planetCode));
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已存在");
        }
        // 2. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean success = this.save(user);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度不足");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不足");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        // 2. 加密密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("userAccount", userAccount)
                .eq("userPassword", encryptPassword)
        );
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword.");
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户或密码错误");
        }
        // 3. 用户信息脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser 原始用户
     * @return 脱敏用户
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setProfile(originUser.getProfile());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否注销成功
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        if (request == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户(SQL 版)
     *
     * @return 用户列表
     */
    @Deprecated
    public List<User> searchUsersByTagsBySQL(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接and查询
        for (String tag : tags) {
            queryWrapper.like("tags", tag);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 根据标签搜索用户（内存过滤）
     *
     * @return 用户列表
     */
    @Override
    public List<User> searchUsersByTags(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 在内存中判断是否包含要求的标签
        return users.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr)) {
                return false;
            }
            Set<String> tagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            for (String tag : tags) {
                if (!tagNameSet.contains(tag)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 更新用户信息
     *
     * @param user      要修改的用户信息
     * @param loginUser
     * @return 是否修改成功
     */
    @Override
    public int updateUser(User user, User loginUser) {
        if (user == null || loginUser == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        Long userId = user.getId();
        if (userId <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id小于等于0");
        // 如果不是管理员，只允许修改自己的信息
        // 如果是管理员，允许更新任意用户信息
        if (!isAdmin(loginUser) && !userId.equals(loginUser.getId()))
            throw new BusinessException(ErrorCode.NO_AUTH, "非管理员用户只能修改自己的信息");
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null)
            throw new BusinessException(ErrorCode.NULL_ERROR);
        return userMapper.updateById(user);
    }

    /**
     * 获取当前登录的用户信息
     *
     * @param request 请求
     * @return 用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) throw new BusinessException(ErrorCode.NOT_LOGIN);
        return (User) userObj;
    }

    /**
     * 判断当前登录用户是否为管理员
     *
     * @param request 请求
     * @return 是否为管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        if (request == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 判断当前登录用户是否为管理员
     *
     * @param loginUser 当前登录用户
     * @return 是否为管理员
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 根据标签的相似度匹配用户
     *
     * @param loginUser 当前登录用户
     * @param num       匹配数量
     * @return 用户列表
     */
    @Override
    public List<User> matchUsers(User loginUser, long num) {
        List<User> userList = list(new QueryWrapper<User>()
                .isNotNull("tags").select("id", "tags"));
        if (CollectionUtils.isEmpty(userList)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户列表为空");
        }
        String loginUserTagsStr = loginUser.getTags();
        Gson gson = new Gson();
        List<String> loginUserTags = gson.fromJson(loginUserTagsStr, new TypeToken<List<String>>() {
        }.getType());
        List<Pair<User, Long>> list = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            // 剔除自己
            if (user.getId().equals(loginUser.getId())) {
                continue;
            }
            String userTagsStr = user.getTags();
            // 剔除没有标签的用户
            if (StringUtils.isBlank(userTagsStr))
                continue;
            List<String> userTags = gson.fromJson(userTagsStr, new TypeToken<List<String>>() {
            }.getType());
            long score = AlgorithmUtils.miniDistanceForTags(loginUserTags, userTags);
            list.add(new Pair<>(user, score));
        }
        // 按编辑距离从小到大排序
        List<Pair<User, Long>> topList = list.stream().sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num).collect(Collectors.toList());
        return topList.stream().map(Pair::getKey)
                .map(user -> getSafetyUser(getById(user.getId()))).collect(Collectors.toList());
    }
}