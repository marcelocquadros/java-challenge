package br.com.comexport.javachallenge.contacontabil;

import br.com.comexport.javachallenge.JavaChallengeApplication;
import br.com.comexport.javachallenge.entities.ContaContabil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JavaChallengeApplication.class })
@WebAppConfiguration
public class ContaContabilControllerIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void criarContaContabilDeveRetornar201() throws Exception {

        ContaContabil request = getContaContabil();

        String URI = "/conta-contabil";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(request.getNumero())));
    }

    @Test
    public void criarContaContabilSemNumeroDeveRetornarBadRequest() throws Exception {

        ContaContabil request = new ContaContabil();
        request.setDescricao("Conta sem numero");

        String URI = "/conta-contabil";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void criarContaContabilSemDescricaoDeveRetornarBadRequest() throws Exception {

        ContaContabil request = new ContaContabil();
        request.setNumero(10010101);

        String URI = "/conta-contabil";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void buscarPorIdDeveRetornarContaContabil() throws Exception {

        ContaContabil novaContaContabilReq = getContaContabil();
        String URI = "/conta-contabil";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(novaContaContabilReq))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isCreated());

        URI+="/"+novaContaContabilReq.getNumero();

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(novaContaContabilReq)));
    }

    @Test
    public void buscarPorIdInexistenteDeveRetornar404() throws Exception {

        ContaContabil contaContabil = getContaContabil();
        contaContabil.setNumero(0);
        String URI = "/conta-contabil/"+contaContabil.getNumero();

        mockMvc.perform(get(URI))
                .andExpect(status().isNotFound());
    }

    private ContaContabil getContaContabil(){
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setDescricao("Conta test");
        contaContabil.setNumero(new Random().nextInt(1000));
        return contaContabil;
    }


    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }

}
