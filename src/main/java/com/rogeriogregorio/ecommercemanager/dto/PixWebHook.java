package com.rogeriogregorio.ecommercemanager.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PixWebHook implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Pix> pix;

    public PixWebHook() {
    }

    public PixWebHook(List<Pix> pix) {
        this.pix = pix;
    }

    public List<Pix> getPix() {
        return pix;
    }

    public void setPix(List<Pix> pix) {
        this.pix = pix;
    }

    public static class Pix implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String endToEndId;
        private String txid;
        private String chave;
        private String valor;
        private String horario;
        private String infoPagador;

        public Pix() {
        }

        public Pix(String endToEndId, String txid, String chave, String valor, String horario, String infoPagador) {
            this.endToEndId = endToEndId;
            this.txid = txid;
            this.chave = chave;
            this.valor = valor;
            this.horario = horario;
            this.infoPagador = infoPagador;
        }

        public String getEndToEndId() {
            return endToEndId;
        }

        public void setEndToEndId(String endToEndId) {
            this.endToEndId = endToEndId;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getChave() {
            return chave;
        }

        public void setChave(String chave) {
            this.chave = chave;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getHorario() {
            return horario;
        }

        public void setHorario(String horario) {
            this.horario = horario;
        }

        public String getInfoPagador() {
            return infoPagador;
        }

        public void setInfoPagador(String infoPagador) {
            this.infoPagador = infoPagador;
        }

        @Override
        public String toString() {
            return "Pix {" +
                    "\n  endToEndId: " + endToEndId +
                    "\n  txid: " + txid +
                    "\n  chave: " + chave +
                    "\n  valor: " + valor +
                    "\n  horario: " + horario +
                    "\n  infoPagador: " + infoPagador +
                    "\n}";
        }
    }

    @Override
    public String toString() {
        return "PixWebHook{" +
                "\n  pix: " + pix +
                "\n}";
    }
}
