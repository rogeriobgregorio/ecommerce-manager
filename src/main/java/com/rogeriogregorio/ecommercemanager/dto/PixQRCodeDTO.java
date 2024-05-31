package com.rogeriogregorio.ecommercemanager.dto;

import java.io.Serial;
import java.io.Serializable;

public class PixQRCodeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String qrcode;
    private String imagemQrcode;
    private String linkVisualizacao;

    public PixQRCodeDTO() {
        // default constructor
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getImagemQrcode() {
        return imagemQrcode;
    }

    public void setImagemQrcode(String imagemQrcode) {
        this.imagemQrcode = imagemQrcode;
    }

    public String getLinkVisualizacao() {
        return linkVisualizacao;
    }

    public void setLinkVisualizacao(String linkVisualizacao) {
        this.linkVisualizacao = linkVisualizacao;
    }

    @Override
    public String toString() {
        return "PixQRCodeDTO {" +
                "\n  qrcode: " + qrcode +
                "\n  imagemQrcode: " + imagemQrcode +
                "\n  linkVisualizacao: " + linkVisualizacao +
                "\n}";
    }
}

