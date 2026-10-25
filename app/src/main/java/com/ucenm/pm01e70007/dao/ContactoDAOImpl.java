package com.ucenm.pm01e70007.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ucenm.pm01e70007.model.Contactos;

import java.util.ArrayList;
import java.util.List;

/** DAO: implementación con SQLiteOpenHelper. */
public class ContactoDAOImpl implements ContactoDAO {

    private final DBHelper helper;

    public ContactoDAOImpl(Context context) {
        this.helper = new DBHelper(context.getApplicationContext());
    }

    private ContentValues valores(Contactos c) {
        ContentValues v = new ContentValues();
        v.put(DBHelper.COL_PAIS, c.getPais());
        v.put(DBHelper.COL_NOMBRE, c.getNombre());
        v.put(DBHelper.COL_TELEFONO, c.getTelefono());
        v.put(DBHelper.COL_NOTA, c.getNota());
        v.put(DBHelper.COL_IMAGEN, c.getImagen());
        return v;
    }

    private Contactos mapear(Cursor cur) {
        return new Contactos(
                cur.getInt(cur.getColumnIndexOrThrow(DBHelper.COL_ID)),
                cur.getString(cur.getColumnIndexOrThrow(DBHelper.COL_PAIS)),
                cur.getString(cur.getColumnIndexOrThrow(DBHelper.COL_NOMBRE)),
                cur.getString(cur.getColumnIndexOrThrow(DBHelper.COL_TELEFONO)),
                cur.getString(cur.getColumnIndexOrThrow(DBHelper.COL_NOTA)),
                cur.getBlob(cur.getColumnIndexOrThrow(DBHelper.COL_IMAGEN)));
    }

    @Override
    public long insertar(Contactos contacto) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.insert(DBHelper.TABLA, null, valores(contacto));
    }

    @Override
    public int actualizar(Contactos contacto) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update(DBHelper.TABLA, valores(contacto),
                DBHelper.COL_ID + "=?", new String[]{String.valueOf(contacto.getId())});
    }

    @Override
    public int eliminar(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.delete(DBHelper.TABLA, DBHelper.COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    @Override
    public Contactos obtenerPorId(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        try (Cursor cur = db.query(DBHelper.TABLA, null, DBHelper.COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cur.moveToFirst()) {
                return mapear(cur);
            }
        }
        return null;
    }

    @Override
    public List<Contactos> listar() {
        return buscar(null);
    }

    @Override
    public List<Contactos> buscar(String texto) {
        List<Contactos> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String where = null;
        String[] args = null;
        if (texto != null && !texto.trim().isEmpty()) {
            where = DBHelper.COL_NOMBRE + " LIKE ? OR " + DBHelper.COL_TELEFONO + " LIKE ?";
            String like = "%" + texto.trim() + "%";
            args = new String[]{like, like};
        }
        try (Cursor cur = db.query(DBHelper.TABLA, null, where, args, null, null,
                DBHelper.COL_NOMBRE + " COLLATE NOCASE ASC")) {
            while (cur.moveToNext()) {
                lista.add(mapear(cur));
            }
        }
        return lista;
    }
}
