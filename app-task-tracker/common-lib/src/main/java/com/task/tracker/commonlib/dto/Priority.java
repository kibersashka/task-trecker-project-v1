package com.task.tracker.commonlib.dto;

public enum Priority {

    LOW(10),
    MIDDLE(15),
    HIGH(25);

    private final int xpCount;

    Priority(int xpCount) {
        this.xpCount = xpCount;
    }

    public int getXpCount() {
        return xpCount;
    }
}
