package br.com.comexport.javachallenge.controller;

import br.com.comexport.javachallenge.dto.CriarContaContabilResponse;
import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.service.ContaContabilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
public class ContaContabilController {

    @Autowired
    private ContaContabilService contaContabilService;

    @PostMapping("/conta-contabil")
    public ResponseEntity<CriarContaContabilResponse> criarContaContabil(
            @Valid @RequestBody final ContaContabil contaContabil){

        try {
            log.info("Criando conta contábil {}",contaContabil);
            Integer idContaContabil = contaContabilService.criarContaContabil(contaContabil);
            log.info("Conta contábil criada com sucesso!");

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CriarContaContabilResponse(idContaContabil));

            //return ResponseEntity.created(URI).build() --> Best practice!!!
        } catch (Exception e){
            log.error("Erro criando conta contábil {}", e);
            throw e;
        }

    }

    @GetMapping("/conta-contabil/{id}")
    public ResponseEntity<ContaContabil> buscarPorId(@PathVariable("id") final Integer id){
        try {
            log.info("Buscando conta contábil id: {}", id);
            ContaContabil contaContabil = this.contaContabilService.buscarPorId(id);
            log.info("Conta contábil encontrada com sucesso! {}", contaContabil);
            return ResponseEntity
                    .ok()
                    .eTag("\""+ contaContabil.getVersion()+ "\"") //used to prevent two users update the same register at the same moment
                    .body(contaContabil);
        } catch (Exception e){
            log.error("Erro buscando conta núm. {} ", id);
            throw e;
        }
    }

}
