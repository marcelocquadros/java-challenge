package br.com.comexport.javachallenge.service;

import br.com.comexport.javachallenge.entities.ContaContabil;
import br.com.comexport.javachallenge.exceptions.ResourceAlreadyExistsException;
import br.com.comexport.javachallenge.exceptions.ResourceNotFoundException;
import br.com.comexport.javachallenge.repository.ContaContabilRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class ContaContabilService {

    @Autowired
    private ContaContabilRepository contaContabilRepository;

    public Integer criarContaContabil(final ContaContabil contaContabil){
        log.info("Criando contaContabil - {}", contaContabil);

        Optional<ContaContabil> conta = contaContabilRepository.findById(contaContabil.getNumero());

        if(conta.isPresent()){
            log.error("Conta contábil {} já existente", contaContabil.getNumero());
            throw new ResourceAlreadyExistsException(String.format("Conta contábil %s já existente", contaContabil.getNumero()));
        }

        ContaContabil contaSalva = contaContabilRepository.save(contaContabil);

        log.info("Conta contábil salva com sucesso");

        return contaSalva.getNumero();
    }

    public ContaContabil buscarPorId(final Integer id) {

        if(isNull(id)){
            log.error("Id não pode ser nulo");
            throw new IllegalArgumentException("id não pode ser nulo");
        }

        return contaContabilRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Conta contábil %s não encontrada", id)));

    }
}
