package com.example.poker_dados_cirosd.cliente;

import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.cliente.Cliente;
import com.example.poker_dados_cirosd.controlador.Controlador;
import com.example.poker_dados_cirosd.vista.VistaGrafica;
import javafx.application.Application;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.List;

public class AppCliente extends Application {
    //permito que AppCliente acepte el puertoCliente (y ipCliente, ipServidor, puertoServidor)  por parámetro, con valores por defecto listos para usar.
    //para 2 o mas creo una copia y le doy un puerto mas alto, y asi sucesivamente
    //Me ahorro la configuracion manual

    private static final String IP_SERVIDOR_DEFECTO = "127.0.0.1";
    private static final int PUERTO_SERVIDOR_DEFECTO = 8888;
    private static final String IP_CLIENTE_DEFECTO = "127.0.0.1";
    private static final int PUERTO_CLIENTE_DEFECTO = 9991;

    @Override
    public void start(Stage stage) {
        Parameters params = getParameters();
        List<String> args = params != null ? params.getRaw() : List.of();

        String ipCliente = obtenerStringParametro("ipCliente", args, 1, IP_CLIENTE_DEFECTO);
        int puertoCliente = obtenerIntParametro("puertoCliente", args, 0, PUERTO_CLIENTE_DEFECTO);
        String ipServidor = obtenerStringParametro("ipServidor", args, 2, IP_SERVIDOR_DEFECTO);
        int puertoServidor = obtenerIntParametro("puertoServidor", args, 3, PUERTO_SERVIDOR_DEFECTO);

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

    private String obtenerStringParametro(String clavePropiedad, List<String> args, int indiceArg, String defecto) {
        String prop = System.getProperty(clavePropiedad);
        if (prop != null && !prop.isBlank()) {
            return prop.trim();
        }
        if (args != null && indiceArg >= 0 && indiceArg < args.size()) {
            String val = args.get(indiceArg);
            if (val != null && !val.isBlank()) {
                return val.trim();
            }
        }
        return defecto;
    }

    private int obtenerIntParametro(String clavePropiedad, List<String> args, int indiceArg, int defecto) {
        String prop = System.getProperty(clavePropiedad);
        if (prop != null && !prop.isBlank()) {
            try {
                return Integer.parseInt(prop.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        if (args != null && indiceArg >= 0 && indiceArg < args.size()) {
            try {
                return Integer.parseInt(args.get(indiceArg).trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return defecto;
    }
}