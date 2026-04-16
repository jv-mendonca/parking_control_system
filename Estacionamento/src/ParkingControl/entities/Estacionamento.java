package ParkingControl.entities;

import java.time.Duration;
import java.time.LocalDateTime;

public class Estacionamento {
    private Cliente cliente;
    private Carro carro;
    private LocalDateTime entrada;
    private  LocalDateTime saida;
    private  int ticket;
    private Duration duracao;


    public Estacionamento(Cliente cliente, Carro carro, LocalDateTime entrada, LocalDateTime saida, int ticket) {
        this.ticket = ticket;
        this.cliente = cliente;
        this.carro = carro;
        this.entrada = entrada;
        this.saida = saida;

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
}
