package com.ucenm.pm01e70007.dao;

import com.ucenm.pm01e70007.model.Contactos;

import java.util.List;

public interface ContactoDAO {
    long insertar(Contactos contacto);
    int actualizar(Contactos contacto);
    int eliminar(int id);
    Contactos obtenerPorId(int id);
    List<Contactos> listar();
    List<Contactos> buscar(String texto);
}
