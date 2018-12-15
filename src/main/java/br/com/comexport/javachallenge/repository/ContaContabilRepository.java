package br.com.comexport.javachallenge.repository;

import br.com.comexport.javachallenge.entities.ContaContabil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaContabilRepository extends JpaRepository<ContaContabil, Integer> {
}
