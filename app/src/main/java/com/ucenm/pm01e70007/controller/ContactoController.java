package com.ucenm.pm01e70007.controller;

import android.content.Context;

import com.ucenm.pm01e70007.dao.ContactoDAO;
import com.ucenm.pm01e70007.dao.ContactoDAOImpl;
import com.ucenm.pm01e70007.model.Contactos;
import com.ucenm.pm01e70007.util.Validador;

import java.util.List;

/** CONTROLADOR: valida y coordina la vista con el DAO. */
public class ContactoController {

    /** Error de validación asociado a un campo del formulario. */
    public static class ErrorCampo {
        public static final int NOMBRE = 1;
        public static final int TELEFONO = 2;
        public static final int NOTA = 3;

        public final int campo;
        public final String mensaje;

        public ErrorCampo(int campo, String mensaje) {
            this.campo = campo;
            this.mensaje = mensaje;
        }
    }

    private final ContactoDAO dao;

    public ContactoController(Context context) {
        this.dao = new ContactoDAOImpl(context);
    }

    /** Devuelve null si todo es válido; si no, el primer error encontrado. */
    public ErrorCampo validar(String nombre, String telefono, String nota) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return new ErrorCampo(ErrorCampo.NOMBRE, "Debe ingresar el nombre del contacto.");
        }
        if (!Validador.nombreValido(nombre.trim())) {
            return new ErrorCampo(ErrorCampo.NOMBRE, "El nombre solo puede contener letras y espacios.");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            return new ErrorCampo(ErrorCampo.TELEFONO, "Debe ingresar el número de teléfono.");
        }
        if (!Validador.telefonoValido(telefono.trim())) {
            return new ErrorCampo(ErrorCampo.TELEFONO,
                    "El teléfono solo puede contener dígitos (entre 7 y 15).");
        }
        if (nota == null || nota.trim().isEmpty()) {
            return new ErrorCampo(ErrorCampo.NOTA, "Debe ingresar una nota para el contacto.");
        }
        return null;
    }

    public long guardar(Contactos c) { return dao.insertar(c); }

    public int actualizar(Contactos c) { return dao.actualizar(c); }

    public boolean eliminar(int id) { return dao.eliminar(id) > 0; }

    public Contactos obtener(int id) { return dao.obtenerPorId(id); }

    public List<Contactos> listar() { return dao.listar(); }

    public List<Contactos> buscar(String texto) { return dao.buscar(texto); }
}
