package br.com.comexport.javachallenge.lancamentoContabil;

import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.entities.LancamentoContabil;
import br.com.comexport.javachallenge.repository.LancamentoContabilRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.NoSuchElementException;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LancamentoContabilRepositoryTests {

    @Autowired
    private LancamentoContabilRepository lancamentoContabilRepository;

    @Test
    public void saveDeveRetornarLancamentoContabilCriado() {
        LancamentoContabil lancamentoContabil = this.lancamentoContabilRepository.save(getLancamentoContabil());
        Assert.assertNotNull(lancamentoContabil);
    }

    @Test(expected = NoSuchElementException.class)
    public void findByIdInexistenteDeveRetornarException() {
        lancamentoContabilRepository.findById("xxxxxx").get();
    }


    @Test
    public void findByIdDeveRetornarLancamento() {
        String id = this.lancamentoContabilRepository.save(getLancamentoContabil()).getId();
        Assert.assertNotNull(lancamentoContabilRepository.findById(id).get());
    }


    private LancamentoContabil getLancamentoContabil(){
        LancamentoContabil lancamento = new LancamentoContabil();
        lancamento.setValor(10.00);
        lancamento.setData(20181010);
        ContaContabil contaContabil = new ContaContabil();
        contaContabil.setDescricao("Conta teste");
        contaContabil.setNumero(1010);
        lancamento.setContaContabil(contaContabil);

        return lancamento;
    }
}
