package br.com.comexport.javachallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LancamentosSummaryDTO {

    private double soma;
    private double min;
    private double max;
    private double media;
    private long qtde;

}
