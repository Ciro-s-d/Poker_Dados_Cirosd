package com.example.poker_dados_cirosd.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultadoTiradaTest {

    @Test
    void creaResultadoYDevuelveNombrePuntajeYToString() {
        ResultadoTirada res = new ResultadoTirada("Full", 74);

        assertEquals("Full", res.getNombreJugada());
        assertEquals(74, res.getPuntaje());
        assertEquals("Full (puntaje: 74)", res.toString());
    }
}