package br.com.comexport.javachallenge.utils;

import java.text.DecimalFormat;

import static java.util.Objects.isNull;

public class APIUtils {

    public static Double format2Fraction(Double value){
        if(isNull(value)) return null;
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(value));
    }
}
