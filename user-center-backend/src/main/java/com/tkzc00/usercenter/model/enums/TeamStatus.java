package com.tkzc00.usercenter.model.enums;

/**
 * 队伍状态枚举
 * @author tkzc00
 */
public enum TeamStatus {
    PUBLIC(0, "公开"),
    PRIVATE(1, "私有"),
    SECRET(2, "加密");

    private int value;
    private String text;

    public static TeamStatus getEnumByValue(Integer value) {
        if (value == null) return null;
        for (TeamStatus teamStatus : TeamStatus.values()) {
            if (teamStatus.getValue() == value) {
                return teamStatus;
            }
        }
        return null;
    }

    TeamStatus(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
