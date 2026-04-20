package ParkingControl.ui;

import ParkingControl.service.EstacionamentoService;
import ParkingControl.entities.Carro;
import ParkingControl.util.PlacaUtil;

import java.util.Scanner;

public class Menu {

    private Scanner sc = new Scanner(System.in);
    private EstacionamentoService service = new EstacionamentoService();

    public void executar() {
        int opcao;

        do {
            mostrarMenu();
            opcao = sc.nextInt();
            sc.nextLine();

            switch (opcao) {
                case 1:
                    entrada();
                    break;
                case 2:
                    saida();
                    break;
                case 3:
                    System.out.println("Listagem ainda será implementada.");
                    break;
                case 4:
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao != 4);
    }

    private void mostrarMenu() {
        System.out.println("\n===== MENU =====");
        System.out.println("1 - Entrada de carro");
        System.out.println("2 - Saída de carro");
        System.out.println("3 - Listar");
        System.out.println("4 - Sair");
        System.out.print("Escolha: ");
    }

    private void entrada() {
        System.out.println("\n=== ENTRADA DE CARRO ===");

        System.out.print("Nome do cliente: ");
        String nome = sc.nextLine();

        System.out.print("Placa do carro: ");
        String placa = sc.nextLine();

        if (!PlacaUtil.placaValida(placa)) {
            System.out.println("Erro: placa inválida!");
            return;
        }

        String modelo;
        Carro carroExistente = service.buscarCarroExistente(placa);

        if (carroExistente == null) {
            System.out.print("Modelo do carro: ");
            modelo = sc.nextLine();
        } else {
            modelo = carroExistente.getModelo();
            System.out.println("Carro já cadastrado.");
            System.out.println("Modelo encontrado: " + modelo);
        }

        service.registrarEntrada(nome, placa, modelo);
    }

    private void saida() {
        System.out.print("Digite o ticket: ");
        int ticket = sc.nextInt();
        sc.nextLine();

        service.registrarSaida(ticket);
    }
}