package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericLoggingFilter<T> implements Filter<T> {
    private static final Logger LOG = LoggerFactory.getLogger(GenericLoggingFilter.class);
    private final String message;
    private final String className;
    private final String methodName;

    public GenericLoggingFilter(String message, String className, String methodName) {
        this.message = message;
        this.className = className;
        this.methodName = methodName;
    }

    /**
     * Executes the filter operation.
     * This method logs a message with the class name and method name.
     *
     * @param input the input data to process
     * @return the processed input data
     */
    @Override
    public T execute(T input) {
        LOG.info("{}.{}: {}", className, methodName, message);
        return input;
    }
}