package com.example.poker_dados_cirosd.controlador;

import com.example.poker_dados_cirosd.modelo.IObservador;
import com.example.poker_dados_cirosd.modelo.Jugador;
import com.example.poker_dados_cirosd.modelo.Partida;       //traigo las clases y la interfaz
import com.example.poker_dados_cirosd.vista.IVista;

public class Controlador implements IObservador {       //con implements recibo la info de actualizar

    private final Partida partida;
    private final IVista vista;
    private Integer idJugadorLocal;

    public Controlador(Partida partida, IVista vista) {
        this.partida = partida;
        this.vista = vista;
        this.partida.agregarObservador(this);   //controlador mira la partida y me dice si cabmia algo
    }

    public void setIdJugadorLocal(Integer idJugadorLocal) {
        this.idJugadorLocal = idJugadorLocal;   //separa por jugador
    }

    // LAS ACCIONES QUE VIENEN DESDE VISTA Y YO LLAMO A LOS METODOS PARA QUE ME RESPONDAN
    public void agregarJugador(String nombre) {
        try {
            Jugador nuevo = partida.agregarJugador(nombre);
            if (this.idJugadorLocal == null) {
                this.idJugadorLocal = nuevo.getIdJugador();
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void iniciarPartida() {
        try {
            int idSolicitante = (idJugadorLocal != null) ? idJugadorLocal : 1;
            partida.iniciarPartida(idSolicitante);
        } catch (IllegalStateException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void tirarDados() {
        try {
            partida.tirarLosDados(idJugadorLocal);
        } catch (IllegalStateException | NullPointerException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void alternarBloqueoDado(int indiceDado) {
        try {
            partida.bloqueoDeDados(idJugadorLocal, indiceDado);
        } catch (IllegalStateException | IllegalArgumentException | NullPointerException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void subirApuesta(int nuevaApuestaTotal) {
        try {
            partida.RaiseTrasTirada(idJugadorLocal, nuevaApuestaTotal);
        } catch (IllegalStateException | IllegalArgumentException | NullPointerException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void igualarOAbandonar(boolean quiereIgualar) {
        try {
            partida.Call_Fold(idJugadorLocal, quiereIgualar);
        } catch (IllegalStateException | NullPointerException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void plantarse() {
        try {
            partida.plantarse(idJugadorLocal);
        } catch (IllegalStateException | NullPointerException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void resolverEmpate(boolean quiereRepartir) {
        try {
            partida.solucionEmpate(idJugadorLocal, quiereRepartir);
        } catch (IllegalStateException | NullPointerException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    // ESTAS SON LAS REACCIONES QUE TENGO A LO QUE ME DICE "MODELO" (Le aviso a vista que actualice)
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
}