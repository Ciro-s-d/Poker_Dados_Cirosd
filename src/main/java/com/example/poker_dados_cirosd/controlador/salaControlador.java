package com.example.poker_dados_cirosd.controlador;

import com.example.poker_dados_cirosd.modelo.Jugador;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class salaControlador {

    @FXML
    public TableView<Jugador> tablaJugadores;

    @FXML
    public TableColumn<Jugador, Object> colId;

    @FXML
    public TableColumn<Jugador, String> colNombre;

    // BOTON "Iniciar Partida" (solo admin)
    @FXML
    public Button btnIniciarPartida;

    // LABEL "Esperando que se inicie la partida" (no admin)
    @FXML
    public Label lblEsperando;

    private Controlador controladorMvc;

    public void setControlador(Controlador controladorMvc) {
        this.controladorMvc = controladorMvc;
    }

    @FXML
    public void initialize() {
        if (colId != null) {
            colId.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().getIdJugador()));
        }
        if (colNombre != null) {
            colNombre.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getnombreJugador()));
        }
    }

    public void actualizarSala(List<Jugador> jugadores, String nombreJugadorLocal) {
        actualizarSala(jugadores, nombreJugadorLocal, null);
    }

    public void actualizarSala(List<Jugador> jugadores, String nombreJugadorLocal, Integer idJugadorLocal) {
        if (tablaJugadores != null && jugadores != null) {
            tablaJugadores.setItems(FXCollections.observableArrayList(jugadores));
        }

        boolean esAdmin = false;
        if (jugadores != null && !jugadores.isEmpty()) {
            Jugador primero = jugadores.get(0);
            if (idJugadorLocal != null) {
                esAdmin = primero.getIdJugador() == idJugadorLocal;
            } else if (nombreJugadorLocal != null) {
                esAdmin = primero.getnombreJugador().equals(nombreJugadorLocal);
            }
        }

        setVisibilidad(btnIniciarPartida, esAdmin);
        setVisibilidad(lblEsperando, !esAdmin);
    }

    private void setVisibilidad(javafx.scene.Node nodo, boolean visible) {
        if (nodo != null) {
            nodo.setVisible(visible);
            nodo.setManaged(visible);
        }
    }

    @FXML
    public void onIniciarPartida(ActionEvent actionEvent) {
        if (controladorMvc != null) {
            controladorMvc.iniciarPartida();
        }
    }
}