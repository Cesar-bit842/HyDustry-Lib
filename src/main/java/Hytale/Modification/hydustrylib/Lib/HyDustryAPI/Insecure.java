package Hytale.Modification.hydustrylib.Lib.HyDustryAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR}) 
@Retention(RetentionPolicy.SOURCE) 
public @interface Insecure {
    String reason() default "This method is not safe to use";
} 