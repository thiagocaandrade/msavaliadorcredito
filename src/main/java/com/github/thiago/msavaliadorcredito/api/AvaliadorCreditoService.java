package com.github.thiago.msavaliadorcredito.api;

import com.github.thiago.msavaliadorcredito.domain.model.*;
import com.github.thiago.msavaliadorcredito.infra.client.CartoesClient;
import com.github.thiago.msavaliadorcredito.infra.client.ClienteClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteClient clienteClient;
    private final CartoesClient cartoesClient;

    public SituacaoCliente obterSituacaoCliente(String cpf){

        ResponseEntity<DadosCliente> dadosClienteResponse = clienteClient.dadosClientes(cpf);
        ResponseEntity<List<CartaoCliente>> cartoesClienteResponse = cartoesClient.getCartoesByClientes(cpf);

        return SituacaoCliente
                .builder()
                .cliente(dadosClienteResponse.getBody())
                .cartoes(cartoesClienteResponse.getBody())
                .build();
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda){

        ResponseEntity<DadosCliente> dadosClienteResponse = clienteClient.dadosClientes(cpf);
        ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAteh(renda);
        
        List<Cartao> cartoes = cartoesResponse.getBody();
        List<CartaoAprovado> listaCartoesAprovados = cartoes.stream().map(cartao -> {
            DadosCliente dadosCliente = dadosClienteResponse.getBody();
            BigDecimal limiteBasico = cartao.getLimiteBasico();
            BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
            BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));
            BigDecimal limiteAprovado = fator.multiply(limiteBasico);

            CartaoAprovado aprovado = new CartaoAprovado();
            aprovado.setCartao(cartao.getNome());
            aprovado.setBandeira(cartao.getBandeira());
            aprovado.setLimiteAprovado(limiteAprovado);

            return aprovado;
        }).collect(Collectors.toList());

        return new RetornoAvaliacaoCliente(listaCartoesAprovados);
    }
}
