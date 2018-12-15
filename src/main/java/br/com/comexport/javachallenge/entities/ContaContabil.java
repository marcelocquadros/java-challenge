package br.com.comexport.javachallenge.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Entity
public class ContaContabil {

    @Id
    @Positive(message = "Campo numero deve ser maior que 0")
    @NotNull
    private Integer numero;

    @NotNull(message = "Campo descricao não pode ser nulo")
    @Column(nullable = false)
    private String descricao;

    @JsonIgnore
    @Version
    private Integer version;

}
