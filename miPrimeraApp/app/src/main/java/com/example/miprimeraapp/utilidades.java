package com.example.miprimeraapp;

import java.util.Base64;

public class utilidades {
    static String url_consulta = "http://192.168.56.1:5984/amigos/_design/amigos/_view/amigos";
    static String url_mantenimiento = "http://192.168.56.1:5984/amigos"; // CRUD Insertar, Actualizar, Borrar y Buscar

    static String user = "admin";
    static String passwd = "admin";
    static String credencialesCodificadas = Base64.getEncoder().encodeToString((user + ":" + passwd).getBytes());

    public String generarUnicoId() {
        return java.util.UUID.randomUUID().toString();
    }
}
