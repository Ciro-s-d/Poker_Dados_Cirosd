package com.example.poker_dados_cirosd.modelo;

import java.io.Serializable;

public class ResultadoTirada implements Serializable {

    private String nombreJugada;
    private int puntaje;

    public ResultadoTirada(String nombreJugada, int puntaje) {
        this.nombreJugada = nombreJugada;
        this.puntaje = puntaje;
    }

    public String getNombreJugada() {
        return nombreJugada;
    }

    public int getPuntaje() {
        return puntaje;
    }

    @Override
    public String toString(){
        return nombreJugada + " (puntaje: " + puntaje + ")";
    }
}
