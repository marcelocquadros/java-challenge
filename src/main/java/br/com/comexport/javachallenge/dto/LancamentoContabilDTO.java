package br.com.comexport.javachallenge.dto;

import br.com.comexport.javachallenge.utils.DateIntegerValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LancamentoContabilDTO {

    @NotNull(message = "Campo contaContabil não pode ser nulo")
    private Integer contaContabil;

    @NotNull(message = "Campo data não pode ser nulo")
    @DateIntegerValidator(pattern = "yyyyMMdd") //custom validator (the spring annotation does not support Integer representation)
    private Integer data;
    //@Digits(integer = 10,fraction = 2)
    @NotNull(message = "Campo valor não pode ser nulo")
    private Double valor;

    @JsonIgnore
    private Integer version;


}

