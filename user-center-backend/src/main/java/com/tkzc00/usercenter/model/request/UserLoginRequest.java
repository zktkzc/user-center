package com.tkzc00.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用登录请求体
 *
 * @author tkzc00
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 5184000048059301096L;
    private String userAccount;
    private String userPassword;
}
