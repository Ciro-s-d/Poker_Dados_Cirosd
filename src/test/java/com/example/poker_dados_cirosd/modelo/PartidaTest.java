package com.example.poker_dados_cirosd.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PartidaTest {

    @Test
    void agregaJugadoresValidaNombreYEstadoInicial() {
        Partida partida = new Partida();
        assertEquals(Partida.EstadoDePartida.SALA, partida.getEstado());

        assertThrows(IllegalArgumentException.class, () -> partida.agregarJugador("  "));

        Jugador j1 = partida.agregarJugador("Jugador 1");
        Jugador j2 = partida.agregarJugador("Jugador 2");

        assertEquals(1, j1.getIdJugador());
        assertEquals(2, j2.getIdJugador());
        assertEquals(2, partida.getJugadores().size());
    }

    @Test
    void soloElAdminIniciaPartidaYValidaTurno() {
        Partida partida = new Partida();
        assertThrows(IllegalStateException.class, () -> partida.iniciarPartida(1));

        Jugador j1 = partida.agregarJugador("Admin");
        Jugador j2 = partida.agregarJugador("Invitado");

        assertThrows(IllegalStateException.class, () -> partida.iniciarPartida(j2.getIdJugador()));

        partida.iniciarPartida(j1.getIdJugador());
        assertEquals(Partida.EstadoDePartida.JUGANDO, partida.getEstado());
        assertEquals(j1.getIdJugador(), partida.getJugadorActual().getIdJugador());

        // Turno de j2 no puede tirar cuando le toca a j1
        assertThrows(IllegalStateException.class, () -> partida.tirarLosDados(j2.getIdJugador()));
    }

    @Test
    void tirarBloquearYPlantarseAvanzaTurno() {
        Partida partida = new Partida();
        Jugador j1 = partida.agregarJugador("P1");
        Jugador j2 = partida.agregarJugador("P2");
        partida.iniciarPartida(j1.getIdJugador());

        // No puede bloquear ni plantarse antes del primer tiro
        assertThrows(IllegalStateException.class, () -> partida.bloqueoDeDados(j1.getIdJugador(), 0));
        assertThrows(IllegalStateException.class, () -> partida.plantarse(j1.getIdJugador()));

        partida.tirarLosDados(j1.getIdJugador());
        assertEquals(1, partida.getTirosEnTurno());

        partida.bloqueoDeDados(j1.getIdJugador(), 0);
        assertTrue(j1.getCubo().getDados()[0].estaBloqueado());

        partida.plantarse(j1.getIdJugador());
        assertEquals(j2.getIdJugador(), partida.getJugadorActual().getIdJugador());
    }
}