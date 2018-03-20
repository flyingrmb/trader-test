package org.actech.smart.test.annotation;

import java.lang.annotation.*;

/**
 * Created by paul on 2018/3/18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MockDataInit {
    MockData[] value();
}
