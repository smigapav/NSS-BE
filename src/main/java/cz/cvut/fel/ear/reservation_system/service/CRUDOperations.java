package cz.cvut.fel.ear.reservation_system.service;

import java.util.List;

public interface CRUDOperations<T> {
    void create(T entity);

    T read(Integer id);

    void update(T entity);

    void delete(Integer id);

    List<T> listAll();
}
