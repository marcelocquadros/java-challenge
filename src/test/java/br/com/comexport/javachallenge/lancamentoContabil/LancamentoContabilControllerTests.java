package br.com.comexport.javachallenge.lancamentoContabil;

import br.com.comexport.javachallenge.dto.LancamentoContabilDTO;
import br.com.comexport.javachallenge.dto.LancamentosSummaryDTO;
import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.entities.LancamentoContabil;
import br.com.comexport.javachallenge.exceptions.ResourceNotFoundException;
import br.com.comexport.javachallenge.service.ContaContabilService;
import br.com.comexport.javachallenge.service.LancamentoContabilService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LancamentoContabilControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LancamentoContabilService lancamentoContabilService;

    @MockBean
    private ContaContabilService contaContabilService;

    @Test
    public void criarLancamentoContabilDeveRetornarStatus201() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();

        String URI = "/lancamentos-contabeis";

        String idLancamento = UUID.randomUUID().toString();
        given(lancamentoContabilService.criarLancamentoContabil(Mockito.any(LancamentoContabilDTO.class)))
                .willReturn(idLancamento);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

         mockMvc.perform(requestBuilder)
                 .andExpect(status().isCreated())
                 .andExpect(jsonPath("$.id", is(idLancamento)));
    }

    @Test
    public void criarLancamentoContabilComContaContabilInexistenteDeveRetornar404() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();

        String URI = "/lancamentos-contabeis";

        Mockito.when(lancamentoContabilService.criarLancamentoContabil(Mockito.any(LancamentoContabilDTO.class)))
                .thenThrow(new ResourceNotFoundException("Conta contabil inexistente"));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    public void criarLancamentoContabilComDataFormatoInvalidoDeveRetornaBadRequest() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        request.setData(20181301); //should be yyyyMMdd

        String URI = "/lancamentos-contabeis";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void criarLancamentoContabilComValorNuloDeveRetornaBadRequest() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        request.setData(20181001);
        request.setValor(null);

        String URI = "/lancamentos-contabeis";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void criarLancamentoContabilComContaContabilNulaDeveRetornaBadRequest() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        request.setContaContabil(null);

        String URI = "/lancamentos-contabeis";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void buscarLancamentoContabilPorIdDeveRetornarLancamento() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();

        String URI = "/lancamentos-contabeis/1234";

        Mockito.when(lancamentoContabilService.buscarLancamentoContabilPorId("1234"))
                .thenReturn(request);

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(request)));

    }

    @Test
    public void buscarLancamentoContabilPorIdInexistenteDeveRetornar404() throws Exception {

        String URI = "/lancamentos-contabeis/90909090";

        Mockito.when(lancamentoContabilService.buscarLancamentoContabilPorId("90909090"))
                .thenThrow(new ResourceNotFoundException("Lancamento não encontrado"));

        mockMvc.perform(get(URI))
                .andExpect(status().isNotFound());

    }

    @Test
    public void buscarSumarizadoSemContaDeveRetornarEstaticasParaTodosLancamentos() throws Exception {

        LancamentosSummaryDTO request = getLancamentosSummaryDTO();

        Mockito.when(lancamentoContabilService.buscarSumarizado(null))
                .thenReturn(getLancamentosSummaryDTO());

        String URI = "/lancamentos-contabeis/_stats";

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(request)));

    }

    @Test
    public void buscarSumarizadoPorContaContabilDeveRetornarEstaticas() throws Exception {

        LancamentosSummaryDTO request = getLancamentosSummaryDTO();
        ContaContabil contaContabil = getContaContabil();

        Mockito.when(lancamentoContabilService.buscarSumarizado(contaContabil.getNumero()))
                .thenReturn(getLancamentosSummaryDTO());

        String URI = "/lancamentos-contabeis/_stats/?contaContabil="+contaContabil.getNumero();

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(request)));

    }

    public LancamentosSummaryDTO getLancamentosSummaryDTO(){
        return new LancamentosSummaryDTO(1050.0,4.0,400.0,5.0,5L);
    }


    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
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
