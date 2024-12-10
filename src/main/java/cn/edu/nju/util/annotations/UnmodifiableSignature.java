package cn.edu.nju.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method's signature must not be changed.
 */
@Retention(RetentionPolicy.RUNTIME) // 保留到运行时，可通过反射检查
@Target(ElementType.METHOD)         // 仅可用于方法
public @interface UnmodifiableSignature {
    // DO NOT CHANGE METHOD WITH THIS ANNOTATION!
}
