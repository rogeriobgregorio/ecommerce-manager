package com.rogeriogregorio.ecommercemanager.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PixListChargeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Cob> cobs;
    private Parametros parametros;

    public PixListChargeDTO() {
    }

    public PixListChargeDTO(List<Cob> cobs, Parametros parametros) {
        this.cobs = cobs;
        this.parametros = parametros;
    }

    public List<Cob> getCobs() {
        return cobs;
    }

    public void setCobs(List<Cob> cobs) {
        this.cobs = cobs;
    }

    public Parametros getParametros() {
        return parametros;
    }

    public void setParametros(Parametros parametros) {
        this.parametros = parametros;
    }

    @Override
    public String toString() {
        return "PixListChargeDTO {" +
                "\n  cobs: " + cobs +
                "\n  parametros: " + parametros +
                "\n}";
    }

    public static class Cob implements Serializable {

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
        private List<Pix> pix;
        private String status;

        public Cob() {
        }

        public Cob(Devedor devedor, Loc loc, String pixCopiaECola,
                   Valor valor, String chave, Calendario calendario,
                   String txid, List<InfoAdicionais> infoAdicionais,
                   String location, int revisao, List<Pix> pix, String status) {

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
            this.pix = pix;
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

        public List<Pix> getPix() {
            return pix;
        }

        public void setPix(List<Pix> pix) {
            this.pix = pix;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "Cob {" +
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
                    "\n  pix: " + pix +
                    "\n  status: " + status +
                    "\n}";
        }
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

        @Override
        public String toString() {
            return "InfoAdicionais {" +
                    "\n  valor: " + valor +
                    "\n  nome: " + nome +
                    "\n}";
        }
    }

    public static class Pix implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String horario;
        private String valor;
        private String chave;
        private String txid;
        private String endToEndId;

        public Pix() {
        }

        public Pix(String horario, String valor, String chave, String txid, String endToEndId) {
            this.horario = horario;
            this.valor = valor;
            this.chave = chave;
            this.txid = txid;
            this.endToEndId = endToEndId;
        }

        public String getHorario() {
            return horario;
        }

        public void setHorario(String horario) {
            this.horario = horario;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor = valor;
        }

        public String getChave() {
            return chave;
        }

        public void setChave(String chave) {
            this.chave = chave;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getEndToEndId() {
            return endToEndId;
        }

        public void setEndToEndId(String endToEndId) {
            this.endToEndId = endToEndId;
        }

        @Override
        public String toString() {
            return "Pix {" +
                    "\n  horario: " + horario +
                    "\n  valor: " + valor +
                    "\n  chave: " + chave +
                    "\n  txid: " + txid +
                    "\n  endToEndId: " + endToEndId +
                    "\n}";
        }
    }

    public static class Parametros implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Paginacao paginacao;
        private String inicio;
        private String fim;

        public Parametros() {
        }

        public Parametros(Paginacao paginacao, String inicio, String fim) {
            this.paginacao = paginacao;
            this.inicio = inicio;
            this.fim = fim;
        }

        public Paginacao getPaginacao() {
            return paginacao;
        }

        public void setPaginacao(Paginacao paginacao) {
            this.paginacao = paginacao;
        }

        public String getInicio() {
            return inicio;
        }

        public void setInicio(String inicio) {
            this.inicio = inicio;
        }

        public String getFim() {
            return fim;
        }

        public void setFim(String fim) {
            this.fim = fim;
        }

        @Override
        public String toString() {
            return "Parametros {" +
                    "\n  paginacao: " + paginacao +
                    "\n  inicio: " + inicio +
                    "\n  fim: " + fim +
                    "\n}";
        }
    }

    public static class Paginacao implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private int paginaAtual;
        private int quantidadeTotalDeItens;
        private int itensPorPagina;
        private int quantidadeDePaginas;

        public Paginacao() {
        }

        public Paginacao(int paginaAtual, int quantidadeTotalDeItens,
                         int itensPorPagina, int quantidadeDePaginas) {

            this.paginaAtual = paginaAtual;
            this.quantidadeTotalDeItens = quantidadeTotalDeItens;
            this.itensPorPagina = itensPorPagina;
            this.quantidadeDePaginas = quantidadeDePaginas;
        }

        public int getPaginaAtual() {
            return paginaAtual;
        }

        public void setPaginaAtual(int paginaAtual) {
            this.paginaAtual = paginaAtual;
        }

        public int getQuantidadeTotalDeItens() {
            return quantidadeTotalDeItens;
        }

        public void setQuantidadeTotalDeItens(int quantidadeTotalDeItens) {
            this.quantidadeTotalDeItens = quantidadeTotalDeItens;
        }

        public int getItensPorPagina() {
            return itensPorPagina;
        }

        public void setItensPorPagina(int itensPorPagina) {
            this.itensPorPagina = itensPorPagina;
        }

        public int getQuantidadeDePaginas() {
            return quantidadeDePaginas;
        }

        public void setQuantidadeDePaginas(int quantidadeDePaginas) {
            this.quantidadeDePaginas = quantidadeDePaginas;
        }

        @Override
        public String toString() {
            return "Paginacao {" +
                    "\n  paginaAtual: " + paginaAtual +
                    "\n  quantidadeTotalDeItens: " + quantidadeTotalDeItens +
                    "\n  itensPorPagina: " + itensPorPagina +
                    "\n  quantidadeDePaginas: " + quantidadeDePaginas +
                    "\n}";
        }
    }
}
