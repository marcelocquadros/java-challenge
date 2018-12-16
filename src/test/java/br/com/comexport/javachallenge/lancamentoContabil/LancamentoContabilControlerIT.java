package br.com.comexport.javachallenge.lancamentoContabil;

import br.com.comexport.javachallenge.JavaChallengeApplication;
import br.com.comexport.javachallenge.dto.LancamentoContabilDTO;
import br.com.comexport.javachallenge.dto.LancamentosSummaryDTO;
import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.entities.LancamentoContabil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Random;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private Integer criarContaContabilParaLancamentos() throws Exception {
        String URI = "/conta-contabil";
        ContaContabil contaContabil = getContaContabil();
        contaContabil.setNumero(new Random().nextInt(100));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(contaContabil))
                .contentType(MediaType.APPLICATION_JSON);

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
        return contaContabil.getNumero();
    }


    @Test
    public void criarLancamentoContabilDeveRetornarStatus201() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        Integer conta = criarContaContabilParaLancamentos();
        request.setContaContabil(conta);

        String URI = "/lancamentos-contabeis";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
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

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        Integer conta = criarContaContabilParaLancamentos();
        request.setContaContabil(conta);

        String URI = "/lancamentos-contabeis/";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        String lancamentoJson = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject object = new JSONObject(lancamentoJson);
        URI += object.getString("id");


        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contaContabil", is(conta)));


    }

    @Test
    public void buscarLancamentoContabilPorIdInexistenteDeveRetornar404() throws Exception {

        String URI = "/lancamentos-contabeis/90909090";

        mockMvc.perform(get(URI))
                .andExpect(status().isNotFound());

    }

    @Test
    public void buscarSumarizadoSemContaDeveRetornarEstaticasParaTodosLancamentos() throws Exception {

        criarLancamentoContabilDeveRetornarStatus201();

        String URI = "/lancamentos-contabeis/_stats";

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qtde",is(1)));


    }

    @Test
    public void buscarSumarizadoPorContaContabilDeveRetornarEstaticas() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        Integer conta = criarContaContabilParaLancamentos();
        request.setContaContabil(conta);

        String URI = "/lancamentos-contabeis";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        URI = "/lancamentos-contabeis/_stats/?contaContabil="+conta;

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qtde").exists());
    }

    @Test
    public void atualizarLancamentoContabilDeveRetornar204() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        Integer conta = criarContaContabilParaLancamentos();
        request.setContaContabil(conta);

        String URI = "/lancamentos-contabeis/";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        String lancamentoIdJson = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject object = new JSONObject(lancamentoIdJson);
        URI += object.getString("id");

        String etag = mockMvc.perform(get(URI)).andReturn().getResponse().getHeader("Etag");


        RequestBuilder requestBuilderUpdate = MockMvcRequestBuilders
                .put(URI)
                .header("If-Match", etag)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderUpdate)
                .andExpect(status().isNoContent());

    }

    //simulate two users trying to update the resource at the same time
    @Test
    public void atualizarLancamentoContabilVersionDiferenteDeveRetornarErro() throws Exception {

        LancamentoContabilDTO request = getLancamentoContabilDTO();
        Integer conta = criarContaContabilParaLancamentos();
        request.setContaContabil(conta);

        String URI = "/lancamentos-contabeis/";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        String lancamentoIdJson = mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JSONObject object = new JSONObject(lancamentoIdJson);
        URI += object.getString("id");

        String etag = mockMvc.perform(get(URI)).andReturn().getResponse().getHeader("Etag");
        etag = etag+"1";

        RequestBuilder requestBuilderUpdate = MockMvcRequestBuilders
                .put(URI)
                .header("If-Match", 100)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilderUpdate)
                .andExpect(status().isPreconditionFailed());

    }


    public LancamentosSummaryDTO getLancamentosSummaryDTO(){
        return new LancamentosSummaryDTO(1050.0,4.0,400.0,5.0,5L);
    }

    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }


    private Class<?> mapToObject(String json, Class<?> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz.getClass());
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
