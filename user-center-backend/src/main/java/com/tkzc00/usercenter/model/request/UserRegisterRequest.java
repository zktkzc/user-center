package com.tkzc00.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author tkzc00
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -6814638637082839897L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
