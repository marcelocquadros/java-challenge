package br.com.comexport.javachallenge.contacontabil;

import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.repository.ContaContabilRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;
import java.util.Random;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ContaContabilRepositoryTests {

    @Autowired
    private ContaContabilRepository contaContabilRepository;

    @Test
    public void saveDeveRetornarContaCriada() {
        ContaContabil contaContabil = this.contaContabilRepository.save(getContaContabil());
        Assert.assertNotNull(contaContabil);
    }

    @Test(expected = JpaSystemException.class)
    public void saveSemIdDeveRetornarException(){
        ContaContabil contaSemNumero = getContaContabil();
        contaSemNumero.setNumero(null);
        ContaContabil contaContabil = this.contaContabilRepository.save(contaSemNumero);
    }

    @Test
    public void findByIdExistenteDeveRetornarContaContabil(){
        ContaContabil contaContabil = this.contaContabilRepository.save(getContaContabil());
        Assert.assertNotNull(this.contaContabilRepository.findById(contaContabil.getNumero()).get());
    }

    @Test(expected = NoSuchElementException.class)
    public void findByIdInexistenteDeveRetornarException(){
        Assert.assertNotNull(this.contaContabilRepository.findById(0).get());
    }

    private ContaContabil getContaContabil(){
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setDescricao("Conta test");
        contaContabil.setNumero(new Random().nextInt(1000));
        return contaContabil;
    }
}
