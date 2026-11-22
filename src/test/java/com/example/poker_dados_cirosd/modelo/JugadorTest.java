package com.example.poker_dados_cirosd.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JugadorTest {

    @Test
    void inicializaConFichasPorDefectoYCeroPuntos() {
        Jugador j = new Jugador("Ciro", 1);

        assertEquals(1, j.getIdJugador());
        assertEquals("Ciro", j.getnombreJugador());
        assertEquals(250, j.getCantidadFichas());
        assertEquals(0, j.getPuntajeJugador());
        assertNotNull(j.getCubo());
        assertNull(j.getUltimaJugada());
        assertEquals(0, j.getAporteEnVuelta());
    }

    @Test
    void gestionaFichasYPuntosSinValoresNegativosEnFichas() {
        Jugador j = new Jugador("Ana", 2);
        j.sumarFichas(50);
        assertEquals(300, j.getCantidadFichas());

        j.restarFichas(400);
        assertEquals(0, j.getCantidadFichas());

        j.sumarPuntos(35);
        assertEquals(35, j.getPuntajeJugador());
        j.resetearPuntos();
        assertEquals(0, j.getPuntajeJugador());
    }

    @Test
    void prepararParaVueltaRegistrarAporteYAbandonar() {
        Jugador j = new Jugador("Luis", 3);
        j.getCubo().bloquearDado(0);
        j.sumarPuntos(80);
        j.setUltimaJugada(new ResultadoTirada("Pierna", 34));

        j.prepararParaVuelta(true);
        assertTrue(j.isActivoEnVuelta());
        assertEquals(0, j.getPuntajeJugador());
        assertEquals(0, j.getAporteEnVuelta());
        assertNull(j.getUltimaJugada());
        assertFalse(j.getCubo().getDados()[0].estaBloqueado());

        j.registrarAporteVuelta(30);
        assertEquals(30, j.getAporteEnVuelta());
        assertEquals(220, j.getCantidadFichas());

        j.abandonarVuelta();
        assertFalse(j.isActivoEnVuelta());
    }
}