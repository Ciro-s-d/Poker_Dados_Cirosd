package com.example.poker_dados_cirosd.vista;

import com.example.poker_dados_cirosd.controlador.Controlador;
import com.example.poker_dados_cirosd.controlador.menuControlador;
import com.example.poker_dados_cirosd.controlador.mesaControlador;
import com.example.poker_dados_cirosd.controlador.salaControlador;
import com.example.poker_dados_cirosd.controlador.finPartidaControlador;
import com.example.poker_dados_cirosd.modelo.Dado;
import com.example.poker_dados_cirosd.modelo.Jugador;
import com.example.poker_dados_cirosd.modelo.Partida;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class VistaGrafica implements IVista {

    private List<Jugador> ultimosJugadores;

    private final Stage stage;
    private Controlador controlador;

    private salaControlador ctrlSala;
    private boolean salaCargada = false;

    private mesaControlador ctrlMesa;
    private boolean mesaCargada = false;

    public VistaGrafica(Stage stage) {
        this.stage = stage;
    }

    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
        if (this.ctrlMesa != null) {
            this.ctrlMesa.setControlador(controlador);
        }
    }

    @Override
    public void mostrarMenu() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = cargarFXML("/fxml/menu.fxml");
                Parent root = loader.load();

                menuControlador ctrlMenu = loader.getController();
                if (ctrlMenu != null) {
                    ctrlMenu.setControlador(controlador);
                }

                establecerEscena(root, "Póker de Dados - Menú");
                salaCargada = false;
                ctrlSala = null;
                mesaCargada = false;
                ctrlMesa = null;
            } catch (IOException e) {
                mostrarError("No se pudo cargar menu.fxml: " + e.getMessage());
            }
        });
    }

    @Override
    public void mostrarSala(List<Jugador> jugadores) {
        Platform.runLater(() -> {
            try {
                if (!salaCargada || ctrlSala == null) {
                    FXMLLoader loader = cargarFXML("/fxml/sala.fxml");
                    Parent root = loader.load();

                    ctrlSala = loader.getController();
                    if (ctrlSala != null) {
                        ctrlSala.setControlador(controlador);
                    }

                    establecerEscena(root, "Póker de Dados - Sala de Espera");
                    salaCargada = true;
                }

                if (ctrlSala != null) {
                    String nombreLocal = obtenerNombreJugadorLocal(jugadores);
                    ctrlSala.actualizarSala(jugadores, nombreLocal);
                }
            } catch (IOException e) {
                mostrarError("No se pudo cargar sala.fxml: " + e.getMessage());
            }
        });
    }

    @Override
    public void mostrarMesa(
            Partida.EstadoDePartida estado,
            int pozo,
            int apuestaMaximaActual,
            Jugador jugadorActual,
            List<Jugador> jugadores,
            Dado[] dadosVisibles,
            boolean dadosRevelados
    ) {
        this.ultimosJugadores = jugadores;
        Platform.runLater(() -> {
            URL urlMesa = getClass().getResource("/fxml/mesa.fxml");
            if (urlMesa == null) {
                mostrarInfo("¡La partida comenzó! (Todavía no creaste /fxml/mesa.fxml)");
                return;
            }

            try {
                if (!mesaCargada || ctrlMesa == null) {
                    FXMLLoader loader = new FXMLLoader(urlMesa);
                    Parent root = loader.load();

                    ctrlMesa = loader.getController();
                    if (ctrlMesa != null && controlador != null) {
                        ctrlMesa.setControlador(controlador);
                    }

                    establecerEscena(root, "Póker de Dados - Mesa");
                    salaCargada = false;
                    ctrlSala = null;
                    mesaCargada = true;
                }

                if (ctrlMesa != null) {
                    Jugador jugadorLocal = obtenerJugadorLocal(jugadores);
                    if (jugadorLocal != null) {
                        ctrlMesa.setJugadorLocal(jugadorLocal);
                    }
                    ctrlMesa.actualizarMesa(
                            estado,
                            pozo,
                            apuestaMaximaActual,
                            jugadorActual,
                            jugadores,
                            dadosVisibles,
                            dadosRevelados
                    );
                }
            } catch (IOException e) {
                mostrarError("No se pudo cargar mesa.fxml: " + e.getMessage());
            }
        });
    }



    @Override
    public void mostrarResultadoRonda(String mensaje) {
        Platform.runLater(() -> mostrarInfo(mensaje));
    }

    @Override
    public void mostrarFinPartida(String mensaje) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = cargarFXML("/fxml/finPartida.fxml");
                Parent root = loader.load();

                finPartidaControlador ctrlFin = loader.getController();
                if (ctrlFin != null) {
                    ctrlFin.setControlador(controlador);
                    ctrlFin.mostrarGanador(ultimosJugadores);
                }

                establecerEscena(root, "Póker de Dados - Fin de Partida");
                salaCargada = false;
                ctrlSala = null;
                mesaCargada = false;
                ctrlMesa = null;
            } catch (IOException e) {
                mostrarError("No se pudo cargar finPartida.fxml: " + e.getMessage());
            }
        });
    }

    @Override
    public void mostrarError(String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private FXMLLoader cargarFXML(String ruta) throws IOException {
        URL ubicacion = getClass().getResource(ruta);
        if (ubicacion == null) {
            throw new IOException("No se encontró el recurso " + ruta);
        }
        return new FXMLLoader(ubicacion);
    }

    private void establecerEscena(Parent root, String titulo) {
        stage.setTitle(titulo);
        stage.setMinWidth(920);
        stage.setMinHeight(680);
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }
        stage.show();
    }

    private Jugador obtenerJugadorLocal(List<Jugador> jugadores) {
        if (controlador == null || controlador.getIdJugadorLocal() == null || jugadores == null) {
            return null;
        }
        int idLocal = controlador.getIdJugadorLocal();
        for (Jugador j : jugadores) {
            if (j.getIdJugador() == idLocal) {
                return j;
            }
        }
        return null;
    }

    private String obtenerNombreJugadorLocal(List<Jugador> jugadores) {
        Jugador local = obtenerJugadorLocal(jugadores);
        return (local != null) ? local.getnombreJugador() : "";
    }
}