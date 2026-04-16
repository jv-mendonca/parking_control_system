package ParkingControl.application;




import java.util.Locale;
import java.util.Scanner;

public class Main {
    static void main() {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        Sistema sistema = new Sistema();
        sistema.executar();


    }


}





















