package com.example.poker_dados_cirosd.vista;

import com.example.poker_dados_cirosd.modelo.Dado;
import com.example.poker_dados_cirosd.modelo.Jugador;       //importo las clases que debe leer
import com.example.poker_dados_cirosd.modelo.Partida;

import java.util.List;

public interface IVista {
    void mostrarSala(List<Jugador> jugadores);
    void mostrarMesa(Partida.EstadoDePartida estado, int pozo, int apuestaMaxima,
                     Jugador jugadorActual, List<Jugador> jugadores, Dado[] dadosVisibles,
                     boolean dadosRevelados);
    void mostrarResultadoRonda(String mensaje);
    void mostrarFinPartida(String mensaje);
    void mostrarError(String mensaje);
}