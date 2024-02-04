package com.tkzc00.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户解散队伍请求
 *
 * @author tkzc00
 */
@Data
public class TeamDeleteRequest implements Serializable {
    private static final long serialVersionUID = -4193552427745302132L;

    /**
     * 队伍id
     */
    private Long teamId;
}
