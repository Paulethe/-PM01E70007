package com.ucenm.pm01e70007.util;

import android.text.InputFilter;

import java.util.regex.Pattern;

/** Expresiones regulares para validar y limitar la entrada. */
public final class Validador {

    private Validador() {}

    /** Solo letras (con tildes y ñ) y espacios. */
    public static final Pattern NOMBRE = Pattern.compile("^[\\p{L} ]+$");
    /** Solo dígitos, entre 7 y 15. */
    public static final Pattern TELEFONO = Pattern.compile("^\\d{7,15}$");

    private static final Pattern CHAR_NOMBRE = Pattern.compile("[\\p{L} ]");
    private static final Pattern CHAR_TELEFONO = Pattern.compile("\\d");

    public static boolean nombreValido(String s) { return NOMBRE.matcher(s).matches(); }

    public static boolean telefonoValido(String s) { return TELEFONO.matcher(s).matches(); }

    /** Filtro que rechaza en tiempo real los caracteres inválidos del nombre. */
    public static InputFilter filtroNombre() { return filtro(CHAR_NOMBRE); }

    /** Filtro que rechaza en tiempo real todo lo que no sea dígito. */
    public static InputFilter filtroTelefono() { return filtro(CHAR_TELEFONO); }

    private static InputFilter filtro(final Pattern permitido) {
        return (source, start, end, dest, dstart, dend) -> {
            StringBuilder sb = new StringBuilder();
            boolean cambio = false;
            for (int i = start; i < end; i++) {
                char ch = source.charAt(i);
                if (permitido.matcher(String.valueOf(ch)).matches()) {
                    sb.append(ch);
                } else {
                    cambio = true;
                }
            }
            return cambio ? sb.toString() : null;
        };
    }
}
