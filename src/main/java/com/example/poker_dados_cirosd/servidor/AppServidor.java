package com.example.poker_dados_cirosd.servidor;

import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.servidor.Servidor;
import com.example.poker_dados_cirosd.modelo.Partida;
import java.rmi.RemoteException;

//ARCHIVO RMI
public class AppServidor {
    public static void main(String[] args) {
        String ip = "127.0.0.1"; // En pruebas locales: 127.0.0.1; entre dos PCs: IP privada de red
        int puerto = 8888; // Puerto del registry en el servidor

        Partida modelo = new Partida();
        try {
            new Servidor(ip, puerto).iniciar(modelo); // Publica la única partida en el servidor
            System.out.println("Servidor iniciado en " + ip + ":" + puerto);
        } catch (RemoteException | RMIMVCException e) {
            e.printStackTrace();
        }
    }
}
