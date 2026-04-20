package ParkingControl.util;

public class PlacaUtil {
    public static String normalizarPlaca(String placa) {
        return placa.toUpperCase().trim().replaceAll("[^A-Z0-9]", "");
    }

    public static boolean placaValida(String placa) {
        String p = normalizarPlaca(placa);

        if (p.matches("[A-Z]{3}[0-9]{4}")) return true;      // ABC1234
        if (p.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")) return true; // ABC1D23

        return false;
    }
}
