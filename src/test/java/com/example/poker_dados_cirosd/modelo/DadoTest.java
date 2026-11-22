package com.example.poker_dados_cirosd.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DadoTest {

    @Test
    void constructorInicializaDesbloqueadoYConValorValido() {
        Dado dado = new Dado();
        assertFalse(dado.estaBloqueado());
        assertTrue(dado.getValor() >= 1 && dado.getValor() <= 6);
    }

    @Test
    void tirarModificaValorDentroDelRangoCuandoEstaDesbloqueado() {
        Dado dado = new Dado();
        dado.desbloquear();
        for (int i = 0; i < 50; i++) {
            dado.tirar();
            assertTrue(dado.getValor() >= 1 && dado.getValor() <= 6);
        }
    }

    @Test
    void tirarNoCambiaElValorCuandoEstaBloqueado() {
        Dado dado = new Dado();
        dado.desbloquear();
        dado.tirar();
        int valorFijo = dado.getValor();

        dado.bloquear();
        assertTrue(dado.estaBloqueado());

        for (int i = 0; i < 20; i++) {
            dado.tirar();
            assertEquals(valorFijo, dado.getValor());
        }
    }

    @Test
    void bloquearDesbloquearYSetBloqueadoFuncionanCorrectamente() {
        Dado dado = new Dado();

        dado.bloquear();
        assertTrue(dado.estaBloqueado());

        dado.desbloquear();
        assertFalse(dado.estaBloqueado());

        dado.setBloqueado(true);
        assertTrue(dado.estaBloqueado());

        dado.setBloqueado(false);
        assertFalse(dado.estaBloqueado());
    }
}