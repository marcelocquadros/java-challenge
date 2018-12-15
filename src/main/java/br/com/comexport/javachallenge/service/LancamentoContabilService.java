package br.com.comexport.javachallenge.service;

import br.com.comexport.javachallenge.dto.LancamentoContabilDTO;
import br.com.comexport.javachallenge.dto.LancamentosSummaryDTO;
import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.entities.LancamentoContabil;
import br.com.comexport.javachallenge.exceptions.ResourceAlreadyExistsException;
import br.com.comexport.javachallenge.exceptions.ResourceNotFoundException;
import br.com.comexport.javachallenge.repository.ContaContabilRepository;
import br.com.comexport.javachallenge.repository.LancamentoContabilRepository;
import br.com.comexport.javachallenge.utils.APIUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class LancamentoContabilService {

    @Autowired
    private LancamentoContabilRepository lancamentoContabilRepository;

    @Autowired
    private ContaContabilRepository contaContabilRepository;


    public String criarLancamentoContabil(final LancamentoContabilDTO lancamentoContabilDTO){

        if(isNull(lancamentoContabilDTO)){
            log.error("lancamentoContabilDTO não pode ser nulo");
            throw new IllegalArgumentException("lancamentoContabilDTO não pode ser nulo");
        }
        log.info("Criando lancamamento contábil");

        try {
            log.info("Buscando contaContabil número {}", lancamentoContabilDTO.getContaContabil());

            ContaContabil contaContabil = contaContabilRepository.findById(lancamentoContabilDTO.getContaContabil())
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    String.format("Conta contábil núm: %s não encontrada", lancamentoContabilDTO.getContaContabil())));

            log.info("Conta contábil encontrada com sucesso");

            LancamentoContabil novoLancamento = new LancamentoContabil();

            novoLancamento.setContaContabil(contaContabil);
            novoLancamento.setVersion(lancamentoContabilDTO.getVersion());
            novoLancamento.setValor(lancamentoContabilDTO.getValor());
            novoLancamento.setData(lancamentoContabilDTO.getData());
            novoLancamento = lancamentoContabilRepository.save(novoLancamento);

            log.info("Lancamento salvo com sucesso!");
            return novoLancamento.getId();
        } catch (Exception e){
            log.error("Erro criando lançamento contábil", e);
            throw e;
        }
    }


    public LancamentoContabilDTO buscarLancamentoContabilPorId(String id) {

        try{
            if(isNull(id)){
                log.error("id não pode ser nulo");
                throw new IllegalArgumentException("id é nulo");
            }
            log.info("Buscando lancamento contábil id: ", id);
             LancamentoContabil entity = this.lancamentoContabilRepository.findById(id)
                     .orElseThrow(
                             ()-> new ResourceNotFoundException(String.format("Lançamento contábil %s não encontrado", id)));
            log.info("Lançamento contábil encontrado com sucesso!");

            return new LancamentoContabilDTO(entity.getContaContabil().getNumero(),
                                             entity.getData(),
                                             entity.getValor(),
                                             entity.getVersion());
        }catch (Exception e){
            log.error("Erro buscando lancamento contábil id: ", id);
            throw e;
        }
    }

    public List<LancamentoContabilDTO> buscarLancamentosContabeis(Integer contaContabil) {

        try{

            if(isNull(contaContabil)){
                log.info("Buscando estatisticas para todos lancamentos contábeis");
                return this.lancamentoContabilRepository.findAll()
                        .stream()
                        .map(l ->
                            new LancamentoContabilDTO(l.getContaContabil().getNumero(), l.getData(), l.getValor(),l.getVersion())
                        )
                       .collect(Collectors.toList());
            }

            log.info("Buscando estatisticas para lancamentos contábeis da conta  contábil núm. {}", contaContabil);
            //Testing contaContabil exists, if not found, an ResourceNotFound will be thrown
            ContaContabil contaContabilEntity = contaContabilRepository.findById(contaContabil)
                    .orElseThrow(
                            () -> new ResourceNotFoundException(
                                    String.format("Conta contábil núm: %s não encontrada",contaContabil)));


            return  this.lancamentoContabilRepository.findByContaContabil(contaContabilEntity)
                    .stream()
                    .map(l ->
                            new LancamentoContabilDTO(l.getContaContabil().getNumero(), l.getData(), l.getValor(),l.getVersion())
                    )
                    .collect(Collectors.toList());

        }catch (Exception e){
            log.error("Erro buscando lançamentos contáveis" , e);
            throw e;
        }
    }

    public LancamentosSummaryDTO buscarSumarizado(final Integer contaContabil) {

        try{
            log.info("Buscando estatisticas de lancamentos");
            DoubleSummaryStatistics sts = this.buscarLancamentosContabeis(contaContabil)
                    .stream()
                    .mapToDouble(l -> l.getValor())
                    .summaryStatistics();

            if(sts.getCount() == 0){
                log.warn("Nenhum lançamento encontrado");
                throw new ResourceNotFoundException("Não há lançamentos");
            }
            log.info("Estaticas para {} lançamentos", sts.getCount());
            log.debug("Estatisticas: {}", sts);
            return new LancamentosSummaryDTO(
                    APIUtils.format2Fraction(sts.getSum()),
                    APIUtils.format2Fraction(sts.getMin()),
                    APIUtils.format2Fraction(sts.getMax()),
                    APIUtils.format2Fraction(sts.getAverage()),
                    sts.getCount());

        }catch (Exception e){
            log.error("Erro buscando estatisticas de lançamentos", e);
            throw  e;
        }
    }

    public void atualizarLancamento(final LancamentoContabilDTO lancamentoDTO, String id) {

        try {
            log.info("Atualizando lancamento contábil id: {}", id);
            LancamentoContabil lancamentoContabil = lancamentoContabilRepository.findById(id)
                    .orElseThrow(
                            () -> new ResourceNotFoundException(String.format("Lançamento contabil id: %s não encontrado", id)));

            ContaContabil contaContabil = contaContabilRepository.findById(lancamentoDTO.getContaContabil())
                    .orElseThrow(
                            ()-> new ResourceNotFoundException(String.format("Conta contabil núm %s não encontrada", lancamentoDTO.getContaContabil())));

            lancamentoContabil.setData(lancamentoDTO.getData());
            lancamentoContabil.setValor(lancamentoDTO.getValor());
            lancamentoContabil.setVersion(lancamentoDTO.getVersion());
            lancamentoContabil.setContaContabil(contaContabil);

            lancamentoContabilRepository.save(lancamentoContabil);
            log.info("Lancamento contábil atualizado com sucesso");

        }catch (Exception e){
            log.error("Erro ao atualizar lançamento", e);
            throw e;
        }
    }
}
