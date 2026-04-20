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
                System.out.print("Placa do carro: ");
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
        exibirTicket(estacionamento);


        // Salva no CSV
        salvarEntradaCSV(estacionamento);


    }

    // Normaliza placa (remove espaço, hífen, etc.)
    private String normalizarPlaca(String placa) {
        return placa.toUpperCase().trim().replaceAll("[^A-Z0-9]", "");
    }

    private boolean placaJaExiste(String placa) {
        String placaNormalizada = normalizarPlaca(placa);

        // verifica na lista em memória
        for (Estacionamento estacionamento : lista) {
            String placaLista = normalizarPlaca(estacionamento.getCarro().getPlaca());

            if (placaLista.equals(placaNormalizada) && estacionamento.getSaida() == null) {
                return true;
            }
        }

        // verifica também no CSV
        File arquivo = new File("entrada.csv");

        if (arquivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
                String linha;

                while ((linha = br.readLine()) != null) {
                    String[] dados = linha.split(";");

                    // pula cabeçalho
                    if (dados[0].equals("Ticket")) {
                        continue;
                    }

                    if (dados.length >= 7) {
                        String placaArquivo = normalizarPlaca(dados[3]);
                        String saida = dados[6];

                        // só bloqueia se a mesma placa estiver EM ABERTO
                        if (placaArquivo.equals(placaNormalizada) && saida.equalsIgnoreCase("EM ABERTO")) {
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Erro ao verificar placa no arquivo.");
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
                atualizarSaidaCSV(ticket,est);
                System.out.println("Saída registrada com sucesso!");
                return;
            }
        }
        if(darBaixaTicketNoCSV(ticket)){
            System.out.println("Saida registrada com sucesso!");
        }else {
            System.out.println("ticket nao encontrado");
        }

    }

    private boolean darBaixaTicketNoCSV(int ticketBusca) {
        File arquivo = new File("entrada.csv");
        ArrayList<String> linhas = new ArrayList<>();

        if (!arquivo.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            boolean encontrado = false;

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");

                // mantém cabeçalho
                if (dados[0].equals("Ticket")) {
                    linhas.add(linha);
                    continue;
                }

                if (dados.length >= 9 &&
                        Integer.parseInt(dados[0]) == ticketBusca &&
                        dados[6].equals("EM ABERTO")) {

                    LocalDateTime entrada = LocalDateTime.parse(dados[5], formatter);
                    LocalDateTime saida = LocalDateTime.now();

                    Duration duracao = Duration.between(entrada, saida);

                    Estacionamento estTemp = new Estacionamento(null, null, entrada, saida, ticketBusca);
                    estTemp.getTempoFormatado();
                    dados[6] = saida.format(formatter);
                    dados[7] = estTemp.getTempoFormatado();
                    dados[8] = String.format("R$ %.2f", estTemp.getValor());

                    linha = String.join(";", dados);
                    encontrado = true;
                }

                linhas.add(linha);
            }

            if (!encontrado) {
                return false;
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo.");
            return false;
        }

        try (FileWriter writer = new FileWriter(arquivo, false)) {
            for (String l : linhas) {
                writer.write(l + "\n");
            }
        } catch (IOException e) {
            System.out.println("Feche o Excel antes de atualizar.");
            return false;
        }

        return true;
    }



    private void exibirTicket(Estacionamento estacionamento){
        System.out.println();
        System.out.println("====================================");
        System.out.println("         TICKET DE ENTRADA         ");
        System.out.println("====================================");
        System.out.println("Ticket : " + estacionamento.getTicket());
        System.out.println("Cliente: " + estacionamento.getCliente().getNomeCliente());
        System.out.println("Código : " + estacionamento.getCliente().getCodigoCliente());
        System.out.println("Carro  : " + estacionamento.getCarro().getModelo());
        System.out.println("Placa  : " + estacionamento.getCarro().getPlaca());
        System.out.println("Entrada: " + estacionamento.getEntrada().format(formatter));
        System.out.println("Status : EM ABERTO");
        System.out.println("====================================");
        System.out.println("Guarde este ticket para registrar a saída.");
        System.out.println();

    }




    // Atualiza saída no arquivo CSV
    private void atualizarSaidaCSV(int ticketBusca, Estacionamento estAtualizado) {
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

                if (dados.length >= 9 &&
                        Integer.parseInt(dados[0]) == ticketBusca &&
                        dados[6].equals("EM ABERTO")) {

                    dados[6] = estAtualizado.getSaida().format(formatter);
                    dados[7] = estAtualizado.getTempoFormatado();
                    dados[8] = String.format("R$ %.2f", estAtualizado.getValor());


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
                writer.append("Ticket;Nome;Modelo;Placa;Codigo;Entrada;Saida;Diferenca;Valor a Pagar\n");
            }

            writer.append(String.valueOf(est.getTicket())).append(";");
            writer.append(est.getCliente().getNomeCliente()).append(";");
            writer.append(est.getCarro().getModelo()).append(";");
            writer.append(est.getCarro().getPlaca()).append(";");
            writer.append(String.valueOf(est.getCliente().getCodigoCliente())).append(";");
            writer.append(est.getEntrada().format(formatter)).append(";");
            writer.append("EM ABERTO").append(";");
            writer.append("-").append(";");
            writer.append("R$0.0").append("\n");

            writer.close();

        } catch (IOException e) {
            System.out.println("Erro ao salvar. Feche o Excel e tente novamente.");
        }
    }

}
