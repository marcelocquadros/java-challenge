package br.com.comexport.javachallenge.repository;

import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.entities.LancamentoContabil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LancamentoContabilRepository extends JpaRepository<LancamentoContabil, String> {

    List<LancamentoContabil> findByContaContabil(final ContaContabil contaContabil);

}
