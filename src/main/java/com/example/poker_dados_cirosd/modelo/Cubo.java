package com.example.poker_dados_cirosd.modelo;

import java.io.Serializable;

public class Cubo implements Serializable {
    private static final int CANTIDAD_DADOS = 5;
    private Dado[] dados;

    public Cubo(){
        this(CANTIDAD_DADOS);
    }

    public Cubo(int cantidadDados) {
        this.dados = new Dado[cantidadDados];
        for(int i = 0; i < cantidadDados; i++){
            this.dados[i] = new Dado();
        }
    }

    public void tirar(){
        for(Dado d : dados){
            d.tirar();          //el dado evalua si esta bloqueado o no y recien ahi tira
        }
    }

    public void bloquearDado(int indice){
        if(indice >= 0 && indice < dados.length){
            dados[indice].bloquear();
        }
    }

    public void desbloquearDado(int indice){
        if(indice >= 0 && indice < dados.length){
            dados[indice].desbloquear();
        }
    }

    public void desbloquearTodosDados(){            //fin de turno, todos se desbloquean
        for(Dado d : dados){
            d.desbloquear();
        }
    }

    public Dado[] getDados() {
        return dados;
    }
}
