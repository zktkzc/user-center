package com.tkzc00.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tkzc00.usercenter.common.ErrorCode;
import com.tkzc00.usercenter.exception.BusinessException;
import com.tkzc00.usercenter.mapper.TeamMapper;
import com.tkzc00.usercenter.model.domain.Team;
import com.tkzc00.usercenter.model.domain.User;
import com.tkzc00.usercenter.model.domain.UserTeam;
import com.tkzc00.usercenter.model.enums.TeamStatus;
import com.tkzc00.usercenter.service.TeamService;
import com.tkzc00.usercenter.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

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
}




