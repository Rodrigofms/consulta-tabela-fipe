package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoAPI;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner SC = new Scanner(System.in);

    private final ConsumoAPI CONSUMO = new ConsumoAPI();

    private final ConverteDados CONVERSOR = new ConverteDados();

    public void exibeMenu(){
        var menu = """
                \n
                *** OPCOES ***
                - Carro
                - Moto
                - Caminhao
                
                Digite uma das opcoes para consulta:
                """;

        System.out.println(menu);
        var opcao = SC.nextLine();
        String endereco;

        String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
        if(opcao.toLowerCase().contains("carr")) {
            endereco = URL_BASE + "carros/marcas";
        } else if(opcao.toLowerCase().contains("mot")){
            endereco = URL_BASE + "motos/marcas";
        } else{
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = CONSUMO.obterDados(endereco);

        var marcas = CONVERSOR.obterLista(json, Dados.class);

        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nInforme o codigo da marca para consulta: ");
        var codigoMarca = SC.nextLine();

        endereco += "/" + codigoMarca + "/modelos";
        json = CONSUMO.obterDados(endereco);
        var modeloLista = CONVERSOR.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");

        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro a ser buscado: ");

        var nomeVeiculo = SC.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\n Modelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("\nDigite o codigo do modelo para buscar os valores de avaliacao: ");
        var codigoModelo = SC.nextLine();

        endereco += "/" + codigoModelo + "/anos";

        json = CONSUMO.obterDados(endereco);

        List<Dados> anos = CONVERSOR.obterLista(json, Dados.class);

        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = CONSUMO.obterDados(enderecoAnos);
            Veiculo veiculo = CONVERSOR.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veiculos filtrados com avaliacoes por ano: ");
        veiculos.forEach(System.out::println);
    }
}
