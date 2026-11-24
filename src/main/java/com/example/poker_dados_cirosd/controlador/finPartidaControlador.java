package com.example.poker_dados_cirosd.controlador;

import com.example.poker_dados_cirosd.modelo.Jugador;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Comparator;
import java.util.List;

public class finPartidaControlador {

    @FXML private ImageView imgGanador;
    @FXML private Label lblNombreGanador;
    @FXML private Label lblFichasGanador;
    @FXML private Button btnVolverMenu;

    private Controlador controlador;

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public void mostrarGanador(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            if (lblNombreGanador != null) lblNombreGanador.setText("Sin ganador");
            if (lblFichasGanador != null) lblFichasGanador.setText("Fichas: 0");
            return;
        }

        Jugador ganador = jugadores.stream()
                .max(Comparator.comparingInt(Jugador::getCantidadFichas))
                .orElse(jugadores.get(0));

        if (lblNombreGanador != null) {
            lblNombreGanador.setText(ganador.getnombreJugador());
        }
        if (lblFichasGanador != null) {
            lblFichasGanador.setText("Fichas: " + ganador.getCantidadFichas());
        }
        if (imgGanador != null) {
            int numPersonaje = ((Math.max(1, ganador.getIdJugador()) - 1) % 4) + 1;
            var urlAvatar = getClass().getResource("/img/personajes/personaje_" + numPersonaje + ".jpg");
            if (urlAvatar != null) {
                imgGanador.setImage(new Image(urlAvatar.toExternalForm()));
            }
        }
    }

    @FXML
    private void onVolverMenu() {
        if (controlador != null) {
            controlador.abandonarPartida();
        }
    }
}