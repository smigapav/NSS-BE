package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericLoggingFilter<T> implements Filter<T> {
    private static final Logger LOG = LoggerFactory.getLogger(GenericLoggingFilter.class);

    @Override
    public T execute(T input) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        String callerClass = caller.getClassName();
        String callerMethod = caller.getMethodName();

        LOG.info("Processing object: {} from class: {} method: {}", input, callerClass, callerMethod);
        return input;
    }
}