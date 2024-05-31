package com.rogeriogregorio.ecommercemanager.dto;

import java.io.Serial;
import java.io.Serializable;

public class PixEVPKeyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String chave;

    public PixEVPKeyDTO() {
        // default constructor
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    @Override
    public String toString() {
        return "PixEVPKeyDTO {" +
                "\n  chave: " + chave +
                "\n}";
    }
}
