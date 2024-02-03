package com.tkzc00.usercenter.model.dto;

import com.tkzc00.usercenter.common.PageRequest;
import lombok.Data;

/**
 * 队伍查询封装类
 *
 * @author tkzc00
 */
@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer status;
}
