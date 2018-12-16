package br.com.comexport.javachallenge.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
public class LancamentoContabil {

    @Id @GeneratedValue(generator="org.hibernate.id.UUIDGenerator")
    @GenericGenerator(name="org.hibernate.id.UUIDGenerator", strategy = "uuid")
    private String id;

    @ManyToOne(optional = false)
    private ContaContabil contaContabil;

    @Column(nullable = false)
    private Integer data;

    @Column(nullable = false)
    private Double valor;

    @Version
    @Column(nullable = false)
    private Integer version;

}
