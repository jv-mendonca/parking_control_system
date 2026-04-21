package ParkingControl.service;

import ParkingControl.entities.Carro;
import ParkingControl.entities.Cliente;
import ParkingControl.entities.Estacionamento;
import ParkingControl.repository.EstacionamentoRepository;
import ParkingControl.util.PlacaUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class EstacionamentoService {

    private ArrayList<Estacionamento> lista = new ArrayList<>();
    private ArrayList<Carro> carrosCadastrados = new ArrayList<>();
    private Random gerador = new Random();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private EstacionamentoRepository repository = new EstacionamentoRepository();


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


    public EstacionamentoService() {
        carregarDadosDoCSV();
    }

    public void registrarEntrada(String nome, String placaCarro, String modeloInformado) {
        int codigo = gerador.nextInt(9000) + 1000;


        if (!PlacaUtil.placaValida(placaCarro)) {
            System.out.println("Erro: placa inválida!");
            return;
        }

        if (placaJaExiste(placaCarro)) {
            System.out.println("Erro: já existe um veículo com essa placa estacionado.");
            return;
        }

        Carro carro = buscarCarroPorPlaca(placaCarro);

        if (carro == null) {
            carro = new Carro(modeloInformado, placaCarro);
            carrosCadastrados.add(carro);
            System.out.println("Carro cadastrado com sucesso.");
            System.out.println("Código do cliente: " + codigo);
        } else {
            System.out.println("Carro já cadastrado no sistema.");
            System.out.println("Modelo: " + carro.getModelo());
            System.out.println("Placa: " + carro.getPlaca());
        }

        Cliente cliente = new Cliente(nome, codigo);
        int ticket = gerador.nextInt(9000) + 1000;
        LocalDateTime entrada = LocalDateTime.now();
        String vaga = gerarVaga();


        Estacionamento est = new Estacionamento(cliente, carro, entrada, null, ticket, vaga);
        lista.add(est);

        repository.salvarEntrada(est, entrada.format(formatter));

        System.out.println("Entrada registrada com sucesso!");
        exibirTicket(est);
    }

    private String gerarVaga() {
        String vaga;
        do {
            char letra = (char) ('A' + gerador.nextInt(5));
            int numero = gerador.nextInt(20) + 1;
            vaga = letra  + "-" + numero;
        } while (vagaOcupada(vaga));

        return vaga;
    }

    private boolean vagaOcupada(String vaga) {
        for (Estacionamento est : lista) {
            if (est.getSaida() == null && est.getVaga().equalsIgnoreCase(vaga)) {
                return true;
            }
        }
        return false;
    }

    public void registrarSaida(int ticket) {
        for (Estacionamento est : lista) {
            if (est.getTicket() == ticket && est.getSaida() == null) {
                est.setSaida(LocalDateTime.now());

                repository.atualizarSaida(
                        ticket,
                        est.getSaida().format(formatter),
                        est.getTempoFormatado(),
                        est.getValor()
                );

                System.out.println("Saída registrada com sucesso!");
                return;
            }
        }

        System.out.println("Ticket não encontrado.");
    }

    private boolean placaJaExiste(String placa) {
        String placaNormalizada = PlacaUtil.normalizarPlaca(placa);

        for (Estacionamento estacionamento : lista) {
            String placaLista = PlacaUtil.normalizarPlaca(estacionamento.getCarro().getPlaca());

            if (placaLista.equals(placaNormalizada) && estacionamento.getSaida() == null) {
                return true;
            }
        }

        return repository.existePlacaEmAberto(placaNormalizada);
    }

    private Carro buscarCarroPorPlaca(String placa) {
        String placaNormalizada = PlacaUtil.normalizarPlaca(placa);

        for (Carro carro : carrosCadastrados) {
            String placaCarro = PlacaUtil.normalizarPlaca(carro.getPlaca());

            if (placaCarro.equals(placaNormalizada)) {
                return carro;
            }
        }
        return null;
    }

    private void carregarDadosDoCSV() {
        ArrayList<String[]> dadosLista = repository.carregarLinhas();

        for (String[] dados : dadosLista) {
            try {
                int ticket = Integer.parseInt(dados[IDX_TICKET].trim());
                String nome = dados[IDX_NOME].trim();
                String modelo = dados[IDX_MODELO].trim();
                String placa = dados[IDX_PLACA].trim();
                int codigo = Integer.parseInt(dados[IDX_CODIGO].trim());
                String vaga = dados[IDX_VAGA].trim();

                LocalDateTime entrada = LocalDateTime.parse(
                        dados[IDX_ENTRADA].trim(), formatter
                );

                LocalDateTime saida = null;
                if (!dados[IDX_SAIDA].trim().equalsIgnoreCase("EM ABERTO")) {
                    saida = LocalDateTime.parse(
                            dados[IDX_SAIDA].trim(), formatter
                    );
                }

                // reutiliza carro já cadastrado
                Carro carro = buscarCarroPorPlaca(placa);
                if (carro == null) {
                    carro = new Carro(modelo, placa);
                    carrosCadastrados.add(carro);
                }

                Cliente cliente = new Cliente(nome, codigo);

                Estacionamento est = new Estacionamento(
                        cliente,
                        carro,
                        entrada,
                        saida,
                        ticket,
                        vaga
                );

                lista.add(est);

            } catch (Exception e) {
                System.out.println("Erro ao carregar linha do CSV.");
            }
        }
    }

    private void exibirTicket(Estacionamento estacionamento) {
        System.out.println();
        System.out.println("====================================");
        System.out.println("         TICKET DE ENTRADA         ");
        System.out.println("====================================");
        System.out.println("Ticket : " + estacionamento.getTicket());
        System.out.println("Cliente: " + estacionamento.getCliente().getNomeCliente());
        System.out.println("Código : " + estacionamento.getCliente().getCodigoCliente());
        System.out.println("Carro  : " + estacionamento.getCarro().getModelo());
        System.out.println("Placa  : " + estacionamento.getCarro().getPlaca());
        System.out.println("Vaga   : " + estacionamento.getVaga());
        System.out.println("Entrada: " + estacionamento.getEntrada().format(formatter));
        System.out.println("Status : EM ABERTO");
        System.out.println("====================================");
        System.out.println("Guarde este ticket para registrar a saída.");
        System.out.println();
    }


    public Carro buscarCarroExistente(String placa) {
        return buscarCarroPorPlaca(placa);
    }
}