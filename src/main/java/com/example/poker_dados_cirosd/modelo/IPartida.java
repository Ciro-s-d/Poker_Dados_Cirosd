package com.example.poker_dados_cirosd.modelo;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import java.rmi.RemoteException;
import java.util.List;

public interface IPartida extends IObservableRemoto {
    Jugador agregarJugador(String nombre) throws RemoteException;
    void iniciarPartida(int idJugadorSolicitud) throws RemoteException;
    void tirarLosDados(int idJugador) throws RemoteException;
    void bloqueoDeDados(int idJugador, int indiceDado) throws RemoteException;
    void RaiseTrasTirada(int idJugador, int nuevaApuestaTotal) throws RemoteException;
    void Call_Fold(int idJugador, boolean quiereIgualar) throws RemoteException;
    void plantarse(int idJugador) throws RemoteException;
    void solucionEmpate(int idJugador, boolean quiereRepartir) throws RemoteException;
    List<Jugador> getJugadores() throws RemoteException;
    Jugador getJugadorActual() throws RemoteException;
    Partida.EstadoDePartida getEstado() throws RemoteException;
    int getPozo() throws RemoteException;
    int getApuestaMaximaActual() throws RemoteException;
    Dado[] getDadosVisibles(int idJugadorConsulta) throws RemoteException;
    boolean isDadosRevelados() throws RemoteException;
    int getTirosEnTurno() throws RemoteException;
}
