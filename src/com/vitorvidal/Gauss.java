package com.vitorvidal;

public class Gauss extends Thread {
    private final double[][] inputMatrix;
    private final int i;
    private final int j;
    private final int rows;
    private final int columns;
    private double ratio;

    Gauss(double[][] inputMatrix, int i, int j, int rows, int columns, double ratio) {
        this.inputMatrix = inputMatrix;
        this.i = i;
        this.j = j;
        this.rows = rows;
        this.columns = columns;
        this.ratio = ratio;
    }


    public void run() {
        for (int k = i; k < rows; k++) {
            inputMatrix[j][k] -= ratio * inputMatrix[i][k];
            inputMatrix[j][k] = roundOff(inputMatrix[j][k]);
        }
        inputMatrix[j][columns - 1] -= ratio * inputMatrix[i][columns - 1];
        inputMatrix[j][columns - 1] = roundOff(inputMatrix[j][columns - 1]);
        ratio = 0;
    }

    private static double roundOff(double x) {
        double a = x;
        double temp = Math.pow(10.0, 2);
        a *= temp;
        a = Math.round(a);
        return (a / (float) temp);
    }
}