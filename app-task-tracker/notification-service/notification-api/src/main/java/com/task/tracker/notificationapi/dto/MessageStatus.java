package com.task.tracker.notificationapi.dto;

public enum MessageStatus {
    /**
     * отправлен
     */
    SENT,
    /**
     * ожидает отправки
     */
    AWAITING_DISPATCH,
    /**
     * отмена сообщения по причине внешнего апи
     */
    FAILED;

}
