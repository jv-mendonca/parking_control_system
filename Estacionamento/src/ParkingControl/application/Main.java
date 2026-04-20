package ParkingControl.application;




import ParkingControl.service.EstacionamentoService;
import ParkingControl.ui.Menu;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    static void main() {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(System.in);

        Menu menu = new Menu();
        menu.executar();


    }


}





















