package com.rogeriogregorio.ecommercemanager.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PixChargeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Devedor devedor;
    private Loc loc;
    private String pixCopiaECola;
    private Valor valor;
    private String chave;
    private Calendario calendario;
    private String txid;
    private List<InfoAdicionais> infoAdicionais;
    private String location;
    private int revisao;
    private String status;

    public PixChargeDTO() {
    }

    public PixChargeDTO(Devedor devedor, Loc loc, String pixCopiaECola,
                        Valor valor, String chave, Calendario calendario,
                        String txid, List<InfoAdicionais> infoAdicionais,
                        String location, int revisao, String status) {

        this.devedor = devedor;
        this.loc = loc;
        this.pixCopiaECola = pixCopiaECola;
        this.valor = valor;
        this.chave = chave;
        this.calendario = calendario;
        this.txid = txid;
        this.infoAdicionais = infoAdicionais;
        this.location = location;
        this.revisao = revisao;
        this.status = status;
    }

    public Devedor getDevedor() {
        return devedor;
    }

    public void setDevedor(Devedor devedor) {
        this.devedor = devedor;
    }

    public Loc getLoc() {
        return loc;
    }

    public void setLoc(Loc loc) {
        this.loc = loc;
    }

    public String getPixCopiaECola() {
        return pixCopiaECola;
    }

    public void setPixCopiaECola(String pixCopiaECola) {
        this.pixCopiaECola = pixCopiaECola;
    }

    public Valor getValor() {
        return valor;
    }

    public void setValor(Valor valor) {
        this.valor = valor;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Calendario getCalendario() {
        return calendario;
    }

    public void setCalendario(Calendario calendario) {
        this.calendario = calendario;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public List<InfoAdicionais> getInfoAdicionais() {
        return infoAdicionais;
    }

    public void setInfoAdicionais(List<InfoAdicionais> infoAdicionais) {
        this.infoAdicionais = infoAdicionais;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRevisao() {
        return revisao;
    }

    public void setRevisao(int revisao) {
        this.revisao = revisao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Devedor implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String cpf;
        private String nome;

        public Devedor() {
        }

        public Devedor(String cpf, String nome) {
            this.cpf = cpf;
            this.nome = nome;
        }

        public String getCpf() {
            return cpf;
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }

    public static class Loc implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String location;
        private int id;
        private String criacao;
        private String tipoCob;

        public Loc() {
        }

        public Loc(String location, int id, String criacao, String tipoCob) {
            this.location = location;
            this.id = id;
            this.criacao = criacao;
            this.tipoCob = tipoCob;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCriacao() {
            return criacao;
        }

        public void setCriacao(String criacao) {
            this.criacao = criacao;
        }

        public String getTipoCob() {
            return tipoCob;
        }

        public void setTipoCob(String tipoCob) {
            this.tipoCob = tipoCob;
        }
    }

    public static class Valor implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String original;

        public Valor() {
        }

        public Valor(String original) {
            this.original = original;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }

    public static class Calendario implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private int expiracao;
        private String criacao;

        public Calendario() {
        }

        public Calendario(int expiracao, String criacao) {
            this.expiracao = expiracao;
            this.criacao = criacao;
        }

        public int getExpiracao() {
            return expiracao;
        }

        public void setExpiracao(int expiracao) {
            this.expiracao = expiracao;
        }

        public String getCriacao() {
            return criacao;
        }

        public void setCriacao(String criacao) {
            this.criacao = criacao;
        }
    }

    public static class InfoAdicionais implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String valor;
        private String nome;

        public InfoAdicionais() {
        }

        public InfoAdicionais(String valor, String nome) {
            this.valor = valor;
            this.nome = nome;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }
}
