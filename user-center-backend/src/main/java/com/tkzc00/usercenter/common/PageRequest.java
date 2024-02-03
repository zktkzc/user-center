package com.tkzc00.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 *
 * @author tkzc00
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -4005557220621014136L;
    /**
     * 当前页
     */
    protected int page = 1;
    /**
     * 每页显示的条数
     */
    protected int pageSize = 10;
}
