package ParkingControl.repository;

import ParkingControl.entities.Estacionamento;

import java.io.*;
import java.util.ArrayList;

public class EstacionamentoRepository {

    private final String arquivoNome = "entrada.csv";

    private static final int IDX_TICKET = 0;
    private static final int IDX_NOME = 1;
    private static final int IDX_MODELO = 2;
    private static final int IDX_PLACA = 3;
    private static final int IDX_CODIGO = 4;
    private static final int IDX_VAGA = 5;
    private static final int IDX_ENTRADA = 6;
    private static final int IDX_SAIDA = 7;
    private static final int IDX_DIFERENCA = 8;
    private static final int IDX_VALOR = 9;

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

                if (dados[IDX_TICKET].trim().equals("Ticket")) {
                    linhas.add(linha);
                    continue;
                }

                if (dados.length >= 10 &&
                        Integer.parseInt(dados[IDX_TICKET].trim()) == ticketBusca &&
                        dados[IDX_SAIDA].trim().equalsIgnoreCase("EM ABERTO")) {

                    dados[IDX_SAIDA] = saidaFormatada;
                    dados[IDX_DIFERENCA] = tempoFormatado;
                    dados[IDX_VALOR] = String.format("R$ %.2f", valor);

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

                if (dados[IDX_TICKET].trim().equals("Ticket")) {
                    continue;
                }

                if (dados.length >= 10) {
                    String placaArquivo = dados[IDX_PLACA].trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
                    String saida = dados[IDX_SAIDA].trim();

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

                if (dados[IDX_TICKET].trim().equals("Ticket")) {
                    continue;
                }

                if (dados.length >= 10) {
                    for (int i = 0; i < dados.length; i++) {
                        dados[i] = dados[i].trim();
                    }
                    dadosLista.add(dados);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar dados do CSV.");
        }

        return dadosLista;
    }
}