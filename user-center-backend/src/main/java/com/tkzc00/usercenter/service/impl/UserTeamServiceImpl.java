package com.tkzc00.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tkzc00.usercenter.model.domain.UserTeam;
import com.tkzc00.usercenter.service.UserTeamService;
import com.tkzc00.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author tkzc00
* @description 针对表【user_team(用户队伍关联表)】的数据库操作Service实现
* @createDate 2024-02-02 19:16:35
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




