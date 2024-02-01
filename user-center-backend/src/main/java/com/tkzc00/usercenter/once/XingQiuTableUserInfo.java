package com.tkzc00.usercenter.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 星球表格用户信息
 *
 * @author tkzc00
 */
@Data
public class XingQiuTableUserInfo {
    @ExcelProperty("成员编号")
    private String planetCode;
    @ExcelProperty("成员昵称")
    private String username;
}
