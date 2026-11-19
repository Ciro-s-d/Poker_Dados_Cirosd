package com.example.poker_dados_cirosd.modelo;

public class EvaluadorJugadas {

    public static ResultadoTirada evaluar(Dado[] dados){
        int[] frecuencias = new int[8]; //uso del 2 al 7 (el 1 vale mas puntos)
        for(Dado d : dados){
            frecuencias[d.getValor()]++;
        }
        frecuencias[7] = frecuencias[1]; //copio los 1 y los hago valer 7
        frecuencias[1] = 0;


        //EVALUACION DE JUGADAS

        for(int i = 2; i<=7; i++){
            if(frecuencias[i] == 5){
                return new ResultadoTirada("Poker Real", 90 + i);
            }
        }

        for(int i = 2; i<=7; i++){
            if(frecuencias[i] == 4){
                return new ResultadoTirada("Poker Cuadruple", 80 + i);
            }
        }


        //FULL
        int trioValor = 0;
        int parValor = 0;

        for (int i = 2; i <= 7; i++){
            if(frecuencias[i] == 3){
                trioValor = i;
            } else if (frecuencias[i] == 2) {
                parValor = i;
            }
        }
        if(trioValor > 0 && parValor > 0){
            int puntos = 60 + (trioValor * 2) + parValor;
            return new ResultadoTirada("Full", puntos);
        }


        //ESCALERAS (CAMBIO EN ESCALERA MAYOR Y MENOR: MAYOR = 3,4,5,6,1 / MENOR = 2,3,4,5,6)
        boolean centro = (frecuencias[3] == 1 && frecuencias[4] == 1 && frecuencias[5] == 1 && frecuencias[6] == 1);

        if(centro && frecuencias[7] == 1){
            return new ResultadoTirada("Escalera Mayor", 50);
        } else if (centro && frecuencias[2] == 1) {
            return new ResultadoTirada("Escalera Menor", 40);
        }


        //TRIPLES
        for(int i = 2; i <= 7; i++){
            if(frecuencias[i] == 3){
                return new ResultadoTirada("Pierna", 30 + i);
            }
        }


        //PAR DOBLE Y PAR SIMPLE
        int primerPar = 0;
        int segundoPar = 0;

        for(int i=2; i<= 7; i++) {
            if (frecuencias[i] == 2) {
                if (primerPar == 0) {
                    primerPar = i;
                } else {
                    segundoPar = i;
                }
            }

            if (segundoPar > 0) {
                return new ResultadoTirada("Doble par", 10 + segundoPar + primerPar);
            } else if (primerPar > 0) {
                return new ResultadoTirada("Par Simple", 0 + primerPar);
            }
        }

        return new ResultadoTirada("Sin Jugada", 0);
    }
}
