package ParkingControl.entities;

public class Cliente {
    private String nomeCliente;
    private int codigoCliente;

    public Cliente(String nomeCliente, int codigoCliente) {
        this.nomeCliente = nomeCliente;
        this.codigoCliente = codigoCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }
}
