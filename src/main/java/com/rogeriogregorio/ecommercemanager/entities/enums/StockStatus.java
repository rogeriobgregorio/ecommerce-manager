package com.rogeriogregorio.ecommercemanager.entities.enums;

public enum StockStatus {

    AVAILABLE(1),
    OUT_OF_STOCK(2),
    PRE_ORDER(3);

    private int code;

    private StockStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StockStatus valueOf(int code) {

        for (StockStatus value : StockStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("The stock status code is invalid");
    }
}
