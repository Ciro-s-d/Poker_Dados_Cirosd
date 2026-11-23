package com.example.poker_dados_cirosd;

import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.cliente.Cliente;
import com.example.poker_dados_cirosd.controlador.Controlador;
import com.example.poker_dados_cirosd.vista.VistaGrafica;
import javafx.application.Application;
import javafx.stage.Stage;
import java.rmi.RemoteException;

public class HelloApplication extends Application {
    //MODIFICADO PARA QUE ANDE RMI
    @Override
    public void start(Stage stage) {
        String ipCliente = "127.0.0.1";
        int puertoCliente = 9991;
        String ipServidor = "127.0.0.1";
        int puertoServidor = 8888;

        Controlador controlador = new Controlador();
        VistaGrafica vista = new VistaGrafica(stage);
        vista.setControlador(controlador);
        controlador.setVista(vista);

        try {
            new Cliente(ipCliente, puertoCliente, ipServidor, puertoServidor).iniciar(controlador);
            vista.mostrarMenu();
        } catch (RemoteException | RMIMVCException e) {
            vista.mostrarError("No se pudo conectar al servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}