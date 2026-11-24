package com.example.poker_dados_cirosd.controlador;

import com.example.poker_dados_cirosd.modelo.Jugador;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
            colId.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.toString());
                    setStyle("-fx-background-color: transparent; -fx-text-fill: #2b1d0e; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-border-color: transparent #a1887f transparent transparent; -fx-border-width: 0 1 0 0;");
                }
            });
        }

        if (colNombre != null) {
            colNombre.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getnombreJugador()));
            colNombre.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item);
                    setStyle("-fx-background-color: transparent; -fx-text-fill: #2b1d0e; -fx-font-weight: bold; -fx-padding: 0 0 0 10; -fx-border-color: transparent;");
                }
            });
        }

        if (tablaJugadores != null) {
            tablaJugadores.setRowFactory(tv -> new TableRow<>() {
                @Override
                protected void updateItem(Jugador item, boolean empty) {
                    super.updateItem(item, empty);
                    setPrefHeight(28);
                    if (empty || item == null) {
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setStyle("-fx-background-color: rgba(255, 248, 225, 0.25); -fx-border-color: transparent transparent #8d6e4c transparent; -fx-border-width: 0 0 1 0;");
                    }
                }
            });
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