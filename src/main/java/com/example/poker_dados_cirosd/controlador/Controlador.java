package com.example.poker_dados_cirosd.controlador;

import com.example.poker_dados_cirosd.modelo.IObservador;
import com.example.poker_dados_cirosd.modelo.Jugador;
import com.example.poker_dados_cirosd.modelo.Partida;
import com.example.poker_dados_cirosd.vista.IVista;

public class Controlador implements IObservador {

    private final Partida partida;
    private final IVista vista;
    private Integer idJugadorLocal;

    public Controlador(Partida partida, IVista vista) {
        this.partida = partida;
        this.vista = vista;
        this.partida.agregarObservador(this);
    }

    public void setIdJugadorLocal(Integer idJugadorLocal) {
        this.idJugadorLocal = idJugadorLocal;
    }

    public Integer getIdJugadorLocal() {
        return idJugadorLocal;
    }

    public void agregarJugador(String nombre) {
        try {
            Jugador nuevo = partida.agregarJugador(nombre);
            if (this.idJugadorLocal == null && nuevo != null) {
                this.idJugadorLocal = nuevo.getIdJugador();
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void iniciarPartida() {
        try {
            if (idJugadorLocal == null) {
                vista.mostrarError("Primero debés unirte a la partida con un nombre.");
                return;
            }
            partida.iniciarPartida(idJugadorLocal);
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void tirarDados() {
        try {
            if (!validarJugadorLocal()) return;
            partida.tirarLosDados(idJugadorLocal);
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void alternarBloqueoDado(int indiceDado) {
        try {
            if (!validarJugadorLocal()) return;
            partida.bloqueoDeDados(idJugadorLocal, indiceDado);
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void subirApuesta(int nuevaApuestaTotal) {
        try {
            if (!validarJugadorLocal()) return;
            partida.RaiseTrasTirada(idJugadorLocal, nuevaApuestaTotal);
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void igualarOAbandonar(boolean quiereIgualar) {
        try {
            if (!validarJugadorLocal()) return;
            partida.Call_Fold(idJugadorLocal, quiereIgualar);
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void plantarse() {
        try {
            if (!validarJugadorLocal()) return;
            partida.plantarse(idJugadorLocal);
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void resolverEmpate(boolean quiereRepartir) {
        try {
            if (!validarJugadorLocal()) return;
            partida.solucionEmpate(idJugadorLocal, quiereRepartir);
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private boolean validarJugadorLocal() {
        if (idJugadorLocal == null) {
            vista.mostrarError("No hay un jugador local identificado.");
            return false;
        }
        return true;
    }

    @Override
    public void actualizar(Partida.EventoEnJuego evento) {
        switch (evento) {
            case JUGADORES_CAMBIOS -> vista.mostrarSala(partida.getJugadores());
            case INICIA_PARTIDA, CAMBIA_TURNO, DADOS_TIRADOS, APUESTA_ACTUALIZADA, JUGADOR_ABANDONO ->
                    refrescarMesa();
            case RONDA_FINALIZADA -> {
                refrescarMesa();
                vista.mostrarResultadoRonda("Ronda finalizada. Pozo actual: " + partida.getPozo());
            }
            case PARTIDA_FINALIZADA -> {
                refrescarMesa();
                vista.mostrarFinPartida("¡Partida finalizada!");
            }
        }
    }

    private void refrescarMesa() {
        int idConsulta = (idJugadorLocal != null)
                ? idJugadorLocal
                : (partida.getJugadorActual() != null ? partida.getJugadorActual().getIdJugador() : -1);

        vista.mostrarMesa(
                partida.getEstado(),
                partida.getPozo(),
                partida.getApuestaMaximaActual(),
                partida.getJugadorActual(),
                partida.getJugadores(),
                partida.getDadosVisibles(idConsulta),
                partida.isDadosRevelados()
        );
    }

    public int getTirosEnTurno() {
        return partida.getTirosEnTurno();
    }

    public void abandonarPartida() {
        if (vista != null) {
            vista.mostrarMenu();
        }
    }
}