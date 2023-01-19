package com.github.thiago.msavaliadorcredito.api;

import com.github.thiago.msavaliadorcredito.domain.model.DadosAvaliacao;
import com.github.thiago.msavaliadorcredito.domain.model.RetornoAvaliacaoCliente;
import com.github.thiago.msavaliadorcredito.domain.model.SituacaoCliente;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("avaliacoes-credito")
@RequiredArgsConstructor
public class AvaliadorCreditoController {

    private final AvaliadorCreditoService avaliadorCreditoService;

    @GetMapping
    public String status(){
        return "Success Avaliador Credito";
    }

    @GetMapping(value = "situacao-cliente", params = "cpf")
    public ResponseEntity<SituacaoCliente> consultaSituacaoCliente(@RequestParam("cpf") String cpf){

        SituacaoCliente situacaoCliente = avaliadorCreditoService.obterSituacaoCliente(cpf);

        return ResponseEntity.ok(situacaoCliente);
    }

    @PostMapping
    public ResponseEntity realizarAvaliacao(@RequestBody DadosAvaliacao dados){
        RetornoAvaliacaoCliente retornoAvaliacaoCliente = avaliadorCreditoService.realizarAvaliacao(dados.getCpf(), dados.getRenda());

        return ResponseEntity.ok(retornoAvaliacaoCliente);

    }
}
