package cz.cvut.fel.ear.reservation_system.pipesandfilters;

import java.util.ArrayList;
import java.util.List;

public class Pipeline<T> {
    private final List<Filter<T>> filters = new ArrayList<>();

    public void addFilter(Filter<T> filter) {
        filters.add(filter);
    }

    public T execute(T input) {
        T result = input;
        for (Filter<T> filter : filters) {
            result = filter.execute(result);
        }
        return result;
    }
}
