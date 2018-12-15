package br.com.comexport.javachallenge.controller;

import br.com.comexport.javachallenge.dto.CriarLancamentoContabilResponse;
import br.com.comexport.javachallenge.dto.LancamentoContabilDTO;
import br.com.comexport.javachallenge.dto.LancamentosSummaryDTO;
import br.com.comexport.javachallenge.exceptions.ResourceAlreadyUpdatedException;
import br.com.comexport.javachallenge.service.ContaContabilService;
import br.com.comexport.javachallenge.service.LancamentoContabilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@RestController
public class LancamentoController {

    @Autowired
    private LancamentoContabilService lancamentoContabilService;

    @Autowired
    private ContaContabilService contaContabilService;

    @PostMapping("/lancamentos-contabeis")
    public ResponseEntity<CriarLancamentoContabilResponse> criarLancamento(@Valid @RequestBody LancamentoContabilDTO lancamentoContabilDTO) {
        log.info("Criando lancamento contábil {}", lancamentoContabilDTO);
        try{
            String id = lancamentoContabilService.criarLancamentoContabil(lancamentoContabilDTO);
            log.info("Lançamento contábil criado com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CriarLancamentoContabilResponse(id));
        } catch (Exception e){
            log.error("Erro criando lançamento contábil");
            throw e;
        }
    }


    @GetMapping("/lancamentos-contabeis/{id}")
    public ResponseEntity<LancamentoContabilDTO> buscarLancamentoCotabilPorId(@PathVariable final String id){

        try{
            log.info("Buscando lancamento contábil id: {} ", id);
            LancamentoContabilDTO lancamento = this.lancamentoContabilService.buscarLancamentoContabilPorId(id);
            log.info("Lancamento contábil encontrado: {} ",lancamento);

            return ResponseEntity.ok()
                    .eTag("\""+ lancamento.getVersion()+ "\"") //used to prevent two users update the same resource https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19
                    .body(lancamento);

        }catch (Exception e){
            log.error("Erro buscando lançamento contábil por id", e);
            throw  e;
        }
    }

    @GetMapping("/lancamentos-contabeis")
    public ResponseEntity<List<LancamentoContabilDTO>> buscarLancamentosContabeis(
            @RequestParam(value = "contaContabil", required = false)
            final Integer  contaContabil) {

        try{
            log.info("Buscando lancamentos contábeis");
            List<LancamentoContabilDTO> lancamentos = this.lancamentoContabilService.buscarLancamentosContabeis(contaContabil);
            log.info("Encontrados {} lançamentos", lancamentos.size());
            return ResponseEntity.ok(lancamentos);

        } catch (Exception e){
            log.error("Erro buscando lancamentos contábeis", e);
            throw e;
        }

    }

    @GetMapping("/lancamentos-contabeis/_stats")
    public ResponseEntity<LancamentosSummaryDTO> buscarSumarizado(@RequestParam(value = "contaContabil", required = false)
                                                               final Integer contaContabil){
        try{

            return ResponseEntity.ok(this.lancamentoContabilService.buscarSumarizado(contaContabil));

        } catch (Exception e){
            log.error("Erro buscando lançamentos sumarizados", e);
            throw e;
        }
    }


    @PutMapping("/lancamentos-contabeis/{id}")
    public ResponseEntity atualizarLancamentoContabil(WebRequest request,
                                                      @PathVariable("id") final String id,
                                                      @RequestBody final LancamentoContabilDTO lancamentoDTO) {

        try {

            LancamentoContabilDTO lancamentoContabil = lancamentoContabilService.buscarLancamentoContabilPorId(id);

            String ifMatchValue = request.getHeader("If-Match");
            if (isEmpty(ifMatchValue)) {
                log.error("If-Match request header value not found");
                return ResponseEntity.badRequest().build();
            }

            if (!ifMatchValue.equals("\"" + lancamentoContabil.getVersion() + "\"")) {
                log.error("Dectectada nova versão do lançamento");
                throw new ResourceAlreadyUpdatedException("Detectada nova versão do lançamento");
            }

            this.lancamentoContabilService.atualizarLancamento(lancamentoDTO, id);
            log.info("Lançamento atualizado com sucesso!");
            return  ResponseEntity.noContent().build();

        } catch (OptimisticLockingFailureException e) {
            log.error("Detectada nova versão do lancamento: {}", id);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Erro atualizando lançamento id: {}", id);
            throw e;
        }
    }

}
