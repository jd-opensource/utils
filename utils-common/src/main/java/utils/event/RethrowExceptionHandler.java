package utils.event;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

public class RethrowExceptionHandler<TListener> implements ExceptionHandle<TListener> {

    public RethrowExceptionHandler() {
    }

    @Override
    public void handle(Exception ex, TListener listener, Method method, Object[] args) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

}
