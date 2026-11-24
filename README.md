# Póker de Dados — TP Final POO

## Autor
- Dellagogna Ciro

## Cátedra y Asignatura
- **Asignatura:** Programación Orientada a Objetos (POO)
- Universidad Nacional de Luján 

Este proyecto se desarrolla aplicando principios de diseño orientado a objetos, separación de responsabilidades, el patrón **MVC** junto con **Observer** y comunicación en red mediante **RMI** (`LibreriaRMIMVC`) sobre **JavaFX** y **Maven**.

## De qué trata el juego
El juego es una adaptación del **Póker de Dados** donde los participantes compiten para formar la jugada de mayor valor con **5 dados** y quedarse con el **pozo**.

- En cada turno, el jugador activo puede realizar hasta **2 tiros** (pudiendo bloquear o desbloquear dados entre tiradas o plantarse) y participar en las acciones de apuesta de la ronda (**subir**, **igualar** o **retirarse**), además de resolverse rondas, desempates y el cierre de partida con el ganador.
- La arquitectura separa el dominio (`modelo`), la interfaz gráfica en JavaFX/FXML (`vista` y controladores de pantalla) y la coordinación (`controlador`).

## Cambios en las escaleras (Escalera Mayor y Escalera Menor)
En `EvaluadorJugadas`, la evaluación de escaleras se ajustó respecto a las reglas tradicionales de poker de dados:
- **Escalera Mayor:** se evalúa con la combinación `3, 4, 5, 6, 1` (donde el `1` actúa con valor alto `7` en la tabla de frecuencias).
- **Escalera Menor:** se evalúa con la combinación `2, 3, 4, 5, 6`.

## Testeo en línea en una misma PC (puertos diferentes)
Para probar el modo en red en una sola computadora sin conflictos de puertos:
1. Se ejecuta el servidor (`LanzadorServidor`) utilizando la dirección local `127.0.0.1` y un puerto asignado al servidor (por ejemplo, `8888`).
2. Se abren múltiples instancias del cliente (`LanzadorCliente`) —habilitando *Allow multiple instances* en el IDE— apuntando a la IP y puerto del servidor (`127.0.0.1` y `8888`), pero asignando a **cada cliente un puerto local distinto** para evitar colisiones (`Port already in use`).
3. De este modo, cada ventana representa un jugador independiente sincronizado a través de la única instancia de `Partida` en el servidor.

## Diagrama de clases
Dentro del repositorio se incluye el diagrama de clases en formato Mermaid en el archivo **`diagrama_de_clases.md`**, con las clases, relaciones y responsabilidades delimitadas por paquete.