package com.example.poker_dados_cirosd.modelo;

public interface IObservable {
    void agregarObservador(IObservador observador);
    void quitarObservador(IObservador observador);
}