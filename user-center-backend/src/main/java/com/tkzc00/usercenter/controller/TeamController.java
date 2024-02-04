package com.tkzc00.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tkzc00.usercenter.common.BaseResponse;
import com.tkzc00.usercenter.common.ErrorCode;
import com.tkzc00.usercenter.common.ResultUtils;
import com.tkzc00.usercenter.exception.BusinessException;
import com.tkzc00.usercenter.model.domain.Team;
import com.tkzc00.usercenter.model.domain.User;
import com.tkzc00.usercenter.model.domain.UserTeam;
import com.tkzc00.usercenter.model.dto.TeamQuery;
import com.tkzc00.usercenter.model.request.*;
import com.tkzc00.usercenter.model.vo.TeamUserVO;
import com.tkzc00.usercenter.service.TeamService;
import com.tkzc00.usercenter.service.UserService;
import com.tkzc00.usercenter.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 队伍接口
 *
 * @author tkzc00
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
public class TeamController {
    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestParam Long teamId) {
        if (teamId == null || teamId <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id为空或小于等于0");
        boolean result = teamService.removeById(teamId);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库删除操作失败");
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库更新操作失败");
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeam(@RequestParam("teamId") Long teamId) {
        if (teamId == null || teamId <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id为空或小于等于0");
        Team team = teamService.getById(teamId);
        if (team == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "数据库查询操作失败");
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        List<TeamUserVO> list = teamService.listTeams(teamQuery, userService.isAdmin(request));
        if (list == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "查询失败");
        return ResultUtils.success(list);
    }

    @GetMapping("/list/create")
    public BaseResponse<List<TeamUserVO>> listMyTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> list = teamService.listTeams(teamQuery, true);
        if (list == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "查询失败");
        return ResultUtils.success(list);
    }

    @GetMapping("/list/join")
    public BaseResponse<List<TeamUserVO>> listJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        User loginUser = userService.getLoginUser(request);
        List<UserTeam> userTeamList = userTeamService.list(new QueryWrapper<UserTeam>().eq("userId", loginUser.getId()));
        if (userTeamList == null || userTeamList.isEmpty())
            throw new BusinessException(ErrorCode.NULL_ERROR, "查询失败");
        List<Long> teamIds = new ArrayList<>(userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId)).keySet());
        teamQuery.setTeamIds(teamIds);
        List<TeamUserVO> list = teamService.listTeams(teamQuery, true);
        if (list == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "查询失败");
        return ResultUtils.success(list);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> page = teamService.page(new Page<>(teamQuery.getPage(), teamQuery.getPageSize()), queryWrapper);
        if (page == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "查询失败");
        return ResultUtils.success(page);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        return ResultUtils.success(true);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request) {
        if (teamDeleteRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        long id = teamDeleteRequest.getTeamId();
        if (id <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id为空或小于等于0");
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id, loginUser);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库删除操作失败");
        return ResultUtils.success(true);
    }
}
