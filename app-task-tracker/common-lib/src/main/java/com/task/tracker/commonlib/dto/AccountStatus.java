package com.task.tracker.commonlib.dto;

public enum AccountStatus {
    INTER(0),
    JUNIOR(150),
    MIDDLE(500),
    SENIOR(1000);

    private final int xpCount;

    AccountStatus(int xpCount) {
        this.xpCount = xpCount;
    }

    public int getXpCount() {
        return xpCount;
    }
}
