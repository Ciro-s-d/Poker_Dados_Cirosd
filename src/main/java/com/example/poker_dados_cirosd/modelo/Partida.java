package com.example.poker_dados_cirosd.modelo;

import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

//PARA RMI EXTIENDE observableRemoto e IMPLEMENTA IPartida
public class Partida extends ObservableRemoto implements IObservable, IPartida, Serializable {

    public enum EstadoDePartida implements Serializable{
        SALA,
        JUGANDO,
        EMPATE_EN_CURSO,
        FIN_PARTIDA
    }

    public enum EventoEnJuego implements Serializable{
        JUGADORES_CAMBIOS,
        INICIA_PARTIDA,
        CAMBIA_TURNO,
        DADOS_TIRADOS,
        RONDA_FINALIZADA,
        PARTIDA_FINALIZADA,
        APUESTA_ACTUALIZADA,
        JUGADOR_ABANDONO
    }

    private final transient List<IObservador> observadores = new ArrayList<>();

    @Override                   //necesario implementar observadores
    public void agregarObservador(IObservador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void quitarObservador(IObservador observador) {
        observadores.remove(observador);
    }

    //INICIO DE PARETIDA, CREO LO QUE NECESITO
    private EstadoDePartida estado = EstadoDePartida.SALA;
    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Jugador> participantesVuelta = new ArrayList<>();
    private boolean faseSoloIgualarOAbandonar = false;
    private final Set<Integer> jugadoresEnPartidaIds = new LinkedHashSet<>();

    //COSAS DURANTE PARTIDA
    private int siguienteIdJugador = 1;
    private int indiceTurno = 0;
    private int tirosEnTurno = 0;
    private int TirosPorTurno = 2;
    private int pozo = 0;
    private int apuestaInicialRonda = 10;
    private int apuestaMaximaActual = 10;   //esto se modifica a medida que se hacen los call
    private boolean dadosRevelados = false; //se revelaron los dados ya o no?

    //ESTO ES POR SI HAY UN EMPATE GUARDAR LOS EMPATADOS
    private final List<Integer> jugadoresEmpatadosIds = new ArrayList<>();
    private final Map<Integer, Boolean> decisionesEmpate = new HashMap<>(); //TRUE = REPARTE - FALSE = RONDA EXTRA



    //AGREGAR JUGADORES A PARTIDA
    public synchronized Jugador agregarJugador(String nombre) {
        if (estado != EstadoDePartida.SALA) {
            throw new IllegalStateException("Partida iniciada. Espere.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre debe ser valido");
        }

        String nombreLimpio = nombre.trim();
        for (Jugador j : jugadores) {
            if (j.getnombreJugador() != null && j.getnombreJugador().equalsIgnoreCase(nombreLimpio)) {
                throw new IllegalArgumentException("Ya existe un jugador con ese nombre.");
            }
        }

        Jugador nuevo = new Jugador(nombreLimpio, siguienteIdJugador++);
        jugadoresEnPartidaIds.add(nuevo.getIdJugador());
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
        prepararRondaNormal();
        notificacion(EventoEnJuego.INICIA_PARTIDA);
    }

    //RONDAS
    private void prepararRondaNormal() {
        participantesVuelta.clear();
        for (Jugador j : jugadores) {
            if (j.getCantidadFichas() > 0) {
                participantesVuelta.add(j);
            }
        }
        iniciarVueltaCon(participantesVuelta, apuestaInicialRonda);
    }

    private void prepararDesempate() {
        // Solo participan quienes empataron en la vuelta anterior
        List<Jugador> empatadosActivos = new ArrayList<>();
        for (Integer id : jugadoresEmpatadosIds) {
            Jugador j = getJugadorPorId(id);
            if (j != null && j.getCantidadFichas() > 0) {
                empatadosActivos.add(j);
            }
        }
        participantesVuelta.clear();
        participantesVuelta.addAll(empatadosActivos);
        iniciarVueltaCon(participantesVuelta, apuestaInicialRonda);
    }

    private void iniciarVueltaCon(List<Jugador> participantes, int montoBase) {
        pozo = 0;
        jugadoresEmpatadosIds.clear();
        decisionesEmpate.clear();
        dadosRevelados = false; // Los dados empiezan secretos hasta la resolución
        indiceTurno = 0;
        tirosEnTurno = 0;
        apuestaMaximaActual = montoBase;
        this.faseSoloIgualarOAbandonar = false;

        for (Jugador j : participantes) {
            j.prepararParaVuelta(true);         //aportes iniciales, y calls
            int aportePrevio = j.getAporteEnVuelta();
            int faltante = Math.max(0, apuestaMaximaActual - aportePrevio);
            int aporteReal = Math.min(faltante, j.getCantidadFichas());
            j.registrarAporteVuelta(aporteReal);
            pozo += aporteReal;
        }
    }

    //TIRAR
    @Override
    public synchronized void tirarLosDados(int idJugador) {
        validarTurno(idJugador);
        if (faseSoloIgualarOAbandonar) {
            throw new IllegalStateException("Hubo una subida: solo podés igualar o abandonar esta ronda con tus dados bloqueados.");
        }
        if (tirosEnTurno >= TirosPorTurno) {
            throw new IllegalStateException("Maximo de tiros");
        }

        Jugador actual = getJugadorActual();
        actual.getCubo().tirar();
        tirosEnTurno++;
        notificacion(EventoEnJuego.DADOS_TIRADOS);
    }

    //BLOQUERA DADOS
    @Override
    public synchronized void bloqueoDeDados(int idJugador, int indiceDado) {
        validarTurno(idJugador);
        if (faseSoloIgualarOAbandonar) {
            throw new IllegalStateException("No podés modificar el bloqueo de dados mientras respondés a una subida.");
        }
        if (tirosEnTurno == 0) {
            throw new IllegalStateException("Debes tirar una vez para apartar");
        }
        Jugador actual = getJugadorActual();
        Dado[] dados = actual.getCubo().getDados();

        if (indiceDado < 0 || indiceDado >= dados.length) {
            throw new IllegalArgumentException("Índice de dado inválido.");
        }

        dados[indiceDado].setBloqueado(!dados[indiceDado].estaBloqueado());
        notificacion(EventoEnJuego.DADOS_TIRADOS);
    }

    //CALLS, RAISE Y FOLD
    @Override
    public synchronized void RaiseTrasTirada(int idJugador, int nuevaApuestaTotalVuelta) {
        validarTurno(idJugador);
        if (faseSoloIgualarOAbandonar) {
            throw new IllegalStateException("Solo podés igualar o abandonar esta ronda.");
        }
        if (tirosEnTurno < 1) {
            throw new IllegalStateException("Debés realizar al menos tu primera tirada antes de subir o confirmar apuesta.");
        }

        Jugador actual = getJugadorActual();
        if (nuevaApuestaTotalVuelta <= apuestaMaximaActual) {
            throw new IllegalArgumentException("La subida debe ser mayor a la apuesta actual (" + apuestaMaximaActual + ").");
        }

        int diferenciaNecesaria = nuevaApuestaTotalVuelta - actual.getAporteEnVuelta();
        if (diferenciaNecesaria > actual.getCantidadFichas()) {
            throw new IllegalStateException("Sin fichas suficientes para esa apuesta.");
        }

        if (diferenciaNecesaria > 0) {
            actual.registrarAporteVuelta(diferenciaNecesaria);
            pozo += diferenciaNecesaria;
        }

        apuestaMaximaActual = nuevaApuestaTotalVuelta;

        // Evaluamos y fijamos la jugada de quien sube
        if (actual.getUltimaJugada() == null) {
            ResultadoTirada res = EvaluadorJugadas.evaluar(actual.getCubo().getDados());
            actual.setUltimaJugada(res);
            actual.sumarPuntos(res.getPuntaje());
        }

        notificacion(EventoEnJuego.APUESTA_ACTUALIZADA);
        avanzarTurnoTrasSubida();
    }

    private void avanzarTurnoTrasSubida() {
        List<Jugador> activos = getJugadoresActivosEnVuelta();
        if (activos.size() <= 1) {
            nuevoTurno_ResultadoRonda();
            return;
        }

        Jugador siguiente = buscarSiguienteJugadorPendiente(activos);
        if (siguiente != null) {
            configurarTurnoPara(siguiente);
            notificacion(EventoEnJuego.CAMBIA_TURNO);
        } else {
            faseSoloIgualarOAbandonar = false;
            resolverRonda();
        }
    }

    @Override
    public synchronized void Call_Fold(int idJugador, boolean quiereIgualar) {
        validarTurno(idJugador);
        if (!faseSoloIgualarOAbandonar && tirosEnTurno < 1) {
            throw new IllegalStateException("Debés realizar al menos tu primera tirada antes de igualar o abandonar.");
        }

        Jugador actual = getJugadorActual();

        if (!quiereIgualar) {
            actual.abandonarVuelta();
            notificacion(EventoEnJuego.JUGADOR_ABANDONO);
            nuevoTurno_ResultadoRonda();
            return;
        }

        int faltante = Math.max(0, apuestaMaximaActual - actual.getAporteEnVuelta());
        if (faltante > actual.getCantidadFichas()) {
            throw new IllegalStateException("Sin fichas para igualar la apuesta actual (" + apuestaMaximaActual + "). Debés abandonar.");
        }

        if (faltante > 0) {
            actual.registrarAporteVuelta(faltante);
            pozo += faltante;
            notificacion(EventoEnJuego.APUESTA_ACTUALIZADA);
        }

        if (actual.getUltimaJugada() == null) {
            ResultadoTirada res = EvaluadorJugadas.evaluar(actual.getCubo().getDados());
            actual.setUltimaJugada(res);
            actual.sumarPuntos(res.getPuntaje());
        }

        nuevoTurno_ResultadoRonda();
    }

    //ME QUEDO CON MIS DADOS
    @Override
    public synchronized void plantarse(int idJugador) {
        validarTurno(idJugador);
        if (faseSoloIgualarOAbandonar) {
            throw new IllegalStateException("Hubo una subida: solo podés igualar o abandonar esta ronda.");
        }
        if (tirosEnTurno < 1) {
            throw new IllegalStateException("Debés tirar al menos una vez antes de plantarte.");
        }

        Jugador actual = getJugadorActual();
        if (actual.getAporteEnVuelta() < apuestaMaximaActual && actual.getCantidadFichas() > 0) {
            throw new IllegalStateException("Debés igualar la apuesta actual (" + apuestaMaximaActual + ") o abandonar antes de plantarte.");
        }

        ResultadoTirada resultado = EvaluadorJugadas.evaluar(actual.getCubo().getDados());
        actual.setUltimaJugada(resultado);
        actual.sumarPuntos(resultado.getPuntaje());

        nuevoTurno_ResultadoRonda();
    }
    //HAY JUGADORES POR TIRAR?
    private void nuevoTurno_ResultadoRonda() {
        List<Jugador> activos = getJugadoresActivosEnVuelta();

        if (activos.size() == 1) {
            dadosRevelados = true;
            Jugador unicoGanador = activos.get(0);
            unicoGanador.sumarFichas(pozo);
            pozo = 0;
            faseSoloIgualarOAbandonar = false;
            verificar_FinPartida_NuevaRonda();
            return;
        }

        Jugador siguiente = buscarSiguienteJugadorPendiente(activos);
        if (siguiente != null) {
            configurarTurnoPara(siguiente);
            notificacion(EventoEnJuego.CAMBIA_TURNO);
        } else {
            faseSoloIgualarOAbandonar = false;
            resolverRonda();
        }
    }

    //SIN ESTO, SI YO TENGO UN RAISE ANTES DE MI MANO NO ME DEJA TIRAR
    private void configurarTurnoPara(Jugador siguiente) {
        indiceTurno = participantesVuelta.indexOf(siguiente);
        tirosEnTurno = 0;
        boolean todosYaJugaronSuMano = participantesVuelta.stream()
                .filter(j -> j.isActivoEnVuelta() && jugadoresEnPartidaIds.contains(j.getIdJugador()))
                .allMatch(j -> j.getUltimaJugada() != null);
        boolean debeIgualar = siguiente.getAporteEnVuelta() < apuestaMaximaActual && siguiente.getCantidadFichas() > 0;
        // Solo entra en modo "igualar o abandonar" en la 2da pasada, una vez que todos ya tiraron/jugaron su mano
        faseSoloIgualarOAbandonar = todosYaJugaronSuMano && debeIgualar;
    }

    private Jugador buscarSiguienteJugadorPendiente(List<Jugador> activos) {
        int total = participantesVuelta.size();
        boolean todosYaJugaronSuMano = activos.stream()
                .allMatch(j -> j.getUltimaJugada() != null);

        for (int offset = 1; offset <= total; offset++) {
            int candidatoIdx = (indiceTurno + offset) % total;
            Jugador candidato = participantesVuelta.get(candidatoIdx);
            if (!candidato.isActivoEnVuelta() || !jugadoresEnPartidaIds.contains(candidato.getIdJugador())) {
                continue;
            }

            // 1ra pasada: prioridad a quien todavía no jugó su mano (no tiró/plantó/subió)
            if (!todosYaJugaronSuMano) {
                if (candidato.getUltimaJugada() == null) {
                    return candidato;
                }
            } else {
                // 2da pasada: solo quienes quedaron por debajo de la apuesta máxima actual
                boolean debeIgualar = candidato.getAporteEnVuelta() < apuestaMaximaActual && candidato.getCantidadFichas() > 0;
                if (debeIgualar) {
                    return candidato;
                }
            }
        }
        return null;
    }

    private void resolverRonda() {
        dadosRevelados = true;

        List<Jugador> competidores = new ArrayList<>();
        for (Jugador j : participantesVuelta) {
            if (j.isActivoEnVuelta() && jugadoresEnPartidaIds.contains(j.getIdJugador())) {
                if (j.getUltimaJugada() == null) {
                    ResultadoTirada res = EvaluadorJugadas.evaluar(j.getCubo().getDados());
                    j.setUltimaJugada(res);
                    j.sumarPuntos(res.getPuntaje());
                }
                competidores.add(j);
            }
        }

        if (competidores.isEmpty()) {
            verificar_FinPartida_NuevaRonda();
            return;
        }

        int mejorPuntaje = competidores.stream()
                .mapToInt(Jugador::getPuntajeJugador)
                .max()
                .orElse(0);

        List<Jugador> empatados = competidores.stream()
                .filter(j -> j.getPuntajeJugador() == mejorPuntaje)
                .toList();

        if (empatados.size() > 1) {
            estado = EstadoDePartida.EMPATE_EN_CURSO;
            jugadoresEmpatadosIds.clear();
            decisionesEmpate.clear();
            for (Jugador j : empatados) {
                jugadoresEmpatadosIds.add(j.getIdJugador());
            }
            notificacion(EventoEnJuego.RONDA_FINALIZADA);
        } else {
            Jugador ganadorRonda = empatados.get(0);
            ganadorRonda.sumarFichas(pozo);
            pozo = 0;
            verificar_FinPartida_NuevaRonda();
        }
    }

    @Override
    public synchronized void abandonarPartida(int idJugador) {
        Jugador saliente = getJugadorPorId(idJugador);
        if (saliente == null) {
            return;
        }

        jugadoresEnPartidaIds.remove(idJugador);
        saliente.abandonarVuelta();

        if (estado == EstadoDePartida.JUGANDO || estado == EstadoDePartida.EMPATE_EN_CURSO) {
            List<Jugador> restantesEnPartida = jugadores.stream()
                    .filter(j -> jugadoresEnPartidaIds.contains(j.getIdJugador()))
                    .toList();

            if (restantesEnPartida.size() == 1) {
                Jugador ultimoEnPie = restantesEnPartida.get(0);
                ultimoEnPie.sumarFichas(pozo);
                pozo = 0;
                estado = EstadoDePartida.FIN_PARTIDA;
                notificacion(EventoEnJuego.PARTIDA_FINALIZADA);
                return;
            }

            if (restantesEnPartida.isEmpty()) {
                pozo = 0;
                estado = EstadoDePartida.FIN_PARTIDA;
                notificacion(EventoEnJuego.PARTIDA_FINALIZADA);
                return;
            }

            notificacion(EventoEnJuego.JUGADOR_ABANDONO);
            nuevoTurno_ResultadoRonda();
        } else {
            notificacion(EventoEnJuego.JUGADORES_CAMBIOS);
        }
    }

    //SEGUIR CON LA RESPUESTA AL DESEMPATE
    public synchronized void solucionEmpate(int idJugador, boolean quiereRepartir) {
        if (estado != EstadoDePartida.EMPATE_EN_CURSO) {    // debe existir un empate activo
            throw new IllegalStateException("No hay ningún empate en curso.");
        }

        if (!jugadoresEmpatadosIds.contains(idJugador)) {   // solo los jugadores que empataron
            throw new IllegalStateException("No participás de este empate.");
        }

        if (!quiereRepartir) {
            estado = EstadoDePartida.JUGANDO;       //si uno no reparte se juega
            prepararDesempate();
            notificacion(EventoEnJuego.CAMBIA_TURNO);
            return;
        }

        decisionesEmpate.put(idJugador, true);

        if (decisionesEmpate.size() == jugadoresEmpatadosIds.size()) {  //ambos de acuerdo, se reparte
            int parte = pozo / jugadoresEmpatadosIds.size();
            int resto = pozo % jugadoresEmpatadosIds.size();
            for (int i = 0; i < jugadoresEmpatadosIds.size(); i++) {
                Jugador j = getJugadorPorId(jugadoresEmpatadosIds.get(i));
                if (j != null) {
                    j.sumarFichas(parte + (i == 0 ? resto : 0));
                }
            }
            pozo = 0;
            verificar_FinPartida_NuevaRonda();
        }
    }

    //VERIFICA SI HAY MAS DE 1 JUGADOR CON FICHAS
    private void verificar_FinPartida_NuevaRonda() {
        List<Jugador> restantes = jugadores.stream()
                .filter(j -> jugadoresEnPartidaIds.contains(j.getIdJugador()))
                .toList();

        long jugadoresConFichas = restantes.stream()
                .filter(j -> j.getCantidadFichas() > 0)
                .count();

        if (restantes.size() <= 1 || (jugadores.size() > 1 && jugadoresConFichas <= 1)) {
            estado = EstadoDePartida.FIN_PARTIDA;
            notificacion(EventoEnJuego.PARTIDA_FINALIZADA);
        } else {
            estado = EstadoDePartida.JUGANDO;
            prepararRondaNormal();
            notificacion(EventoEnJuego.RONDA_FINALIZADA);
        }
    }

    private void validarTurno(int idJugador) {
        if (estado != EstadoDePartida.JUGANDO) {
            throw new IllegalStateException("La partida no está en curso.");
        }
        Jugador actual = getJugadorActual();

        if (actual == null || actual.getIdJugador() != idJugador) {
            throw new IllegalStateException("No es tu turno.");
        }

        if (!actual.isActivoEnVuelta()) {
            throw new IllegalStateException("Ya abandonaste esta ronda.");
        }
    }

    //ME BLOQUE OTODO MENOS IGUALAR O ABANDONAR LUEGO DE MIS TIROS SI HAY APUESTA MAS ALTA
    @Override
    public boolean isSoloIgualarOAbandonar() {
        return faseSoloIgualarOAbandonar;
    }

    //GETTERS / SETTERS
    private List<Jugador> getJugadoresActivosEnVuelta() {
        return participantesVuelta.stream()
                .filter(Jugador::isActivoEnVuelta)
                .toList();
    }

    private Jugador getJugadorPorId(Integer id) {
        return jugadores.stream().filter(j -> j.getIdJugador() == id).findFirst().orElse(null);
    }

    public Jugador getJugadorActual() {
        // Validación: evitar IndexOutOfBoundsException si la lista está vacía o el índice está fuera de rango
        if (participantesVuelta.isEmpty() || indiceTurno < 0 || indiceTurno >= participantesVuelta.size()) {
            return null;
        }
        return participantesVuelta.get(indiceTurno);
    }

    public EstadoDePartida getEstado() {
        return estado;
    }

    public List<Jugador> getJugadores() {
        return new ArrayList<>(jugadores);
    }

    public int getPozo() {
        return pozo;
    }

    public int getTirosEnTurno() {
        return tirosEnTurno;
    }

    //como ahora no tengo cubo gheneral tengo que validar si es un cubo habilitado o no
    public Dado[] getDadosVisibles(int idJugadorConsultante) {
        Jugador objetivo = getJugadorPorId(idJugadorConsultante);
        if (objetivo == null) {
            return null;
        }
        Jugador actual = getJugadorActual();
        boolean esPropioTurno = actual != null && actual.getIdJugador() == idJugadorConsultante;
        if (dadosRevelados || esPropioTurno) {
            return objetivo.getCubo().getDados();
        }
        return null;
    }

    public int getApuestaMaximaActual() {
        return apuestaMaximaActual;
    }

    public boolean isDadosRevelados() {
        return dadosRevelados;
    }

    public Map<Integer, Boolean> getDecisionesEmpate() {
        return new LinkedHashMap<>(decisionesEmpate);
    }

    private void notificacion(EventoEnJuego evento) {
        try {
            notificarObservadores(evento);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
