package com.master.air.dao;

import java.sql.SQLException;
import java.util.List;


public interface DAO<T> {
    

    T create(T obj) throws SQLException;

    T findById(int id) throws SQLException;
    

    List<T> findAll() throws SQLException;

    boolean update(T obj) throws SQLException;

    boolean delete(int id) throws SQLException;
}
