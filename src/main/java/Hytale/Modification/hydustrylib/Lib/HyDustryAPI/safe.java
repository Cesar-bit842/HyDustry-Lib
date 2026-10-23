package Hytale.Modification.hydustrylib.Lib.HyDustryAPI;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR}) 
@Retention(RetentionPolicy.SOURCE) 
public @interface safe {
    String reason() default "stable and safe method";
} 