package com.example.poker_dados_cirosd.modelo;

import java.io.Serializable;
import java.util.Random;

public class Dado implements Serializable{
    //Serializable me deja persistir la info para que no se pierda
    private int valor;
    private boolean bloqueado;

    private static Random random = new Random();

    public Dado(){
        this.bloqueado = false;
        this.tirar(); //valor aleatorio
    }

    private void tirar() {
        if (!bloqueado) {
            this.valor = random.nextInt(6) + 1;
        }
    }


    //estado del dado
    public void bloquear(){
        this.bloqueado = true;
    }

    public void desbloquear(){
        this.bloqueado = false;
    }

    //esta me deja modificarlo desde afuera sin poner un if, de forma directa
    public void setBloqueado(boolean bloqueado){
        this.bloqueado = bloqueado;
    }

    //obtengo datos del dado
    public int getValor(){
        return valor;
    }

    public boolean estaBloqueado(){
      return bloqueado;
    }
}
