package ParkingControl.entities;



public class Carro {
    private String modelo;
    private String placa;

    public Carro(String modelo, String placa) {
        this.modelo = modelo;
        setPlaca(placa);
    }



    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {

        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        placa = placa.toUpperCase().trim();

        placa = placa.replaceAll("[^A-Z0-9]", "");

        if(placa.length() != 7){
            throw  new IllegalArgumentException("A placa deve ter exatamente 7 caracteres");
        }


        if (!placa.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) {
            throw new IllegalArgumentException("Placa inválida!");
        }
        this.placa = placa;
    }
}
