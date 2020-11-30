package com.vitorvidal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {

        long startTime = System.nanoTime();

        int rows;
        int columns;
        double[][] inputMatrix;
        double[] outputMatrix;
        try {
            // Read the txt archive
            FileReader reader = new FileReader("input.txt");
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            line = bufferedReader.readLine();

            String[] inputArray;
            inputArray = line.split(";");

            rows = Integer.parseInt(inputArray[0]);
            columns = Integer.parseInt(inputArray[1]);
            inputMatrix = new double[rows][columns];

            parseInputMatrix(inputMatrix, bufferedReader);
            reader.close();

            matrixSimplification(rows, columns, inputMatrix);

            outputMatrix = new double[rows];

            calculateVariables(rows, columns, inputMatrix, outputMatrix);
            printOutput(outputMatrix);
            saveOutputToFile(outputMatrix);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nTempo de execução:");
        long stopTime = System.nanoTime();
        System.out.println((stopTime - startTime) / 100000000.0 + " segundos");
    }

    private static void matrixSimplification(int rows, int columns, double[][] inputMatrix) {
        System.out.println("Matriz inicial:");
        printMatrix(inputMatrix);
        System.out.println();

        Gauss[] threads = new Gauss[NUM_THREADS];
        for (int i = 0; i < rows - 1; i++) {
            int aux = 0;
            int threadNumber = 0;
            for (int j = i + 1; j < rows; j++) {
                double ratio = inputMatrix[j][i] / inputMatrix[i][i];
                ratio = roundOff(ratio);
                System.out.println("Fator de simplificação para a linha " + j);
                System.out.println(ratio);
                System.out.println();

                if (threadNumber + 1 == NUM_THREADS) {
                    if (aux + 1 == NUM_THREADS) aux = 0;
                    try {
                        threads[aux].join();
                    } catch (InterruptedException e) {
                        System.out.println("Erro aqui");
                        e.printStackTrace();
                    }
                    aux++;
                    threadNumber = 0;
                }
                threads[threadNumber] = new Gauss(inputMatrix, i, j, rows, columns, ratio);
                threads[threadNumber].start();
                threadNumber++;
            }
            for (int k = 0 ; k < NUM_THREADS; k++) {
                try {
                    if (threads[k] != null)
                        threads[k].join();
                } catch (InterruptedException e) {
                    System.out.println("Erro no fim da execução");
                    e.printStackTrace();
                }
            }

            System.out.println("Matriz resultante após iteração:");
            printMatrix(inputMatrix);

            System.out.println();
        }
    }

    // Backpropagation
    private static void calculateVariables(int rows, int columns, double[][] inputMatrix, double[] outputMatrix) {
        for (int i = rows - 1; i >= 0; i--) {

            outputMatrix[i] = inputMatrix[i][columns - 1];

            for (int j = i + 1; j < rows; j++) {
                outputMatrix[i] -= inputMatrix[i][j] * outputMatrix[j];
            }

            outputMatrix[i] /= inputMatrix[i][i];
            outputMatrix[i] = roundOff(outputMatrix[i]);
        }
    }

    private static void saveOutputToFile(double[] outputMatrix) throws IOException {
        FileWriter writer = new FileWriter("output.txt", false);
        writer.write(String.valueOf(outputMatrix.length) + '\n');
        for (double matrix : outputMatrix) {
            writer.write(String.valueOf(matrix) + '\n');
        }
        writer.close();
    }

    private static void printOutput(double[] outputMatrix) {
        System.out.println("Resultado do sistema linear:");
        for (double matrix : outputMatrix) {
            System.out.println(matrix + " ");
        }
    }

    private static double roundOff(double x) {
        double a = x;
        double temp = Math.pow(10.0, 2);
        a *= temp;
        a = Math.round(a);
        return (a / (float) temp);
    }

    private static void parseInputMatrix(double[][] inputMatrix, BufferedReader bufferedReader) throws IOException {
        String[] inputArray;
        String line;
        int aux = 0;

        while ((line = bufferedReader.readLine()) != null) {
            inputArray = line.split(";");

            for (int i = 0; i < inputArray.length; i++) {
                inputMatrix[aux][i] = Double.parseDouble(inputArray[i]);
            }
            aux++;
        }
    }

    private static void printMatrix(double[][] inputMatrix) {
        for (double[] matrix : inputMatrix) {
            for (double v : matrix) {
                System.out.print(v + " ");
            }
            System.out.println();
        }
    }
}