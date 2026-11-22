package com.example.poker_dados_cirosd.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EvaluadorJugadasTest {

    private Dado[] dadosConValores(int v0, int v1, int v2, int v3, int v4) {
        int[] vals = {v0, v1, v2, v3, v4};
        Dado[] dados = new Dado[5];
        for (int i = 0; i < 5; i++) {
            Dado d = new Dado();
            while (d.getValor() != vals[i]) {
                d.desbloquear();
                d.tirar();
            }
            dados[i] = d;
        }
        return dados;
    }

    @Test
    void evaluaPokerReal() {
        ResultadoTirada res = EvaluadorJugadas.evaluar(dadosConValores(6, 6, 6, 6, 6));
        assertEquals("Poker Real", res.getNombreJugada());
        assertEquals(96, res.getPuntaje());
    }

    @Test
    void evaluaPokerCuadruple() {
        ResultadoTirada res = EvaluadorJugadas.evaluar(dadosConValores(4, 4, 4, 4, 2));
        assertEquals("Poker Cuadruple", res.getNombreJugada());
        assertEquals(84, res.getPuntaje());
    }

    @Test
    void evaluaFull() {
        ResultadoTirada res = EvaluadorJugadas.evaluar(dadosConValores(5, 5, 5, 3, 3));
        assertEquals("Full", res.getNombreJugada());
        assertEquals(60 + (5 * 2) + 3, res.getPuntaje());
    }

    @Test
    void evaluaEscaleras() {
        ResultadoTirada mayor = EvaluadorJugadas.evaluar(dadosConValores(3, 4, 5, 6, 1));
        assertEquals("Escalera Mayor", mayor.getNombreJugada());
        assertEquals(50, mayor.getPuntaje());

        ResultadoTirada menor = EvaluadorJugadas.evaluar(dadosConValores(2, 3, 4, 5, 6));
        assertEquals("Escalera Menor", menor.getNombreJugada());
        assertEquals(40, menor.getPuntaje());
    }

    @Test
    void evaluaPiernaDobleParParSimpleYSinJugada() {
        ResultadoTirada pierna = EvaluadorJugadas.evaluar(dadosConValores(4, 4, 4, 2, 5));
        assertEquals("Pierna", pierna.getNombreJugada());
        assertEquals(34, pierna.getPuntaje());

        ResultadoTirada doblePar = EvaluadorJugadas.evaluar(dadosConValores(2, 2, 5, 5, 6));
        assertEquals("Doble par", doblePar.getNombreJugada());
        assertEquals(10 + 5 + 2, doblePar.getPuntaje());

        ResultadoTirada parSimple = EvaluadorJugadas.evaluar(dadosConValores(3, 3, 2, 5, 6));
        assertEquals("Par Simple", parSimple.getNombreJugada());
        assertEquals(3, parSimple.getPuntaje());

        ResultadoTirada sinJugada = EvaluadorJugadas.evaluar(dadosConValores(2, 3, 4, 5, 1));
        assertEquals("Sin Jugada", sinJugada.getNombreJugada());
        assertEquals(0, sinJugada.getPuntaje());
    }
}