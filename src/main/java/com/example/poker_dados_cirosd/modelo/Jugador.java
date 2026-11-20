package com.example.poker_dados_cirosd.modelo;

import java.io.Serializable;

public class Jugador implements Serializable {
    private static final int FICHAS_INICIALES = 250;

    private int idJugador;
    private String nombreJugador;
    private int puntajeJugador;
    private ResultadoTirada ultimaJugada;
    private int cantidadFichas;
    private final Cubo cubo;                //cubo secreto, antes solo habia un cubo general
    private int aporteEnVuelta;             //se fija si calleo la apuesta o no
    private boolean activoEnVuelta;         //false si foldeo

    public Jugador(String nombreJugador, int idJugador){
        this.nombreJugador = nombreJugador;
        this.cubo = new Cubo();
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

    public Cubo getCubo() {
        return cubo;
    }

    public int getAporteEnVuelta(){
        return aporteEnVuelta;
    }

    public boolean isActivoEnVuelta() {
        return activoEnVuelta;
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

    //TEMA VUELTAS Y SU APUESTA
    public void prepararParaVuelta(boolean activo) {
        this.puntajeJugador = 0;
        this.ultimaJugada = null;
        this.aporteEnVuelta = 0;
        this.activoEnVuelta = activo;
        this.cubo.desbloquearTodosDados();
    }

    public void abandonarVuelta() {
        this.activoEnVuelta = false;        //si abandono igual perdi las fichas apostadas
    }

    public void registrarAporteVuelta(int monto) {
        if (monto > 0) {
            int real = Math.min(monto, this.cantidadFichas);
            this.restarFichas(real);
            this.aporteEnVuelta += real;
        }
    }
}
