package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;

/**
 * this class stores the input data from the json to start the application.
 */
public class OperationData {
    private Attack[] attacks;
    private int R2D2;
    private int Lando;
    private int Ewoks;

    public OperationData(Attack[] attacks, int R2D2, int Lando, int Ewoks ){
        this.attacks=attacks;
        this.R2D2=R2D2;
        this.Lando=Lando;
        this.Ewoks=Ewoks;
    }

    public OperationData(){}

    public Attack[] getAttacks(){
        return attacks;
    }

    public int getR2D2(){
        return R2D2;
    }

    public int getLando() {
        return Lando;
    }

    public int getEwoks() {
        return Ewoks;
    }
}

