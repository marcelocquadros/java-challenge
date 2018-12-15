package br.com.comexport.javachallenge.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class LancamentoContabil {

    @Id @GeneratedValue(generator="org.hibernate.id.UUIDGenerator")
    @GenericGenerator(name="org.hibernate.id.UUIDGenerator", strategy = "uuid")
    private String id;

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private ContaContabil contaContabil;

    @NotNull
    @Column(nullable = false)
    private Integer data;

    @NotNull
    @Column(nullable = false)
    private Double valor;

    @Version
    @Column(nullable = false)
    private Integer version;

}
