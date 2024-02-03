package com.tkzc00.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = 2944547485452615691L;

    /**
     * 队伍ID
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
