package ParkingControl.application;

import ParkingControl.entities.Carro;
import ParkingControl.entities.Cliente;
import ParkingControl.entities.Estacionamento;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;


public class Sistema {


    private Scanner sc = new Scanner(System.in);
    // Lista que guarda todos os veículos no sistema (memória)

    private ArrayList<Estacionamento> lista = new ArrayList<>();

    private  Random gerador = new Random();

    // Formato de data padrão Brasil
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Método principal do sistema (loop do menu)
    public void executar() {
        int opcao;

        do {
            mostrarMenu();
            opcao = sc.nextInt();
            sc.nextLine();

            tratarOpcao(opcao);

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

    private void tratarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                System.out.println("Teste");
                registrarEntrada();
                break;
            case 2:
                registrarSaida();
                break;
            case 3:
                //listar();
                break;
            case 4:
                System.out.println("Encerrando...");
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }

    // Cadastro de entrada de veículo
    private void registrarEntrada(){
        int ticket = gerador.nextInt(100000);
        System.out.print("Nome do cliente: ");
        String nome = sc.nextLine();

        System.out.print("Modelo do Carro: ");
        String modeloCarro = sc.nextLine();

        Carro carro = null;

        while (carro == null){
            try {
                System.out.println("Placa do carro: ");
                String placaCarro = sc.nextLine();

                if(placaJaExiste(placaCarro)){
                    System.out.println("Esta placa ja esta no estacionamento");
                    continue;
                }
                // Cria carro (validação acontece na classe Carro)
                 carro = new Carro(modeloCarro, placaCarro);
                break;
            } catch (IllegalArgumentException e){
                System.out.println(e.getMessage());

            }
        }

        int codigo = gerador.nextInt(100);

        Cliente cliente = new Cliente(nome,codigo);

        LocalDateTime entrada = LocalDateTime.now();
        // Saída começa como null (veículo ainda não saiu)
        Estacionamento estacionamento = new Estacionamento(cliente, carro, entrada, null, ticket);

        lista.add(estacionamento);

        // Exibe ticket
        System.out.println("Veiculo Registrado");
        System.out.println("=== TICKET GERADO ===");
        System.out.println("Ticket: " + ticket);
        System.out.println("Placa: " + carro.getPlaca());
        System.out.println("Entrada: " + entrada.format(formatter));


          // Salva no CSV
        salvarEntradaCSV(estacionamento);


    }

    // Normaliza placa (remove espaço, hífen, etc.)
    private String normalizarPlaca(String placa) {
        return placa.toUpperCase().trim().replaceAll("[^A-Z0-9]", "");
    }

    private boolean placaJaExiste(String placa) {
        String placaNormalizada = normalizarPlaca(placa);

        for (Estacionamento estacionamento : lista) {
            String placaLista = normalizarPlaca(estacionamento.getCarro().getPlaca());

            if (placaLista.equals(placaNormalizada) && estacionamento.getSaida() == null) {
                return true;
            }
        }
        return false;
    }


    // Registro de saída
    private void registrarSaida() {
        System.out.print("Digite o ticket: ");
        int ticket = sc.nextInt();
        sc.nextLine();

        for (Estacionamento est : lista) {
            if (est.getTicket() == ticket && est.getSaida() == null) {
                est.setSaida(LocalDateTime.now());
                atualizarSaidaCSV(ticket);
                System.out.println("Saída registrada com sucesso!");
                return;
            }
        }

        System.out.println("Ticket não encontrado.");
    }




    // Atualiza saída no arquivo CSV
    private void atualizarSaidaCSV(int ticketBusca) {
        File arquivo = new File("entrada.csv");
        ArrayList<String> linhas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");

                if (dados[0].equals("Ticket")) {
                    linhas.add(linha);
                    continue;
                }

                if (dados.length >= 8 &&
                        Integer.parseInt(dados[0]) == ticketBusca &&
                        dados[6].equals("EM ABERTO")) {


                    LocalDateTime saida = LocalDateTime.now();

                    LocalDateTime entrada = LocalDateTime.parse(dados[5], formatter);

                    Duration duracao = Duration.between(entrada,saida);

                    long horas = duracao.toHours();
                    long minutos = duracao.toMinutes() % 60;

                    dados[6] = LocalDateTime.now().format(formatter);
                    dados[7] = horas + "h " + minutos + "min";

                    linha = String.join(";", dados);

                }

                linhas.add(linha);
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo.");
            return;
        }

        try (FileWriter writer = new FileWriter(arquivo, false)) {
            for (String l : linhas) {
                writer.write(l + "\n");
            }
        } catch (IOException e) {
            System.out.println("Feche o Excel antes de atualizar.");
        }
    }






    private void salvarEntradaCSV(Estacionamento est) {
        File arquivo = new File("entrada.csv");

        try {
            boolean precisaCabecalho = !arquivo.exists() || arquivo.length() == 0;

            FileWriter writer = new FileWriter(arquivo, true);

            if (precisaCabecalho) {
                writer.append("Ticket;Nome;Modelo;Placa;Codigo;Entrada;Saida;Diferença\n");
            }

            writer.append(String.valueOf(est.getTicket())).append(";");
            writer.append(est.getCliente().getNomeCliente()).append(";");
            writer.append(est.getCarro().getModelo()).append(";");
            writer.append(est.getCarro().getPlaca()).append(";");
            writer.append(String.valueOf(est.getCliente().getCodigoCliente())).append(";");
            writer.append(est.getEntrada().format(formatter)).append(";");
            writer.append("EM ABERTO").append(";");
            writer.append("-").append("\n");

            writer.close();

        } catch (IOException e) {
            System.out.println("Erro ao salvar. Feche o Excel e tente novamente.");
        }
    }

}
