package br.com.comexport.javachallenge.contacontabil;

import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.exceptions.ResourceAlreadyExistsException;
import br.com.comexport.javachallenge.exceptions.ResourceNotFoundException;
import br.com.comexport.javachallenge.repository.ContaContabilRepository;
import br.com.comexport.javachallenge.service.ContaContabilService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContaContabilServiceTests {

    @Autowired
    private ContaContabilService contaContabilService;

    @MockBean
    private ContaContabilRepository repository;

    @Test
    public void criarContaContabilDeveRetornarNumeroDaConta(){
        Mockito.when(repository.save(Mockito.any(ContaContabil.class))).thenReturn(getContaContabil());
        Integer numero = contaContabilService.criarContaContabil(getContaContabil());
        Assert.assertNotNull(numero);
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void criarContaContabilComMesmoNumeroDeveRetornarException(){
        ContaContabil conta1 =  getContaContabil();
        Mockito.when(repository.save(Mockito.any(ContaContabil.class))).thenReturn(conta1);
        Mockito.when(repository.findById(conta1.getNumero())).thenReturn(Optional.of(conta1));

        this.contaContabilService.criarContaContabil(conta1);
        ContaContabil conta2 =  getContaContabil();
        conta2.setNumero(conta1.getNumero()); //same identifier
        this.contaContabilService.criarContaContabil(conta2);
    }


    @Test
    public void buscarPorIdDeveRetornarContaContabil(){
        ContaContabil contaContabil  =  getContaContabil();
        Mockito.when(repository.findById(contaContabil.getNumero())).thenReturn(Optional.of(contaContabil));
        ContaContabil contaContabilBusca = contaContabilService.buscarPorId(contaContabil.getNumero());

        Assert.assertEquals(contaContabil, contaContabilBusca);

    }

    @Test(expected = ResourceNotFoundException.class)
    public void buscarPorIdInexistenteDeveRetornarResourceNotFoundException(){
        Mockito.when(repository.findById(Mockito.anyInt())).thenThrow(new ResourceNotFoundException("Conta nao encontrada"));
        ContaContabil contaContabilBusca = contaContabilService.buscarPorId(88);
    }



    private ContaContabil getContaContabil(){
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setDescricao("Conta test");
        contaContabil.setNumero(new Random().nextInt(1000));
        return contaContabil;
    }
}
