package cz.cvut.fel.ear.reservation_system.pipesandfilters;

public interface Filter<T> {
    T execute(T input);
}
