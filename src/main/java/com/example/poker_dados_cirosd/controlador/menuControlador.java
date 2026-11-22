package com.example.poker_dados_cirosd.controlador;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class menuControlador {

    @FXML
    public Label lbl_Titulo;

    @FXML
    public Button btn_Jugar;

    @FXML
    public Button btn_Salir;

    @FXML
    public VBox vboxAnteSala;

    @FXML
    public TextField txtNombreJugador;

    @FXML
    public Button btn_Unirse;

    @FXML
    public Button btn_Atras;

    private Controlador controladorMvc;

    public void setControlador(Controlador controladorMvc) {
        this.controladorMvc = controladorMvc;
    }

    @FXML
    public void initialize() {
        //no se ve el 2do menu, solo depsues de apretar jugar
        if (vboxAnteSala != null) {
            vboxAnteSala.setVisible(false);
            vboxAnteSala.setManaged(false);
        }
    }

    // BOTON "Jugar"
    @FXML
    public void onJugar(ActionEvent actionEvent) {
        if (vboxAnteSala != null) {
            vboxAnteSala.setVisible(true);
            vboxAnteSala.setManaged(true);
        }
        if (txtNombreJugador != null) {
            txtNombreJugador.requestFocus();
        }
    }

    // BOTON "Salir"
    @FXML
    public void onSalir(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    // BOTON "Unirse"
    @FXML
    public void onUnirse(ActionEvent actionEvent) {
        String nombreJugador = (txtNombreJugador != null)
                ? txtNombreJugador.getText().trim()
                : "";

        if (nombreJugador.isEmpty()) {
            mostrarAlerta("Tenés que ingresar un nombre para unirte.");
            return;
        }

        if (controladorMvc != null) {
            // Conecta con tu Controlador del MVC para registrar el jugador / entrar a la sala:
            // ajustá el nombre del metodo si en tu Controlador se llama distinto (ej: agregarJugador o unirseASala).
            controladorMvc.agregarJugador(nombreJugador);
        }
    }

    // BOTON "Atras"
    @FXML
    public void onAtras(ActionEvent actionEvent) {
        if (vboxAnteSala != null) {
            vboxAnteSala.setVisible(false);
            vboxAnteSala.setManaged(false);
        }
        if (txtNombreJugador != null) {
            txtNombreJugador.clear();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}