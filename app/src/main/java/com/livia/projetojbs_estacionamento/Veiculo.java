package com.livia.projetojbs_estacionamento;

public class Veiculo {
    String placa;
    String entradaDia;
    String entradaHora;
    String saidaDia;
    String saidaHora;

    String horaFormatada;

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getEntradaDia() {
        return entradaDia;
    }

    public void setEntradaDia(String entradaDia) {
        this.entradaDia = entradaDia;
    }

    public String getEntradaHora() {
        return entradaHora;
    }

    public void setEntradaHora(String entradaHora) {
        this.entradaHora = entradaHora;
    }

    public String getSaidaDia() {
        return saidaDia;
    }

    public void setSaidaDia(String saidaDia) {
        this.saidaDia = saidaDia;
    }

    public String getSaidaHora() {
        return saidaHora;
    }

    public void setSaidaHora(String saidaHora) {
        this.saidaHora = saidaHora;
    }

    public String getHoraFormatada() {
        return horaFormatada;
    }

    public void setHoraFormatada(String horaFormatada){
        this.horaFormatada = horaFormatada;
    }

    public Veiculo(String placa, String entradaDia, String entradaHora, String saidaDia, String saidaHora) {
        this.placa = placa;
        this.entradaDia = entradaDia;
        this.entradaHora = entradaHora;
        this.saidaDia = saidaDia;
        this.saidaHora = saidaHora;
    }

    public Veiculo() {
    }
}
