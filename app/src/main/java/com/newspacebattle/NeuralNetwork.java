package com.newspacebattle;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class NeuralNetwork {
    private int inputSize;
    private int hiddenSize;
    private int outputSize;
    double[][] weightsInputHidden;
    double[][] weightsHiddenOutput;
    double[] biasesHidden;
    double[] biasesOutput;

    Random rand = new Random();

    NeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.weightsInputHidden = new double[inputSize][hiddenSize];
        this.weightsHiddenOutput = new double[hiddenSize][outputSize];
        biasesHidden = new double[hiddenSize];
        biasesOutput = new double[outputSize];

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputHidden[i][j] = rand.nextDouble() * 2 - 1;
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHiddenOutput[i][j] = rand.nextDouble() * 2 - 1;
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            biasesHidden[i] = rand.nextDouble() * 2 - 1;
        }
        for (int i = 0; i < outputSize; i++) {
            biasesOutput[i] = rand.nextDouble() * 2 - 1;
        }
    }

    NeuralNetwork(List csv) {
        String[] sizesArray = (String[]) csv.get(0);
        for (int i = 0; i < sizesArray.length; i++) {
            sizesArray[i] = sizesArray[i].replaceAll("[^\\d]", "");
        }
        inputSize = Integer.parseInt(sizesArray[0]);
        hiddenSize = Integer.parseInt(sizesArray[1]);
        outputSize = Integer.parseInt(sizesArray[2]);

        String[] weightsAndBiases = (String[]) csv.get(1);
        for (int i = 0; i < weightsAndBiases.length; i++) {
            weightsAndBiases[i] = weightsAndBiases[i].replaceAll("[^\\d.-]", "");
        }

        int index = 0;
        weightsInputHidden = new double[inputSize][hiddenSize];
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputHidden[i][j] = Double.parseDouble(weightsAndBiases[index]);
                index++;
            }
        }
        weightsHiddenOutput = new double[hiddenSize][outputSize];
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHiddenOutput[i][j] = Double.parseDouble(weightsAndBiases[index]);
                index++;
            }
        }
        biasesHidden = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            biasesHidden[i] = Double.parseDouble(weightsAndBiases[index]);
            index++;
        }
        biasesOutput = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            biasesOutput[i] = Double.parseDouble(weightsAndBiases[index]);
            index++;
        }
    }

    public double[] forwardPropagation(double[] input) {
        if (input.length != inputSize) {
            throw new IllegalArgumentException("Input size does not match network input size");
        }
        double[] hiddenLayerOutput = new double[hiddenSize];
        double sum;
        for (int j = 0; j < hiddenSize; j++) {
            sum = 0.0;
            for (int i = 0; i < inputSize; i++) {
                sum += input[i] * weightsInputHidden[i][j];
            }
            hiddenLayerOutput[j] = sigmoid(sum + biasesHidden[j]);
        }

        double[] outputLayerOutput = new double[outputSize];
        for (int j = 0; j < outputSize; j++) {
            sum = 0.0;
            for (int i = 0; i < hiddenSize; i++) {
                sum += hiddenLayerOutput[i] * weightsHiddenOutput[i][j];
            }
            outputLayerOutput[j] = sigmoid(sum + biasesOutput[j]);
        }

        /*System.out.println("Input Neurons:");
        System.out.println(Arrays.toString(input));

        System.out.println("Input to Hidden Weights:");
        for (double[] doubles : weightsInputHidden) {
            System.out.println(Arrays.toString(doubles));
        }

        System.out.println("Hidden Biases:");
        System.out.println(Arrays.toString(biasesHidden));

        System.out.println("Hidden Neurons:");
        System.out.println(Arrays.toString(hiddenLayerOutput));

        System.out.println("Hidden to Output Weights:");
        for (double[] doubles : weightsHiddenOutput) {
            System.out.println(Arrays.toString(doubles));
        }

        System.out.println("Output Biases:");
        System.out.println(Arrays.toString(biasesOutput));

        System.out.println("Output Neurons:");
        System.out.println(Arrays.toString(outputLayerOutput));*/

        return outputLayerOutput;
    }

    public void applyMutation(double probability) {
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                if (rand.nextDouble() < probability) {
                    weightsInputHidden[i][j] += rand.nextGaussian() * 0.1;
                }
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                if (rand.nextDouble() < probability) {
                    weightsHiddenOutput[i][j] += rand.nextGaussian() * 0.1;
                }
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            if (rand.nextDouble() < probability) {
                biasesHidden[i] += rand.nextGaussian() * 0.1;
            }
        }
        for (int i = 0; i < outputSize; i++) {
            if (rand.nextDouble() < probability) {
                biasesOutput[i] += rand.nextGaussian() * 0.1;
            }
        }
    }

    public static NeuralNetwork merge(NeuralNetwork nn1, NeuralNetwork nn2) {
        if (nn1.inputSize != nn2.inputSize || nn1.hiddenSize != nn2.hiddenSize || nn1.outputSize != nn2.outputSize) {
            throw new IllegalArgumentException("Neural networks must have the same size");
        }
        Random rand = new Random();
        NeuralNetwork newNN = new NeuralNetwork(nn1.inputSize, nn1.hiddenSize, nn1.outputSize);
        for (int i = 0; i < nn1.inputSize; i++) {
            for (int j = 0; j < nn1.hiddenSize; j++) {
                newNN.weightsInputHidden[i][j] = rand.nextBoolean() ? nn1.weightsInputHidden[i][j] : nn2.weightsInputHidden[i][j];
            }
        }
        for (int i = 0; i < nn1.hiddenSize; i++) {
            for (int j = 0; j < nn1.outputSize; j++) {
                newNN.weightsHiddenOutput[i][j] = rand.nextBoolean() ? nn1.weightsHiddenOutput[i][j] : nn2.weightsHiddenOutput[i][j];
            }
        }
        for (int i = 0; i < nn1.hiddenSize; i++) {
            newNN.biasesHidden[i] = rand.nextBoolean() ? nn1.biasesHidden[i] : nn2.biasesHidden[i];
        }
        for (int i = 0; i < nn1.outputSize; i++) {
            newNN.biasesOutput[i] = rand.nextBoolean() ? nn1.biasesOutput[i] : nn2.biasesOutput[i];
        }
        return newNN;
    }

    static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    static double reLU(double x) {
        return Math.max(0, x);
    }

    public void saveWeightsAndBiases() {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        baseDir += File.separator + "Download";
        String fileName = "nn.csv";
        String filePath = baseDir + File.separator + fileName;

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(filePath));

            double[] data = new double[inputSize * hiddenSize + hiddenSize * outputSize + hiddenSize + outputSize];
            int index = 0;
            for (int i = 0; i < inputSize; i++) {
                for (int j = 0; j < hiddenSize; j++) {
                    data[index] = weightsInputHidden[i][j];
                    index++;
                }
            }
            for (int i = 0; i < hiddenSize; i++) {
                for (int j = 0; j < outputSize; j++) {
                    data[index] = weightsHiddenOutput[i][j];
                    index++;
                }
            }
            for (int i = 0; i < hiddenSize; i++) {
                data[index] = biasesHidden[i];
                index++;
            }
            for (int i = 0; i < outputSize; i++) {
                data[index] = biasesOutput[i];
                index++;
            }
            String[] sizes = new String[3];
            sizes[0] = String.valueOf(inputSize);
            sizes[1] = String.valueOf(hiddenSize);
            sizes[2] = String.valueOf(outputSize);
            writer.writeNext(sizes);
            String[] dataString = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                dataString[i] = String.valueOf(data[i]);
            }

            writer.writeNext(dataString);

            writer.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Input to Hidden Weights:\n");
        for (double[] doubles : weightsInputHidden) {
            s.append(Arrays.toString(doubles)).append("\n");
        }
        s.append("\nHidden Biases:\n");
        s.append(Arrays.toString(biasesHidden)).append("\n\n");
        s.append("Hidden to Output Weights:\n");
        for (double[] doubles : weightsHiddenOutput) {
            s.append(Arrays.toString(doubles)).append("\n");
        }
        s.append("\nOutput Biases:\n");
        s.append(Arrays.toString(biasesOutput)).append("\n\n");
        return s.toString();
    }
}
