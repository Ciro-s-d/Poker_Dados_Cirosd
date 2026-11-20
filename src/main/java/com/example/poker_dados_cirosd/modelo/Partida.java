package com.example.poker_dados_cirosd.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Partida implements Serializable {

    public enum EstadoDePartida{
        SALA,
        JUGANDO,
        EMPATE_EN_CURSO,
        FIN_PARTIDA
    }

    public enum EventoEnJuego{
        JUGADORES_CAMBIOS,
        INICIA_PARTIDA,
        CAMBIA_TURNO,
        DADOS_TIRADOS,
        RONDA_FINALIZADA,
        PARTIDA_FINALIZADA
    }

    //private final List<IObservable> observadores = new ArrayList<>();

    //INICIO DE PARETIDA, CREO LO QUE NECESITO
    private EstadoDePartida estado = EstadoDePartida.SALA;
    private final List<Jugador> jugadores = new ArrayList<>();
    private final Cubo cubo = new Cubo();

    //COSAS DURANTE PARTIDA
    private int siguienteIdJugador = 1;
    private int indiceTurno = 0;
    private int tirosEnTurno = 0;
    private int TirosPorTurno = 2;
    private int pozo = 0;
    private int apuestaInicialRonda = 10;

    //ESTO ES POR SI HAY UN EMPATE GUARDAR LOS EMPATADOS
    private final List<Integer> jugadoresEmpatadosIds = new ArrayList<>();
    private final Map<Integer, Boolean> decisionesEmpate = new HashMap<>(); //TRUE = REPARTE - FALSE = RONDA EXTRA



    //AGREGAR JUGADORES A PARTIDA
    public synchronized Jugador agregarJugador(String nombre){
        if(estado == EstadoDePartida.JUGANDO){
            throw new IllegalStateException("Partida iniciada. Espere.");
        }
        if(nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("El nombre debe ser valido");
        }

        Jugador nuevo = new Jugador(nombre, siguienteIdJugador++);
        jugadores.add(nuevo);
        notificacion(EventoEnJuego.JUGADORES_CAMBIOS);
        return nuevo;
    }


    //INICIO DE LA PARTIDA
    public synchronized void iniciarPartida(int idJugadorSolicitud){
        if(jugadores.isEmpty()){
            throw new IllegalStateException("Debe haber un jugador");
        }

        //BUSCA EL ID DEL PRIMERO QUE INGRESA Y QUIEN PIDIO LA SOLICITUD
        if(jugadores.get(0).getIdJugador() != idJugadorSolicitud){
            throw new IllegalStateException("Solo el admin inicia la partida");
        }

        estado = EstadoDePartida.JUGANDO;
        indiceTurno = 0;
        prepararRonda();
        notificacion(EventoEnJuego.INICIA_PARTIDA);
    }

    //RONDAS
    private void prepararRonda() {
        pozo = 0;
        jugadoresEmpatadosIds.clear();      //ronda nueva, vacio lista empatados
        decisionesEmpate.clear();
        cubo.desbloquearTodosDados();
        tirosEnTurno = 0;


        //APUESTA MINIMA - POZO
        for(Jugador j : jugadores){
            j.resetearPuntos();
            int aporte = Math.min(apuestaInicialRonda, j.getCantidadFichas());
            j.restarFichas(aporte);
            pozo += aporte;
        }
    }

    //TIRAR
    public synchronized void tirarLosDados(int idJugador){
        validarTurno(idJugador);
        if(tirosEnTurno >= TirosPorTurno){
            throw new IllegalStateException("Maximo de tiros");
        }

        cubo.tirar();
        tirosEnTurno++;
        notificacion(EventoEnJuego.DADOS_TIRADOS);
    }

    //BLOQUERA DADOS
    public synchronized void bloqueoDeDados(int idJugador, int indiceDado){
        validarTurno(idJugador);
        if(tirosEnTurno == 0){
            throw new IllegalStateException("Debes tirar una vez para apartar");
        }

        Dado[] dados = cubo.getDados();
        if(indiceDado >= 0 && indiceDado < dados.length){
            dados[indiceDado].setBloqueado(!dados[indiceDado].estaBloqueado()); //bloquear si no esta bloqueado
            notificacion(EventoEnJuego.DADOS_TIRADOS);
        }
    }

    //ME QUEDO CON MIS DADOS
    public synchronized void plantarse(int idjugador){
        validarTurno(idjugador);

        Jugador actual = getJugadorActual();
        ResultadoTirada resultado = EvaluadorJugadas.evaluar(cubo.getDados());  //lllama al evaluador de jugadas
        actual.setUltimaJugada(resultado);
        actual.sumarPuntos(resultado.getPuntaje());

        nuevoTurno_ResultadoRonda();
    }

    //HAY JUGADORES POR TIRAR?
    private void nuevoTurno_ResultadoRonda() {
        indiceTurno++;
        if (indiceTurno < jugadores.size()) {
            cubo.desbloquearTodosDados();
            tirosEnTurno = 0;
            notificacion(EventoEnJuego.CAMBIA_TURNO);
        } else {
            resultadoRonda();
        }
    }

    private void resultadoRonda() {
        int mejorPuntaje = jugadores.stream()
                .mapToInt(Jugador::getPuntajeJugador)
                .max()                                          //Busca entre los jugadores el de mas puntos
                .orElse(0);

        List<Jugador> empatados = jugadores.stream()
                .filter(j -> j.getPuntajeJugador() == mejorPuntaje) //Si hay puntajes iguales los agrega a la lista de empate
                .toList();

        if (empatados.size() > 1) {
            estado = EstadoDePartida.EMPATE_EN_CURSO;
            jugadoresEmpatadosIds.clear();
            decisionesEmpate.clear();
            for (Jugador j : empatados) {
                jugadoresEmpatadosIds.add(j.getIdJugador());
            }
            notificacion(EventoEnJuego.RONDA_FINALIZADA);
        } else if (empatados.size() == 1) {
            empatados.get(0).sumarFichas(pozo);
            pozo = 0;
            //verificarFinPartidaONuevaRonda();
        }
    }

    //SEGUIR CON LA RESPUESTA AL DESEMPATE Y DEMASES


    public Jugador getJugadorActual() {
        return jugadores.get(indiceTurno);
    }


    private void validarTurno(int idJugador) {
    }


    private void notificacion(EventoEnJuego evento) {
        //for (IObservador obs : observadores) {
        //    obs.actualizar(evento);
        //}
    }


}
