package br.com.comexport.javachallenge.lancamentoContabil;

import br.com.comexport.javachallenge.JavaChallengeApplication;
import br.com.comexport.javachallenge.dto.LancamentoContabilDTO;
import br.com.comexport.javachallenge.dto.LancamentosSummaryDTO;
import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.entities.LancamentoContabil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JavaChallengeApplication.class })
@WebAppConfiguration
public class LancamentoContabilControlerIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }


    @Test
    public void criarLancamentoContabilDeveRetornarStatus201() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();

        String URI = "/lancamentos-contabeis";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

    @Test
    public void criarLancamentoContabilComContaContabilInexistenteDeveRetornar404() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        request.setContaContabil(8888);
        String URI = "/lancamentos-contabeis";

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
    public void criarLancamentoContabilComContaContabilNulaDeveRetornarBadRequest() throws Exception {

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

        String URI = "/lancamentos-contabeis/"+getLancamentoContabil().getId();

        MvcResult result =  mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        Assert.assertNotNull(result.getResponse().getContentAsString());

    }

    @Test
    public void buscarLancamentoContabilPorIdInexistenteDeveRetornar404() throws Exception {

        String URI = "/lancamentos-contabeis/90909090";

        mockMvc.perform(get(URI))
                .andExpect(status().isNotFound());

    }

    @Test
    public void buscarSumarizadoSemContaDeveRetornarEstaticasParaTodosLancamentos() throws Exception {

        String URI = "/lancamentos-contabeis/_stats";

        mockMvc.perform(get(URI))
                .andExpect(status().isOk()).andDo(print());

    }

    @Test
    public void buscarSumarizadoPorContaContabilDeveRetornarEstaticas() throws Exception {

        ContaContabil contaContabil = getContaContabil();

        String URI = "/lancamentos-contabeis/_stats/?contaContabil="+contaContabil.getNumero();

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andDo(print());
    }

/*    @Test
    public void atualizarLancamentoContabilDeveRetornar204() throws Exception {
O
        LancamentoContabilDTO request = getLancamentoContabilDTO();
        request.setContaContabil(null);

        String URI = "/lancamentos-contabeis";
        Mockito.doNothing().when(this.lancamentoContabilService.atualizarLancamento(null,null))

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());

    }
*/

    public LancamentosSummaryDTO getLancamentosSummaryDTO(){
        return new LancamentosSummaryDTO(1050.0,4.0,400.0,5.0,5L);
    }








    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

    private LancamentoContabil getLancamentoContabil(){
        LancamentoContabil lancamento = new LancamentoContabil();
        lancamento.setId("4028808767b3d6480167b3d6eafc0000");
        lancamento.setValor(10.00);
        lancamento.setData(20181010);
        lancamento.setContaContabil(getContaContabil());

        return lancamento;
    }

    private ContaContabil getContaContabil(){
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setDescricao("Conta test");
        contaContabil.setNumero(5555);
        return contaContabil;
    }

    private LancamentoContabilDTO getLancamentoContabilDTO(){
        LancamentoContabilDTO lancamento = new LancamentoContabilDTO();
        lancamento.setValor(10.00);
        lancamento.setData(20181010);
        lancamento.setContaContabil(getContaContabil().getNumero());
        return lancamento;
    }
}
