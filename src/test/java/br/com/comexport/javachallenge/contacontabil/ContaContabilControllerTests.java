package br.com.comexport.javachallenge.contacontabil;

import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.exceptions.ResourceNotFoundException;
import br.com.comexport.javachallenge.service.ContaContabilService;
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

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ContaContabilControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContaContabilService contaContabilService;

    @Test
    public void criarContaContabilDeveRetornar201() throws Exception {

        ContaContabil request = getContaContabil();
        Mockito.when(contaContabilService.criarContaContabil(Mockito.any())).thenReturn(request.getNumero());
        String URI = "/conta-contabil";

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(URI)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapToJson(request))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
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

        ContaContabil contaContabil = getContaContabil();
        Mockito.when(contaContabilService.buscarPorId(contaContabil.getNumero())).thenReturn(contaContabil);

        String URI = "/conta-contabil/"+contaContabil.getNumero();

        mockMvc.perform(get(URI))
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(contaContabil)));
    }

    @Test
    public void buscarPorIdInexistenteDeveRetornar404() throws Exception {

        ContaContabil contaContabil = getContaContabil();
        Mockito.when(contaContabilService.buscarPorId(contaContabil.getNumero()))
                .thenThrow(new ResourceNotFoundException("Conta nao encontrada"));

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

    /**
     * Maps an Object into a JSON String. Uses a Jackson ObjectMapper.
     */
    private String mapToJson(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
