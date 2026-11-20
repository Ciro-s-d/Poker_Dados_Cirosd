package com.example.poker_dados_cirosd.modelo;

import java.io.Serializable;

public class Jugador implements Serializable {
    private static final int FICHAS_INICIALES = 250;

    private int idJugador;
    private String nombreJugador;
    private int puntajeJugador;
    private ResultadoTirada ultimaJugada;
    private int cantidadFichas;

    public Jugador(String nombreJugador, int idJugador){
        this.nombreJugador = nombreJugador;
        this.puntajeJugador = 0;
        this.cantidadFichas = FICHAS_INICIALES;
        this.idJugador = idJugador;
    }


    //GETTERS
    public int getIdJugador() {
        return idJugador;
    }

    public int getCantidadFichas() {
        return cantidadFichas;
    }

    public String getnombreJugador() {
        return nombreJugador;
    }

    public int getPuntajeJugador() {
        return puntajeJugador;
    }

    public ResultadoTirada getUltimaJugada() {
        return ultimaJugada;
    }

    //SETTERS
    public void setUltimaJugada(ResultadoTirada ultimaJugada) {
        this.ultimaJugada = ultimaJugada;
    }

    //CALCUKO DE PUNTOS
    public void sumarPuntos(int puntos){
        if(puntos > 0){
            this.puntajeJugador += puntos;
        }
    }

    public void resetearPuntos(){
        this.puntajeJugador = 0;
    }

    public void sumarFichas(int fichas){
        if(fichas > 0){
            this.cantidadFichas += fichas;
        }
    }

    public void restarFichas(int fichas) {
        if (fichas > 0) {
            this.cantidadFichas = Math.max(0, this.cantidadFichas - fichas);
        }
    }
}
