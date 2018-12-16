package br.com.comexport.javachallenge.lancamentoContabil;

import br.com.comexport.javachallenge.dto.LancamentoContabilDTO;
import br.com.comexport.javachallenge.dto.LancamentosSummaryDTO;
import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.entities.LancamentoContabil;
import br.com.comexport.javachallenge.exceptions.ResourceNotFoundException;
import br.com.comexport.javachallenge.repository.ContaContabilRepository;
import br.com.comexport.javachallenge.repository.LancamentoContabilRepository;
import br.com.comexport.javachallenge.service.LancamentoContabilService;
import br.com.comexport.javachallenge.utils.APIUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.spy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LancamentoContabilServiceTests {

    @Autowired
    private LancamentoContabilService lancamentoContabilService;

    @MockBean
    private LancamentoContabilRepository lancamentoContabilRepository;

    @MockBean
    private ContaContabilRepository contaContabilRepository;

    @Test
    public void criarLancamentoContabilDeveRetornarIdLancamento(){
        LancamentoContabil lancamentoContabil = getLancamentoContabil();

        given(contaContabilRepository.findById(Mockito.anyInt()))
                .willReturn(Optional.of(getContaContabil()));

        given(lancamentoContabilRepository.save(Mockito.any(LancamentoContabil.class)))
                .willReturn(lancamentoContabil);

        String idLancamento = lancamentoContabilService.criarLancamentoContabil(getLancamentoContabilDTO());

        Assert.assertEquals(lancamentoContabil.getId(), idLancamento);

    }


    @Test(expected = ResourceNotFoundException.class)
    public void criarLancamentoContabilComContaContabilInexistenteDeveRetornarRessourceNotFound(){
        LancamentoContabil lancamentoContabil = getLancamentoContabil();

        given(contaContabilRepository.findById(Mockito.anyInt()))
                .willThrow(new ResourceNotFoundException("Conta contabil nao encontrada"));

        lancamentoContabilService.criarLancamentoContabil(getLancamentoContabilDTO());

    }

    @Test
    public void buscarLancamentoContabilPorIdDeveRetornarLancamento(){
        LancamentoContabil lancamentoContabil = getLancamentoContabil();
        given(lancamentoContabilRepository.findById(Mockito.anyString()))
                .willReturn(Optional.of(lancamentoContabil));

        LancamentoContabilDTO lancamentoEncontrado = lancamentoContabilService.buscarLancamentoContabilPorId(lancamentoContabil.getId());
        Assert.assertNotNull(lancamentoEncontrado);
    }


    @Test(expected = ResourceNotFoundException.class)
    public void buscarLancamentoPorIdInexistenteDeveRetornarResourceNotFoundException(){
        given(lancamentoContabilRepository.findById(Mockito.anyString()))
                .willThrow(new ResourceNotFoundException("Lancamento nao encontrado"));
        this.lancamentoContabilService.buscarLancamentoContabilPorId("xxx");
    }

    @Test
    public void buscarLancamentosContabeisDeveRetornarLancamentos(){
        LancamentoContabil lancamentoContabil1 = getLancamentoContabil();
        LancamentoContabil lancamentoContabil2 = getLancamentoContabil();

        Mockito.when(lancamentoContabilRepository.findAll())
                .thenReturn(Arrays.asList(lancamentoContabil1, lancamentoContabil2));

        long qtde = this.lancamentoContabilService.buscarLancamentosContabeis(null)
                .stream().count();

        Assert.assertEquals(qtde, 2);

    }

    @Test
    public void buscarLancamentosContabeisPorContaContabilDeveRetornarLancamentos(){
        LancamentoContabil lancamentoContabil = getLancamentoContabil();
        ContaContabil contaContabil = getContaContabil();

        given(contaContabilRepository.findById(contaContabil.getNumero()))
                .willReturn(Optional.of(contaContabil));

        given(lancamentoContabilRepository.findByContaContabil(contaContabil))
                .willReturn(Arrays.asList(lancamentoContabil));

        List<LancamentoContabilDTO> lancamentos = this.lancamentoContabilService.buscarLancamentosContabeis(contaContabil.getNumero());

        Assert.assertEquals(lancamentos.get(0).getContaContabil(), contaContabil.getNumero());

    }

    @Test
    public void buscarSumarizadoSemContaContabilDeveRetornarEstatiscasCorretas(){
        LancamentoContabil lancamentoContabil1 = getLancamentoContabil();
        lancamentoContabil1.setValor(10.0);
        lancamentoContabil1.setId("a");
        LancamentoContabil lancamentoContabil2 = getLancamentoContabil();
        lancamentoContabil2.setValor(20.0);
        lancamentoContabil2.setId("b");
        LancamentoContabil lancamentoContabil3 = getLancamentoContabil();
        lancamentoContabil3.setValor(30.00);
        lancamentoContabil3.setId("c");

        List<LancamentoContabil> lancamentos = Arrays.asList(lancamentoContabil1, lancamentoContabil2, lancamentoContabil3);

        given(lancamentoContabilRepository.findAll()).willReturn(lancamentos);

        LancamentosSummaryDTO lancamentosSummaryDTO = lancamentoContabilService.buscarSumarizado(null);

        DoubleSummaryStatistics expectedSts = lancamentos.stream()
                .mapToDouble(l -> l.getValor())
                .summaryStatistics();

        Assert.assertEquals(
                new LancamentosSummaryDTO(
                        expectedSts.getSum(),
                        expectedSts.getMin(),
                        expectedSts.getMax(),
                        expectedSts.getAverage(),
                        expectedSts.getCount()
                ),
                lancamentosSummaryDTO
        );

    }

    @Test
    public void buscarSumarizadoPorContaContabilDeveRetornarEstatiscasCorretas(){
        LancamentoContabil lancamentoContabil1 = getLancamentoContabil();
        lancamentoContabil1.setValor(10.0);
        lancamentoContabil1.setId("a");
        LancamentoContabil lancamentoContabil2 = getLancamentoContabil();
        lancamentoContabil2.setValor(20.0);
        lancamentoContabil2.setId("b");
        LancamentoContabil lancamentoContabil3 = getLancamentoContabil();
        lancamentoContabil3.setValor(30.00);
        lancamentoContabil3.setId("c");

        List<LancamentoContabil> lancamentos = Arrays.asList(lancamentoContabil1, lancamentoContabil2, lancamentoContabil3);
        ContaContabil contaContabil = getContaContabil();

        given(contaContabilRepository.findById(contaContabil.getNumero()))
                .willReturn(Optional.of(contaContabil));


        Mockito.when(lancamentoContabilRepository.findByContaContabil(contaContabil))
                .thenReturn(lancamentos);

        LancamentosSummaryDTO lancamentosSummaryDTO = lancamentoContabilService.buscarSumarizado(contaContabil.getNumero());

        DoubleSummaryStatistics expectedSts = lancamentos.stream()
                .mapToDouble(l -> l.getValor())
                .summaryStatistics();

        Assert.assertEquals(
                new LancamentosSummaryDTO(
                        expectedSts.getSum(),
                        expectedSts.getMin(),
                        expectedSts.getMax(),
                        expectedSts.getAverage(),
                        expectedSts.getCount()
                ),
                lancamentosSummaryDTO
        );

    }

    private LancamentoContabil getLancamentoContabil(){
        LancamentoContabil lancamento = new LancamentoContabil();
        lancamento.setId(UUID.randomUUID().toString());
        lancamento.setValor(10.00);
        lancamento.setData(20181010);
        lancamento.setContaContabil(getContaContabil());
        return lancamento;
    }

    private ContaContabil getContaContabil(){
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setDescricao("Conta test");
        contaContabil.setNumero(1010);
        return contaContabil;
    }

    private LancamentoContabilDTO getLancamentoContabilDTO(){
        LancamentoContabilDTO lancamento = new LancamentoContabilDTO();
        lancamento.setValor(10.00);
        lancamento.setData(20181010);
        lancamento.setContaContabil(1010);
        return lancamento;
    }
}
