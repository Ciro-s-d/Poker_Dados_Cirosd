package com.example.poker_dados_cirosd;

import com.example.poker_dados_cirosd.controlador.Controlador;
import com.example.poker_dados_cirosd.modelo.Partida;
import com.example.poker_dados_cirosd.vista.VistaGrafica;
import javafx.application.Application;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        Partida partida = new Partida();
        VistaGrafica vista = new VistaGrafica(stage);
        Controlador controlador = new Controlador(partida, vista);
        vista.setControlador(controlador);

        vista.iniciar();
    }
}