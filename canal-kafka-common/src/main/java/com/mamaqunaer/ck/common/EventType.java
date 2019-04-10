package com.caiya.ck.common;

/**
 * 数据库变更事件类型.
 *
 * @author wangnan
 * @since 1.0
 */
public enum EventType {

    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventType of(String value) {
        for (EventType eventType : EventType.values()) {
            if (eventType.getValue().equals(value)) {
                return eventType;
            }
        }

        return null;
    }


}
