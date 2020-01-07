package com.zys.order.constants;

public enum OrderStatusEnum {

    ORDER_SUCCESS("1"),

    ORDER_PAYED("2"),

    ORDER_FAIL("0");

    private String status;

    OrderStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
