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

        Mockito.when(contaContabilRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(getContaContabil()));

        Mockito.when(lancamentoContabilRepository.save(Mockito.any(LancamentoContabil.class)))
                .thenReturn(lancamentoContabil);

        String idLancamento = lancamentoContabilService.criarLancamentoContabil(getLancamentoContabilDTO());

        Assert.assertEquals(lancamentoContabil.getId(), idLancamento);

    }


    @Test(expected = ResourceNotFoundException.class)
    public void criarLancamentoContabilComContaContabilInexistenteDeveRetornarRessourceNotFound(){
        LancamentoContabil lancamentoContabil = getLancamentoContabil();

        Mockito.when(contaContabilRepository.findById(Mockito.anyInt()))
                .thenThrow(new ResourceNotFoundException("Conta contabil nao encontrada"));

        String idLancamento = lancamentoContabilService.criarLancamentoContabil(getLancamentoContabilDTO());

    }

    @Test
    public void buscarLancamentoContabilPorIdDeveRetornarLancamento(){
        LancamentoContabil lancamentoContabil = getLancamentoContabil();
        Mockito.when(lancamentoContabilRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(lancamentoContabil));

        LancamentoContabilDTO lancamentoEncontrado = lancamentoContabilService.buscarLancamentoContabilPorId(lancamentoContabil.getId());
        Assert.assertNotNull(lancamentoEncontrado);
    }
    

    @Test(expected = ResourceNotFoundException.class)
    public void buscarLancamentoPorIdInexistenteDeveRetornarResourceNotFoundException(){
        Mockito.when(lancamentoContabilRepository.findById(Mockito.anyString()))
                .thenThrow(new ResourceNotFoundException("Lancamento nao encontrado"));
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

        Mockito.when(contaContabilRepository.findById(contaContabil.getNumero()))
                .thenReturn(Optional.of(contaContabil));

        Mockito.when(lancamentoContabilRepository.findByContaContabil(contaContabil))
                .thenReturn(Arrays.asList(lancamentoContabil));

        List<LancamentoContabilDTO> lancamentoResp = this.lancamentoContabilService.buscarLancamentosContabeis(contaContabil.getNumero());


        Assert.assertEquals(lancamentoResp.get(0).getContaContabil(), contaContabil.getNumero());

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



        Mockito.when(lancamentoContabilRepository.findAll())
                .thenReturn(Arrays.asList(lancamentoContabil1, lancamentoContabil2, lancamentoContabil3));

        LancamentosSummaryDTO lancamentosSummaryDTO = lancamentoContabilService.buscarSumarizado(null);

        DoubleSummaryStatistics expectedSts = Arrays.asList(lancamentoContabil1, lancamentoContabil2, lancamentoContabil3)
                .stream().mapToDouble(l -> l.getValor())
                .summaryStatistics();

        Assert.assertEquals(
        new LancamentosSummaryDTO(
                APIUtils.format2Fraction(expectedSts.getSum()),
                APIUtils.format2Fraction(expectedSts.getMin()),
                APIUtils.format2Fraction(expectedSts.getMax()),
                APIUtils.format2Fraction(expectedSts.getAverage()),
                expectedSts.getCount()), lancamentosSummaryDTO );

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

        ContaContabil contaContabil = getContaContabil();

        Mockito.when(contaContabilRepository.findById(contaContabil.getNumero()))
                .thenReturn(Optional.of(contaContabil));


        Mockito.when(lancamentoContabilRepository.findByContaContabil(contaContabil))
                .thenReturn(Arrays.asList(lancamentoContabil1, lancamentoContabil2, lancamentoContabil3));

        LancamentosSummaryDTO lancamentosSummaryDTO = lancamentoContabilService.buscarSumarizado(contaContabil.getNumero());

        DoubleSummaryStatistics expectedSts = Arrays.asList(lancamentoContabil1, lancamentoContabil2, lancamentoContabil3)
                .stream().mapToDouble(l -> l.getValor())
                .summaryStatistics();

        Assert.assertEquals(
                new LancamentosSummaryDTO(
                        APIUtils.format2Fraction(expectedSts.getSum()),
                        APIUtils.format2Fraction(expectedSts.getMin()),
                        APIUtils.format2Fraction(expectedSts.getMax()),
                        APIUtils.format2Fraction(expectedSts.getAverage()),
                        expectedSts.getCount()), lancamentosSummaryDTO );

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
