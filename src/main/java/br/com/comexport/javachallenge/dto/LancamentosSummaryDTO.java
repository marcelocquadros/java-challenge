package br.com.comexport.javachallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LancamentosSummaryDTO {

    private Double soma;
    private Double min;
    private Double max;
    private Double media;
    private Long qtde;

}
