package org.example.NN;

import java.util.Random;

public class NN {
    int[] networkShape = {4, 34, 3}; // network shape input layer, hidden layer, output layer
    Layer[] layers;
    float[] layerGradients;
    Random rand = new Random();


    public void Awake() {
        layers = new Layer[networkShape.length - 1];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = new Layer(networkShape[i], networkShape[i + 1]);
        }
    }

    //This function is used to feed forward the inputs through the network, and return the output, which is the decision of the network, in this case, the direction to move in.
    public float[] Brain(float[] inputs) {
        for (int i = 0; i < layers.length; i++) {
            if (i == 0) {
                layers[i].Forward(inputs);
                layers[i].Activation();
            } else if (i == layers.length - 1) {
                layers[i].Forward(layers[i - 1].nodeArray);
            } else {
                layers[i].Forward(layers[i - 1].nodeArray);
                layers[i].Activation();
            }
        }

        return (layers[layers.length - 1].nodeArray);
    }

    //This function is used to copy the weights and biases from one neural network to another.
    public void copyFrom(NN other) {
        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            Layer otherLayer = other.layers[i];
            layer.copyFrom(otherLayer);
        }
    }

    public class Layer {
        public float[][] weightsArray;
        public float[] biasesArray;
        public float[] nodeArray;

        public int n_inputs;
        public int n_neurons;

        public Layer(int n_inputs, int n_neurons) {
            this.n_neurons = n_neurons;
            this.n_inputs = n_inputs;

            weightsArray = new float[n_neurons][n_inputs];
            biasesArray = new float[n_neurons];
        }

        public void Forward(float[] inputsArray) {
            nodeArray = new float[n_neurons];

            for (int i = 0; i < n_neurons; i++) {
                for (int j = 0; j < n_inputs; j++) {
                    nodeArray[i] += weightsArray[i][j] * inputsArray[j];
                }
                nodeArray[i] += biasesArray[i];
            }
        }

        public void Activation() {
            //leaky relu function
            for (int i = 0; i < nodeArray.length; i++) {
                if (nodeArray[i] < 0) {
                    nodeArray[i] = nodeArray[i] / 10;
                }
            }


            //sigmoid function
            for (int i = 0; i < nodeArray.length; i++) {
                nodeArray[i] = (float) (1 / (1 + Math.expm1(-nodeArray[i])));
            }

            //tanh function
            for (int i = 0; i < nodeArray.length; i++) {
                nodeArray[i] = (float) Math.tanh(nodeArray[i]);
            }

            //relu function
            for (int i = 0; i < nodeArray.length; i++) {
                if (nodeArray[i] < 0) {
                    nodeArray[i] = 0;
                }
            }
        }

        public void MutateLayer(float mutationChance, float mutationAmount) {
            for (int i = 0; i < n_neurons; i++) {
                for (int j = 0; j < n_inputs; j++) {
                    if (rand.nextFloat() < mutationChance) {
                        weightsArray[i][j] += (rand.nextFloat() * 2 - 1) * mutationAmount;
                    }
                }

                if (rand.nextFloat() < mutationChance) {
                    biasesArray[i] += (rand.nextFloat() * 2 - 1) * mutationAmount;
                }
            }
        }

        public void copyFrom(Layer other) {
            for (int i = 0; i < n_neurons; i++) {
                System.arraycopy(other.weightsArray[i], 0, weightsArray[i], 0, n_inputs);
                biasesArray[i] = other.biasesArray[i];
            }
        }

    }

    public void initializeWithWeightsAndBiases(float[][] weights, float[] biases) {
        for (int i = 0; i < layers.length; i++) {
            // Adjust the size of weightsArray to match the size of the incoming weights array
            layers[i].weightsArray = new float[weights.length][];
            for (int j = 0; j < weights.length; j++) {
                layers[i].weightsArray[j] = new float[weights[j].length];
                System.arraycopy(weights[j], 0, layers[i].weightsArray[j], 0, weights[j].length);
            }
            // Adjust the size of biasesArray to match the size of the incoming biases array
            layers[i].biasesArray = new float[biases.length];
            System.arraycopy(biases, 0, layers[i].biasesArray, 0, biases.length);
        }
    }



    public void mutate(float mutationChance, float mutationAmount) {
        for (Layer layer : layers) {
            layer.MutateLayer(mutationChance, mutationAmount);
        }
    }




}


