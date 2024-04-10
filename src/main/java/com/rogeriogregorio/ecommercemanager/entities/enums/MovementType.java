package com.rogeriogregorio.ecommercemanager.entities.enums;

public enum MovementType {

    ENTRANCE(1),
    EXIT(2);

    private int code;

    private MovementType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MovementType valueOf(int code) {

        for (MovementType value : MovementType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("The stock movement code is invalid");
    }
}
