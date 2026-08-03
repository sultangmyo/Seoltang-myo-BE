package com.seoltangmyo.sugarcat.domain.bloodsugar.entity;

public enum SugarStatus {
    LOW,
    NORMAL,
    HIGH;

    public static SugarStatus from(int sugarValue) {
        if (sugarValue < 80) {
            return LOW;
        }

        if (sugarValue <= 150) {
            return NORMAL;
        }

        return HIGH;
    }
}
