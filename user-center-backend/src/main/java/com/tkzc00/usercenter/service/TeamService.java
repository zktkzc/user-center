package com.tkzc00.usercenter.service;

import com.tkzc00.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tkzc00.usercenter.model.domain.User;

/**
* @author tkzc00
* @description 针对表【team(队伍表)】的数据库操作Service
* @createDate 2024-02-02 19:14:49
*/
public interface TeamService extends IService<Team> {
    long addTeam(Team team, User loginUser);
}
