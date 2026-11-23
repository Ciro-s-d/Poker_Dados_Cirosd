package com.example.poker_dados_cirosd.controlador;

import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import com.example.poker_dados_cirosd.modelo.IPartida;
import com.example.poker_dados_cirosd.modelo.Jugador;
import com.example.poker_dados_cirosd.modelo.Partida;
import com.example.poker_dados_cirosd.vista.IVista;
import java.rmi.RemoteException;

public class Controlador implements IControladorRemoto {

    private IPartida modelo;
    private IVista vista;
    private Integer idJugadorLocal;

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.modelo = (IPartida) modeloRemoto;
    }

    public void setVista(IVista vista) {
        this.vista = vista;
    }

    public void setIdJugadorLocal(Integer idJugadorLocal) {
        this.idJugadorLocal = idJugadorLocal;
    }

    public Integer getIdJugadorLocal() {
        return idJugadorLocal;
    }

    public void agregarJugador(String nombre) {
        try {
            if (modelo == null) return;
            Jugador nuevo = modelo.agregarJugador(nombre);
            if (this.idJugadorLocal == null && nuevo != null) {
                this.idJugadorLocal = nuevo.getIdJugador();
            }
        } catch (RemoteException | IllegalStateException | IllegalArgumentException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    public void iniciarPartida() {
        try {
            if (modelo == null) return;
            if (idJugadorLocal == null) {
                if (vista != null) vista.mostrarError("Primero debés unirte a la partida con un nombre.");
                return;
            }
            modelo.iniciarPartida(idJugadorLocal);
        } catch (RemoteException | IllegalStateException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    public void tirarDados() {
        try {
            if (modelo == null || !validarJugadorLocal()) return;
            modelo.tirarLosDados(idJugadorLocal);
        } catch (RemoteException | IllegalStateException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    public void alternarBloqueoDado(int indiceDado) {
        try {
            if (modelo == null || !validarJugadorLocal()) return;
            modelo.bloqueoDeDados(idJugadorLocal, indiceDado);
        } catch (RemoteException | IllegalStateException | IllegalArgumentException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    public void subirApuesta(int nuevaApuestaTotal) {
        try {
            if (modelo == null || !validarJugadorLocal()) return;
            modelo.RaiseTrasTirada(idJugadorLocal, nuevaApuestaTotal);
        } catch (RemoteException | IllegalStateException | IllegalArgumentException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    public void igualarOAbandonar(boolean quiereIgualar) {
        try {
            if (modelo == null || !validarJugadorLocal()) return;
            modelo.Call_Fold(idJugadorLocal, quiereIgualar);
        } catch (RemoteException | IllegalStateException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    public void plantarse() {
        try {
            if (modelo == null || !validarJugadorLocal()) return;
            modelo.plantarse(idJugadorLocal);
        } catch (RemoteException | IllegalStateException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    public void resolverEmpate(boolean quiereRepartir) {
        try {
            if (modelo == null || !validarJugadorLocal()) return;
            modelo.solucionEmpate(idJugadorLocal, quiereRepartir);
        } catch (RemoteException | IllegalStateException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    @Override
    public void actualizar(IObservableRemoto observable, Object arg) throws RemoteException {
        if (!(arg instanceof Partida.EventoEnJuego evento)) return;
        try {
            switch (evento) {
                case JUGADORES_CAMBIOS -> {
                    if (vista != null && modelo != null) vista.mostrarSala(modelo.getJugadores());
                }
                case INICIA_PARTIDA, CAMBIA_TURNO, DADOS_TIRADOS, APUESTA_ACTUALIZADA, JUGADOR_ABANDONO ->
                        refrescarMesa();
                case RONDA_FINALIZADA -> {
                    refrescarMesa();
                    if (vista != null && modelo != null) {
                        vista.mostrarResultadoRonda("Ronda finalizada. Pozo actual: " + modelo.getPozo());
                    }
                }
                case PARTIDA_FINALIZADA -> {
                    refrescarMesa();
                    if (vista != null) {
                        vista.mostrarFinPartida("¡Partida finalizada!");
                    }
                }
            }
        } catch (RemoteException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
        }
    }

    private void refrescarMesa() throws RemoteException {
        if (vista == null || modelo == null) return;
        Jugador actual = modelo.getJugadorActual();
        int idConsulta = (idJugadorLocal != null)
                ? idJugadorLocal
                : (actual != null ? actual.getIdJugador() : -1);

        vista.mostrarMesa(
                modelo.getEstado(),
                modelo.getPozo(),
                modelo.getApuestaMaximaActual(),
                actual,
                modelo.getJugadores(),
                modelo.getDadosVisibles(idConsulta),
                modelo.isDadosRevelados()
        );
    }

    private boolean validarJugadorLocal() {
        if (idJugadorLocal == null) {
            if (vista != null) vista.mostrarError("No hay un jugador local identificado.");
            return false;
        }
        return true;
    }

    public int getTirosEnTurno() {
        try {
            if (modelo == null) return 0;
            // Asegurate de tener int getTirosEnTurno() throws RemoteException; en IPartida
            return modelo.getTirosEnTurno();
        } catch (RemoteException e) {
            if (vista != null) vista.mostrarError(e.getMessage());
            return 0;
        }
    }

    public void abandonarPartida() {
        this.idJugadorLocal = null;
        if (vista != null) {
            vista.mostrarMenu();
        }
    }
}