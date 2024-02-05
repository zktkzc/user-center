package com.tkzc00.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tkzc00.usercenter.common.ErrorCode;
import com.tkzc00.usercenter.exception.BusinessException;
import com.tkzc00.usercenter.mapper.TeamMapper;
import com.tkzc00.usercenter.model.domain.Team;
import com.tkzc00.usercenter.model.domain.User;
import com.tkzc00.usercenter.model.domain.UserTeam;
import com.tkzc00.usercenter.model.dto.TeamQuery;
import com.tkzc00.usercenter.model.enums.TeamStatus;
import com.tkzc00.usercenter.model.request.TeamJoinRequest;
import com.tkzc00.usercenter.model.request.TeamQuitRequest;
import com.tkzc00.usercenter.model.request.TeamUpdateRequest;
import com.tkzc00.usercenter.model.vo.TeamUserVO;
import com.tkzc00.usercenter.model.vo.UserVO;
import com.tkzc00.usercenter.service.TeamService;
import com.tkzc00.usercenter.service.UserService;
import com.tkzc00.usercenter.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author tkzc00
 * @description 针对表【team(队伍表)】的数据库操作Service实现
 * @createDate 2024-02-02 19:14:49
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 添加队伍
     *
     * @param team      队伍信息
     * @param loginUser 登录用户
     * @return 队伍id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if (team == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        if (loginUser == null)
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍最大人数不合法");
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不合法");
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不合法");
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatus teamStatus = TeamStatus.getEnumByValue(status);
        if (teamStatus == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不合法");
        String password = team.getPassword();
        if (teamStatus.equals(TeamStatus.SECRET) && (StringUtils.isBlank(password) || password.length() > 32))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码不合法");
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍过期时间不合法");
        // 校验用户最多创建5个队伍
        long count = count(new QueryWrapper<Team>().eq("userId", loginUser.getId()));
        if (count >= 5)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        // 插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(loginUser.getId());
        boolean result = save(team);
        Long teamId = team.getId();
        if (!result || teamId == null || teamId <= 0)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        // 插入用户队伍关联信息到用户队伍关联表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(loginUser.getId());
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        return teamId;
    }

    /**
     * 查询队伍列表
     *
     * @param teamQuery 队伍查询条件
     * @param isAdmin   是否是管理员
     * @return 队伍列表
     */
    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0)
                queryWrapper.eq("id", id);
            List<Long> teamIds = teamQuery.getTeamIds();
            if (!CollectionUtils.isEmpty(teamIds))
                queryWrapper.in("id", teamIds);
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name))
                queryWrapper.like("name", name);
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText))
                queryWrapper.and(wrapper -> wrapper.like("name", searchText)
                        .or().like("description", searchText));
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description))
                queryWrapper.like("description", description);
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0)
                queryWrapper.eq("maxNum", maxNum);
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0)
                queryWrapper.eq("userId", userId);
            Integer status = teamQuery.getStatus();
            TeamStatus teamStatus = TeamStatus.getEnumByValue(status);
            if (teamStatus == null)
                teamStatus = TeamStatus.PUBLIC;
            if (!isAdmin && teamStatus.equals(TeamStatus.PRIVATE))
                throw new BusinessException(ErrorCode.NO_AUTH, "无权限查看私有队伍");
            queryWrapper.eq("status", teamStatus.getValue());
        }
        // 不展示已过期的队伍
        queryWrapper.and(wrapper -> wrapper.isNull("expireTime").or().gt("expireTime", new Date()));
        List<Team> teamList = list(queryWrapper);
        if (teamList == null || teamList.isEmpty())
            return new ArrayList<>();
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询队伍对应的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null || userId <= 0)
                continue;
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    /**
     * 更新队伍信息
     *
     * @param teamUpdateRequest 队伍更新请求
     * @return 是否更新成功
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        Long teamId = teamUpdateRequest.getId();
        if (teamId == null || teamId <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id为空或小于等于0");
        Team oldTeam = getById(teamId);
        if (oldTeam == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        // 只有管理员或者队伍的创建者可以修改队伍信息
        if (!oldTeam.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser))
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限修改他人队伍");
        TeamStatus teamStatus = TeamStatus.getEnumByValue(teamUpdateRequest.getStatus());
        if (TeamStatus.SECRET.equals(teamStatus)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword()))
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要有密码");
        }
        Team newTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, newTeam);
        boolean result = updateById(newTeam);
        if (!result)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍失败");
        return true;
    }

    /**
     * 加入队伍
     *
     * @param teamJoinRequest 加入队伍请求
     * @param loginUser
     * @return 是否加入成功
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id为空或小于等于0");
        Team team = getById(teamId);
        if (team == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        // 不能加入已过期的队伍
        if (team.getExpireTime() != null && new Date().after(team.getExpireTime()))
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        TeamStatus teamStatus = TeamStatus.getEnumByValue(team.getStatus());
        if (TeamStatus.PRIVATE.equals(teamStatus))
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限加入私有队伍");
        if (TeamStatus.SECRET.equals(teamStatus)) {
            String password = teamJoinRequest.getPassword();
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword()))
                throw new BusinessException(ErrorCode.NO_AUTH, "密码错误");
        }
        // 最多加入5个队伍
        Long userId = loginUser.getId();
        RLock rLock = redissonClient.getLock("yupao:join_team");
        try {
            while (true) {
                if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    // 拿到锁后执行加入队伍操作
                    long count = userTeamService.count(new QueryWrapper<UserTeam>().eq("userId", userId));
                    if (count >= 5)
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多加入5个队伍");
                    // 不能重复加入已加入的队伍
                    count = userTeamService.count(new QueryWrapper<UserTeam>().eq("userId", userId).eq("teamId", teamId));
                    if (count > 0)
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入队伍");
                    // 不能加入人数已满的队伍
                    count = getTeamUsersByTeamId(teamId);
                    if (count >= team.getMaxNum())
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满");
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    boolean result = userTeamService.save(userTeam);
                    if (!result)
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
                    return true;
                }
                // 没拿到锁则继续循环拿锁
            }
        } catch (InterruptedException e) {
            log.error("Do joinTeam error", e);
            return false;
        } finally {
            // 只能释放自己加的锁
            if (rLock.isHeldByCurrentThread())
                rLock.unlock();
        }
    }

    /**
     * 退出队伍
     *
     * @param teamQuitRequest 退出队伍请求
     * @param loginUser       登录用户
     * @return 是否退出成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为null");
        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id为空或小于等于0");
        Team team = getById(teamId);
        if (team == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        // 校验是否加入了该队伍
        Long userId = loginUser.getId();
        long count = userTeamService.count(new QueryWrapper<UserTeam>().eq("userId", userId).eq("teamId", teamId));
        if (count <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入该队伍");
        count = getTeamUsersByTeamId(teamId);
        if (count == 1) {
            // 队伍只有一个人时，直接删除队伍以及用户队伍关联信息
            boolean result = removeById(teamId);
            boolean success = userTeamService.removeById(new QueryWrapper<UserTeam>().eq("teamId", teamId));
            if (!result && !success)
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
            return true;
        } else {
            // 是否是队伍的创建者
            if (team.getUserId().equals(userId)) {
                // 创建者退出队伍时，将队伍的创建者变更为队伍的第二个加入者
                List<UserTeam> userTeamList = userTeamService.list(new QueryWrapper<UserTeam>()
                        .eq("teamId", teamId).orderByAsc("joinTime").last("limit 2"));
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2)
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
                UserTeam userTeam = userTeamList.get(1);
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(userTeam.getUserId());
                boolean result = updateById(updateTeam);
                if (!result)
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
                // 删除用户队伍关联信息
                boolean success = userTeamService.remove(new QueryWrapper<UserTeam>().eq("userId", userId).eq("teamId", teamId));
                if (!success)
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
                return true;
            }
            // 普通成员退出队伍
            boolean result = userTeamService.remove(new QueryWrapper<UserTeam>().eq("userId", userId).eq("teamId", teamId));
            if (!result)
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "退出队伍失败");
            return true;
        }
    }

    /**
     * 解散队伍
     *
     * @param id        队伍id
     * @param loginUser 登录用户
     * @return 是否解散成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, User loginUser) {
        if (id <= 0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id为空或小于等于0");
        Team team = getById(id);
        if (team == null)
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        if (!team.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser))
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限解散他人队伍");
        boolean result = removeById(id);
        boolean success = userTeamService.remove(new QueryWrapper<UserTeam>().eq("teamId", id));
        if (!result && !success)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解散队伍失败");
        return true;
    }

    /**
     * 获取队伍的成员数量
     *
     * @param teamId 队伍id
     * @return 成员数量
     */
    private long getTeamUsersByTeamId(long teamId) {
        return userTeamService.count(new QueryWrapper<UserTeam>().eq("teamId", teamId));
    }
}




