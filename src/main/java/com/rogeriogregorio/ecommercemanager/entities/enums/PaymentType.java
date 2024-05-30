package com.rogeriogregorio.ecommercemanager.entities.enums;

public enum PaymentType {

    PIX(1);

    private int code;

    private PaymentType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PaymentType valueOf(int code) {

        for (PaymentType value : PaymentType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("The payment method code is invalid");
    }
}
