package ParkingControl.repository;

import ParkingControl.entities.Estacionamento;

import java.io.*;
import java.util.ArrayList;

public class EstacionamentoRepository {
    private final String arquivoNome = "entrada.csv";

    public void salvarEntrada(Estacionamento est, String entradaFormatada) {
        File arquivo = new File(arquivoNome);

        try {
            boolean precisaCabecalho = !arquivo.exists() || arquivo.length() == 0;

            FileWriter writer = new FileWriter(arquivo, true);

            if (precisaCabecalho) {
                writer.append("Ticket;Nome;Modelo;Placa;Codigo;Vaga;Entrada;Saida;Diferenca;Valor a Pagar\n");
            }

            writer.append(String.valueOf(est.getTicket())).append(";");
            writer.append(est.getCliente().getNomeCliente()).append(";");
            writer.append(est.getCarro().getModelo()).append(";");
            writer.append(est.getCarro().getPlaca()).append(";");
            writer.append(String.valueOf(est.getCliente().getCodigoCliente())).append(";");
            writer.append(est.getVaga()).append(";");
            writer.append(entradaFormatada).append(";");
            writer.append("EM ABERTO").append(";");
            writer.append("-").append(";");
            writer.append("R$0.0").append("\n");

            writer.close();

        } catch (IOException e) {
            System.out.println("Erro ao salvar. Feche o Excel e tente novamente.");
        }
    }

    public void atualizarSaida(int ticketBusca, String saidaFormatada, String tempoFormatado, double valor) {
        File arquivo = new File(arquivoNome);
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
                        dados[6].trim().equalsIgnoreCase("EM ABERTO")) {

                    dados[7] = saidaFormatada;
                    dados[8] = tempoFormatado;
                    dados[9] = String.format("R$ %.2f", valor);

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

    public boolean existePlacaEmAberto(String placaNormalizada) {
        File arquivo = new File(arquivoNome);

        if (!arquivo.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");

                if (dados[0].equals("Ticket")) {
                    continue;
                }

                if (dados.length >= 7) {
                    String placaArquivo = dados[3].toUpperCase().trim().replaceAll("[^A-Z0-9]", "");
                    String saida = dados[6].trim();

                    if (placaArquivo.equals(placaNormalizada) && saida.equalsIgnoreCase("EM ABERTO")) {
                        return true;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao verificar placa no arquivo.");
        }

        return false;
    }

    public ArrayList<String[]> carregarLinhas() {
        ArrayList<String[]> dadosLista = new ArrayList<>();
        File arquivo = new File(arquivoNome);

        if (!arquivo.exists()) {
            return dadosLista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(";");

                if (dados[0].equals("Ticket")) {
                    continue;
                }

                if (dados.length >= 9) {
                    dadosLista.add(dados);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar dados do CSV.");
        }

        return dadosLista;
    }
}
