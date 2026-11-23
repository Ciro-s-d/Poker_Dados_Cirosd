package com.example.poker_dados_cirosd.controlador;

import com.example.poker_dados_cirosd.modelo.Dado;
import com.example.poker_dados_cirosd.modelo.Jugador;
import com.example.poker_dados_cirosd.modelo.Partida;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.List;

public class mesaControlador {

    // TARJETAS DE LOS JUGADORES ARRIBA
    @FXML private HBox cardJugador0, cardJugador1, cardJugador2, cardJugador3;
    @FXML private ImageView imgJugador0, imgJugador1, imgJugador2, imgJugador3;
    @FXML private Label lblNombreJugador0, lblNombreJugador1, lblNombreJugador2, lblNombreJugador3;
    @FXML private Label lblFichasJugador0, lblFichasJugador1, lblFichasJugador2, lblFichasJugador3;
    @FXML private Label lblApuestaJugador0, lblApuestaJugador1, lblApuestaJugador2, lblApuestaJugador3;
    @FXML private Label lblJugadaJugador0, lblJugadaJugador1, lblJugadaJugador2, lblJugadaJugador3;

    // LABELS DE TODA LA PANTALLA (INFORMACION)
    @FXML private ImageView imgPozo;
    @FXML private Label lblPozo;
    @FXML private Label lblApuestaMaxima;
    @FXML private Label lblCantidadFichas;
    @FXML private Label lblTiros;
    @FXML private Label lblEsTuTurno;

    // DADOS
    @FXML private Button btnDado0, btnDado1, btnDado2, btnDado3, btnDado4;

    // BOTONES QUE APARECEN SIEMPRE
    @FXML private Button btnTirar;
    @FXML private Button btnRetirarse;
    @FXML private Button btnSubir;
    @FXML private Button btnIgualar;
    @FXML private Button btnAbandonarPartida;

    // BOTONES Y LABEL DE SUBIR APUESTA
    @FXML private Pane panelSubir;
    @FXML private TextField txtMontoSubir;
    @FXML private Button btnConfirmarSubir;
    @FXML private Button btnCancelarSubir;

    // VENTANA DE EMPATE
    @FXML private Pane panelEmpate;
    @FXML private Label lblMensajeEmpate;
    @FXML private Button btnRepartir;
    @FXML private Button btnRejugar;

    private Controlador controlador;
    private Jugador jugadorLocal; //el que mira

    private HBox[] tarjetasJugadores;
    private ImageView[] imagenesJugadores;
    private Label[] labelsNombre;
    private Label[] labelsFichas;
    private Label[] labelsApuesta;
    private Label[] labelsJugada;
    private Button[] botonesDados;

    @FXML
    public void initialize() {
        tarjetasJugadores = new HBox[]{cardJugador0, cardJugador1, cardJugador2, cardJugador3};
        imagenesJugadores = new ImageView[]{imgJugador0, imgJugador1, imgJugador2, imgJugador3};
        labelsNombre = new Label[]{lblNombreJugador0, lblNombreJugador1, lblNombreJugador2, lblNombreJugador3};
        labelsFichas = new Label[]{lblFichasJugador0, lblFichasJugador1, lblFichasJugador2, lblFichasJugador3};
        labelsApuesta = new Label[]{lblApuestaJugador0, lblApuestaJugador1, lblApuestaJugador2, lblApuestaJugador3};
        labelsJugada = new Label[]{lblJugadaJugador0, lblJugadaJugador1, lblJugadaJugador2, lblJugadaJugador3};
        botonesDados = new Button[]{btnDado0, btnDado1, btnDado2, btnDado3, btnDado4};

        setVisibleManaged(panelSubir, false);
        setVisibleManaged(panelEmpate, false);      //solo se activan cuando es necesario sino estab ocyultos
        setVisibleManaged(lblEsTuTurno, false);
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public void setJugadorLocal(Jugador jugadorLocal) {
        this.jugadorLocal = jugadorLocal;
    }


    // ACTUALIZACIÓN DE LA MESA
    public void actualizarMesa(Partida.EstadoDePartida estado,
                               int pozo,
                               int apuestaMaxima,
                               Jugador jugadorActual,
                               List<Jugador> jugadores,
                               Dado[] dadosVisibles,
                               boolean dadosRevelados) {

        if (lblPozo != null) {
            lblPozo.setText("POZO: " + pozo);
        }
        if (lblApuestaMaxima != null) {
            lblApuestaMaxima.setText("Apuesta maxima: " + apuestaMaxima);
        }


        Jugador local = (jugadorLocal != null) ? jugadorLocal : jugadorActual;
        if (lblCantidadFichas != null && local != null) {

            lblCantidadFichas.setText("Cantidad de fichas: " + local.getCantidadFichas());
        }

        boolean esMiTurno = (jugadorActual != null && jugadorActual.equals(local));
        setVisibleManaged(lblEsTuTurno, esMiTurno);

        // Tiros restantes
        if (lblTiros != null) {
            int tiros = (controlador != null) ? controlador.getTirosEnTurno() : 0;
            lblTiros.setText("Tiros: " + tiros);
        }

        // ACTUALIZA JUGADORES
        actualizarTarjetasJugadores(jugadores, jugadorActual);

        // ACTUALIZA DADOS
        actualizarDados(dadosVisibles, dadosRevelados, esMiTurno);

        // MUESTRO O SACO EL PANEL DE EMPATE SEGUN LO NECESITO
        boolean hayEmpate = (estado == Partida.EstadoDePartida.EMPATE_EN_CURSO);
        setVisibleManaged(panelEmpate, hayEmpate);

        // HABILITA O NO LOS BOTONES SI ES TU TURNO/ HAY EMPATE
        boolean puedeActuar = esMiTurno && !hayEmpate;
        if (btnTirar != null) btnTirar.setDisable(!puedeActuar);
        if (btnRetirarse != null) btnRetirarse.setDisable(!puedeActuar);
        if (btnSubir != null) btnSubir.setDisable(!puedeActuar);
        if (btnIgualar != null) btnIgualar.setDisable(!puedeActuar);

        if (!puedeActuar) {
            setVisibleManaged(panelSubir, false);
        }
    }

    private void actualizarTarjetasJugadores(List<Jugador> jugadores, Jugador jugadorActual) {
        for (int i = 0; i < tarjetasJugadores.length; i++) {
            HBox tarjeta = tarjetasJugadores[i];
            if (tarjeta == null) continue;

            if (jugadores != null && i < jugadores.size()) {
                Jugador j = jugadores.get(i);
                setVisibleManaged(tarjeta, true);

                if (labelsNombre[i] != null) labelsNombre[i].setText("Nombre: " + j.getnombreJugador());
                if (labelsFichas[i] != null) labelsFichas[i].setText("Fichas: " + j.getCantidadFichas());
                if (labelsApuesta[i] != null) labelsApuesta[i].setText("Apuesta en ronda: " + j.getAporteEnVuelta());
                if (labelsJugada[i] != null) labelsJugada[i].setText("ultima jugada: " + j.getUltimaJugada());

                boolean tieneTurno = (j.equals(jugadorActual));
                tarjeta.setStyle(tieneTurno
                        ? "-fx-background-color: #8c8c8c; -fx-border-color: #ffd700; -fx-border-width: 2;"
                        : "-fx-background-color: #808080;");
            } else {
                // Si hay menos de 4 jugadores oculta las tarjetas que sobran
                setVisibleManaged(tarjeta, false);
            }
        }
    }

    private void actualizarDados(Dado[] dadosVisibles, boolean dadosRevelados, boolean esMiTurno) {
        for (int i = 0; i < botonesDados.length; i++) {
            Button btn = botonesDados[i];
            if (btn == null) continue;

            if (dadosVisibles != null && i < dadosVisibles.length && dadosVisibles[i] != null) {
                Dado d = dadosVisibles[i];
                int valor = d.getValor();
                boolean bloqueado = d.estaBloqueado();

                String textoDado = (dadosRevelados || esMiTurno) ? String.valueOf(valor) : "?";
                btn.setText(bloqueado ? textoDado + " [BLOQ]" : textoDado);
                btn.setDisable(!esMiTurno);

                btn.setStyle(bloqueado
                        ? "-fx-border-color: #d9534f; -fx-border-width: 2; -fx-background-color: #ffe6e6;"
                        : "-fx-border-color: #2e7d32; -fx-border-width: 1;");
            } else {
                btn.setText("DADO " + (i + 1));
                btn.setDisable(true);
            }
        }
    }


    // ACCIONES DE DADOS (On Action)
    // Botón DADO1
    @FXML private void onDado0() { alternarDado(0); }
    // Botón DADO2
    @FXML private void onDado1() { alternarDado(1); }
    // Botón DADO3
    @FXML private void onDado2() { alternarDado(2); }
    // Botón DADO4
    @FXML private void onDado3() { alternarDado(3); }
    // Botón DADO5
    @FXML private void onDado4() { alternarDado(4); }

    private void alternarDado(int indice) {
        if (controlador != null) {
            controlador.alternarBloqueoDado(indice);
        }
    }


    // BOTON "TIRAR"
    @FXML
    private void onTirar() {
        if (controlador != null) {
            controlador.tirarDados();
        }
    }

    // BOTON "Retirarse"
    @FXML
    private void onRetirarse() {
        if (controlador != null) {
            controlador.igualarOAbandonar(false);
        }
    }

    // BOTON "Subir"
    @FXML
    private void onAbrirSubir() {
        if (panelSubir != null) {
            boolean mostrar = !panelSubir.isVisible();
            setVisibleManaged(panelSubir, mostrar);
            if (mostrar && txtMontoSubir != null) {
                txtMontoSubir.clear();
                txtMontoSubir.requestFocus();
            }
        }
    }

    // BOTON "Aceptar" (Adentro de panelSubir)
    @FXML
    private void onConfirmarSubir() {
        if (controlador == null || txtMontoSubir == null) return;
        try {
            int monto = Integer.parseInt(txtMontoSubir.getText().trim());
            controlador.subirApuesta(monto);
            setVisibleManaged(panelSubir, false);
            txtMontoSubir.clear();
        } catch (NumberFormatException e) {
            // CONTROLADORERROR (DESPUES)
        }
    }

    // BOTON "Cancelar" (dentro de panelSubir)
    @FXML
    private void onCancelarSubir() {
        setVisibleManaged(panelSubir, false);
        if (txtMontoSubir != null) {
            txtMontoSubir.clear();
        }
    }

    // BOTON "Igualar"
    @FXML
    private void onIgualar() {
        if (controlador != null) {
            controlador.igualarOAbandonar(true);
        }
    }

    // BOTON "Abandonar partida"
    @FXML
    private void onAbandonarPartida() {
        if (controlador != null) {
            controlador.abandonarPartida();
        }
    }

    // =========================================================
    // ACCIONES DE EMPATE (On Action de Repartir / Re-Jugarr)
    // =========================================================
    // Botón "Repartir" -> On Action: onRepartir
    @FXML
    private void onRepartir() {
        if (controlador != null) {
            controlador.resolverEmpate(true);
        }
    }

    // Botón "Re-Jugarr" -> On Action: onRejugar
    @FXML
    private void onRejugar() {
        if (controlador != null) {
            controlador.resolverEmpate(false);
        }
    }

    // =========================================================
    // UTILIDAD PARA OCULTAR/MOSTRAR SIN DEJAR HUECO
    // =========================================================
    private void setVisibleManaged(javafx.scene.Node nodo, boolean visible) {
        if (nodo != null) {
            nodo.setVisible(visible);
            nodo.setManaged(visible);
        }
    }
}