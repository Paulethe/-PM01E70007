package com.ucenm.pm01e70007.util;

import java.util.ArrayList;
import java.util.List;

/** Listado de países con su código de área. */
public final class Paises {

    public static class Pais {
        private final String nombre;
        private final String codigo;

        Pais(String nombre, String codigo) {
            this.nombre = nombre;
            this.codigo = codigo;
        }

        public String getNombre() { return nombre; }

        public String getCodigo() { return codigo; }

        public String etiqueta() { return nombre + " (+" + codigo + ")"; }
    }

    public static final List<Pais> LISTA = new ArrayList<>();

    static {
        add("Honduras", "504");
        add("Guatemala", "502");
        add("El Salvador", "503");
        add("Nicaragua", "505");
        add("Costa Rica", "506");
        add("Panamá", "507");
        add("Belice", "501");
        add("México", "52");
        add("Estados Unidos", "1");
        add("Canadá", "1");
        add("Cuba", "53");
        add("República Dominicana", "1");
        add("Puerto Rico", "1");
        add("Colombia", "57");
        add("Venezuela", "58");
        add("Ecuador", "593");
        add("Perú", "51");
        add("Bolivia", "591");
        add("Chile", "56");
        add("Argentina", "54");
        add("Uruguay", "598");
        add("Paraguay", "595");
        add("Brasil", "55");
        add("España", "34");
        add("Francia", "33");
        add("Italia", "39");
        add("Alemania", "49");
        add("Portugal", "351");
        add("Reino Unido", "44");
        add("China", "86");
        add("Japón", "81");
        add("India", "91");
    }

    private Paises() {}

    private static void add(String nombre, String codigo) {
        LISTA.add(new Pais(nombre, codigo));
    }

    public static List<String> etiquetas() {
        List<String> r = new ArrayList<>();
        for (Pais p : LISTA) r.add(p.etiqueta());
        return r;
    }

    public static int indice(String nombre) {
        for (int i = 0; i < LISTA.size(); i++) {
            if (LISTA.get(i).getNombre().equals(nombre)) return i;
        }
        return 0;
    }

    public static String codigoDe(String nombre) {
        return LISTA.get(indice(nombre)).getCodigo();
    }
}
