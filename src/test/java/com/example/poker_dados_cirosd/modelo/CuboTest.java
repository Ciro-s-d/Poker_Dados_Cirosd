package com.example.poker_dados_cirosd.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CuboTest {

    @Test
    void constructorPorDefectoCreaCincoDadosDesbloqueados() {
        Cubo cubo = new Cubo();
        assertEquals(5, cubo.getDados().length);
        for (Dado d : cubo.getDados()) {
            assertNotNull(d);
            assertFalse(d.estaBloqueado());
            assertTrue(d.getValor() >= 1 && d.getValor() <= 6);
        }
    }

    @Test
    void constructorConCantidadPersonalizadaCreaCantidadIndicada() {
        Cubo cubo = new Cubo(3);
        assertEquals(3, cubo.getDados().length);
    }

    @Test
    void bloquearYDesbloquearDadoRespetanIndicesYLimites() {
        Cubo cubo = new Cubo();

        cubo.bloquearDado(1);
        assertTrue(cubo.getDados()[1].estaBloqueado());
        assertFalse(cubo.getDados()[0].estaBloqueado());

        cubo.desbloquearDado(1);
        assertFalse(cubo.getDados()[1].estaBloqueado());

        // Índices fuera de rango no deben lanzar excepción
        assertDoesNotThrow(() -> cubo.bloquearDado(-1));
        assertDoesNotThrow(() -> cubo.bloquearDado(99));
        assertDoesNotThrow(() -> cubo.desbloquearDado(-1));
        assertDoesNotThrow(() -> cubo.desbloquearDado(99));
    }

    @Test
    void desbloquearTodosDadosLiberaTodosLosDados() {
        Cubo cubo = new Cubo();
        cubo.bloquearDado(0);
        cubo.bloquearDado(2);
        cubo.bloquearDado(4);

        cubo.desbloquearTodosDados();

        for (Dado d : cubo.getDados()) {
            assertFalse(d.estaBloqueado());
        }
    }

    @Test
    void tirarRespetaDadoBloqueado() {
        Cubo cubo = new Cubo();
        cubo.getDados()[0].setBloqueado(false);
        cubo.getDados()[0].tirar();
        int valorFijo = cubo.getDados()[0].getValor();
        cubo.bloquearDado(0);

        for (int i = 0; i < 20; i++) {
            cubo.tirar();
            assertEquals(valorFijo, cubo.getDados()[0].getValor());
        }
    }
}