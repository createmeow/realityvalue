package dev.ghen.thirst.foundation.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* loaded from: ThirstWasTaken-1.20.1-1.4.0.jar:dev/ghen/thirst/foundation/util/ReflectionUtil.class */
public class ReflectionUtil {
    public static Object fuckYouReflections(Method method, Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
