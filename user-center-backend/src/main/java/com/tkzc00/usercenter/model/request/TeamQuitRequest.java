package com.tkzc00.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户退出队伍请求
 *
 * @author tkzc00
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = -4193552427745302132L;

    /**
     * 队伍id
     */
    private Long teamId;
}
