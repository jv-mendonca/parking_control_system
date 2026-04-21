package ParkingControl.entities;

import java.time.Duration;
import java.time.LocalDateTime;

public class Estacionamento {
    private Cliente cliente;
    private Carro carro;
    private LocalDateTime entrada;
    private LocalDateTime saida;
    private int ticket;
    private Duration duracao;
    private String vaga;


    public Estacionamento(Cliente cliente, Carro carro, LocalDateTime entrada, LocalDateTime saida, int ticket, String vaga) {
        this.ticket = ticket;
        this.cliente = cliente;
        this.carro = carro;
        this.entrada = entrada;
        this.saida = saida;
        this.vaga =  vaga;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public int getTicket() {
        return ticket;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Carro getCarro() {
        return carro;
    }

    public void setCarro(Carro carro) {
        this.carro = carro;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }

    public double getValor() {
        if (entrada == null || saida == null) {
            return 0.0;
        }

        Duration duracao = Duration.between(entrada, saida);
        long minutos = duracao.toMinutes();

        if (minutos <= 60) {
            return 10.0;
        }

        long horasExtras = (minutos - 60) / 60;

        if ((minutos - 60) % 60 != 0) {
            horasExtras++;
        }

        return 10.0 + (horasExtras * 10.0);
    }

    public String getVaga() {
        return vaga;
    }

    public void setVaga(String vaga) {
        this.vaga = vaga;
    }


    public String getTempoFormatado() {
        if (entrada == null || saida == null) {
            return "-";
        }

        Duration duracao = Duration.between(entrada, saida);
        long horas = duracao.toHours();
        long minutos = duracao.toMinutes() % 60;

        return horas + "h " + minutos + "min";
    }
}