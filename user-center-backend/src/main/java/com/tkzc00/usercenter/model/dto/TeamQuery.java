package com.tkzc00.usercenter.model.dto;

import com.tkzc00.usercenter.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 队伍查询封装类
 *
 * @author tkzc00
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    private static final long serialVersionUID = 3393875559886580474L;

    /**
     * id
     */
    private Long id;

    /**
     * 队伍id列表
     */
    private List<Long> teamIds;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 搜索关键词，同时对队伍名称和描述进行查询
     */
    private String searchText;

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
