package br.com.comexport.javachallenge.utils;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateIntegerConstraintValidator  implements ConstraintValidator<DateIntegerValidator, Integer>{

     private String pattern;

    @Override
    public void initialize(DateIntegerValidator constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(Integer originalDate, ConstraintValidatorContext context) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date dateRet = sdf.parse(String.valueOf(originalDate));
            if( sdf.format(dateRet).equals(String.valueOf(originalDate))){
                return true;
            }
            return  false;
        }catch (ParseException e){
            return false;
        }
    }
}
