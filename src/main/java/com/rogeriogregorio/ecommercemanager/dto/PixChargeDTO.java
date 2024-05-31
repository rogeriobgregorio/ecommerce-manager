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
        // default constructor
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

    @Override
    public String toString() {
        return "PixChargeDTO {" +
                "\n  devedor: " + devedor +
                "\n  loc: " + loc +
                "\n  pixCopiaECola: " + pixCopiaECola +
                "\n  valor: " + valor +
                "\n  chave: " + chave +
                "\n  calendario: " + calendario +
                "\n  txid: " + txid +
                "\n  infoAdicionais: " + infoAdicionais +
                "\n  location: " + location +
                "\n  revisao: " + revisao +
                "\n  status: " + status +
                "\n}";
    }

    public static class Devedor implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String cpf;
        private String nome;

        public Devedor() {
            // default constructor
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

        @Override
        public String toString() {
            return "Devedor {" +
                    "\n  cpf: " + cpf +
                    "\n  nome: " + nome +
                    "\n}";
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
            // default constructor
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

        @Override
        public String toString() {
            return "Loc {" +
                    "\n  location: " + location +
                    "\n  id: " + id +
                    "\n  criacao: " + criacao +
                    "\n  tipoCob: " + tipoCob +
                    "\n}";
        }
    }

    public static class Valor implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String original;

        public Valor() {
            // default constructor
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }

        @Override
        public String toString() {
            return "Valor {" +
                    "\n  original: " + original +
                    "\n}";
        }
    }

    public static class Calendario implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private int expiracao;
        private String criacao;

        public Calendario() {
            // default constructor
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

        @Override
        public String toString() {
            return "Calendario {" +
                    "\n  expiracao: " + expiracao +
                    "\n  criacao: " + criacao +
                    "\n}";
        }
    }

    public static class InfoAdicionais implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String valor;
        private String nome;

        public InfoAdicionais() {
            // default constructor
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

        @Override
        public String toString() {
            return "InfoAdicionais {" +
                    "\n  valor: " + valor +
                    "\n  nome: " + nome +
                    "\n}";
        }
    }
}
