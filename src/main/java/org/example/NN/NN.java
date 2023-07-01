package org.example.NN;

import java.util.Random;

public class NN {

    int[] networkShape = {4, 34, 3};
    Layer[] layers;

    float[] layerGradients;
    Random rand = new Random();
    private static final float CONVERGENCE_THRESHOLD = 0.001f; // Convergence threshold
    private static final int CONVERGENCE_CHECK_WINDOW = 10; // Number of consecutive epochs to check for convergence
    private float[] previousLosses = new float[CONVERGENCE_CHECK_WINDOW];
    private int convergenceCheckCount = 0;

    public void Awake() {
        layers = new Layer[networkShape.length - 1];

        for (int i = 0; i < layers.length; i++) {
            layers[i] = new Layer(networkShape[i], networkShape[i + 1]);
        }
    }

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

        return layers[layers.length - 1].nodeArray;
    }

        public void reward(float value) {
            // Adjust the weights or other parameters based on the reward value
            // For example, you can update the weights of the neural network here
            // to reinforce the behavior that led to the reward.
        }

    public void copyFrom(NN other) {
        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            Layer otherLayer = other.layers[i];
            layer.copyFrom(otherLayer);
        }
    }

    public void train(float[][] inputsArray, float[][] expectedOutputsArray) {
        float learningRate = 0.01f;
        int numEpochs = 1000;

        for (int epoch = 0; epoch < numEpochs; epoch++) {
            float totalLoss = 0.0f;

            for (int i = 0; i < inputsArray.length; i++) {
                float[] inputs = inputsArray[i];
                float[] expectedOutputs = expectedOutputsArray[i];

                // Forward propagation
                float[] predictedOutputs = Brain(inputs);

                // Compute the loss
                float[] loss = computeLoss(predictedOutputs, expectedOutputs);

                // Backpropagation
                computeGradients(loss);

                // Update weights and biases using gradient descent
                updateWeightsAndBiases(learningRate);

                // Accumulate loss for monitoring training progress
                totalLoss += calculateTotalLoss(loss);
            }

            // Calculate average loss for the epoch
            float averageLoss = totalLoss / inputsArray.length;

            // Print average loss for monitoring training progress
            System.out.println("Epoch: " + epoch + ", Average Loss: " + averageLoss);
        }
    }

    private void updateWeightsAndBiases(float learningRate) {
        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];

            for (int j = 0; j < layer.n_neurons; j++) {
                for (int k = 0; k < layer.n_inputs; k++) {
                    layer.weightsArray[j][k] += learningRate * layer.layerGradients[j] * layer.inputsArray[k];
                }

                layer.biasesArray[j] += learningRate * layer.layerGradients[j];
            }
        }
    }

    private float[] computeLoss(float[] predictedOutputs, float[] expectedOutputs) {
        if (predictedOutputs.length != expectedOutputs.length) {
            throw new IllegalArgumentException("Mismatch in predicted outputs and expected outputs arrays.");
        }

        float[] loss = new float[predictedOutputs.length];

        for (int i = 0; i < predictedOutputs.length; i++) {
            loss[i] = expectedOutputs[i] - predictedOutputs[i];
        }

        return loss;
    }

    private void computeGradients(float[] loss) {
        for (int i = layers.length - 1; i >= 0; i--) {
            Layer layer = layers[i];

            if (i == layers.length - 1) {
                layer.computeOutputLayerGradients(loss);
            } else {
                Layer nextLayer = layers[i + 1];
                layer.computeHiddenLayerGradients(nextLayer);
            }
        }
    }

    private float calculateTotalLoss(float[] loss) {
        float totalLoss = 0.0f;

        for (float value : loss) {
            totalLoss += value * value;
        }

        return totalLoss;
    }



    public class Layer {

        float[][] weightsArray;
        float[] biasesArray;
        float[] nodeArray;
        float[] inputsArray;

        float[] layerGradients;
        int n_inputs;
        int n_neurons;

        public Layer(int n_inputs, int n_neurons) {
            this.n_inputs = n_inputs;
            this.n_neurons = n_neurons;

            layerGradients = new float[n_neurons];
            inputsArray = new float[n_inputs];
            weightsArray = new float[n_neurons][n_inputs];
            biasesArray = new float[n_neurons];
            nodeArray = new float[n_neurons];
        }

        public void computeOutputLayerGradients(float[] loss) {
            for (int i = 0; i < n_neurons; i++) {
                float derivative = 1.0f - (nodeArray[i] * nodeArray[i]);
                layerGradients[i] = loss[i] * derivative;
            }
        }

        public void computeHiddenLayerGradients(Layer nextLayer) {
            for (int i = 0; i < n_neurons; i++) {
                float sum = 0.0f;
                for (int j = 0; j < nextLayer.n_neurons; j++) {
                    sum += nextLayer.layerGradients[j] * nextLayer.weightsArray[j][i];
                }
                float derivative = 1.0f - (nodeArray[i] * nodeArray[i]);
                layerGradients[i] = sum * derivative;
            }
        }


        public void copyFrom(Layer other) {
            for (int i = 0; i < n_neurons; i++) {
                System.arraycopy(other.weightsArray[i], 0, weightsArray[i], 0, n_inputs);
                biasesArray[i] = other.biasesArray[i];
            }
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
            for (int i = 0; i < nodeArray.length; i++) {
                nodeArray[i] = (float) Math.tanh(nodeArray[i]);
            }
        }

        public void mutateLayer(float mutationChance, float mutationAmount) {
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
    }

    public void useTrainedModel(){
        String trainedModel = loadTrainedModelFromFile();

        useModel(trainedModel);
    }

    private String loadTrainedModelFromFile() {
        // Implement this method to load the trained model from a file
        return null;
    }

    private void useModel(String model) {
        // Implement this method to use the model
    }



    public void MutateNetwork(float mutationChance, float mutationAmount) {
        for (int i = 0; i < layers.length; i++) {
            layers[i].mutateLayer(mutationChance, mutationAmount);
        }
    }

    public void stopTraining() {
        // Stop the training
        // This is just a placeholder, you'll need to implement the actual logic to stop the training
    }

    public float[][] getWeights() {
        float[][] weights = new float[layers.length][];

        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            weights[i] = new float[layer.n_neurons * layer.n_inputs];

            for (int j = 0; j < layer.n_neurons; j++) {
                System.arraycopy(layer.weightsArray[j], 0, weights[i], j * layer.n_inputs, layer.n_inputs);
            }
        }

        return weights;
    }

    public void setWeights(float[][] weights) {
        if (weights.length != layers.length) {
            throw new IllegalArgumentException("Mismatch in the number of weight matrices.");
        }

        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];

            if (weights[i].length != layer.n_neurons * layer.n_inputs) {
                throw new IllegalArgumentException("Mismatch in the size of weight matrix " + i);
            }

            for (int j = 0; j < layer.n_neurons; j++) {
                System.arraycopy(weights[i], j * layer.n_inputs, layer.weightsArray[j], 0, layer.n_inputs);
            }
        }
    }

    public float[][] getBiases() {
        float[][] biases = new float[layers.length][];

        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            biases[i] = layer.biasesArray;
        }

        return biases;
    }

    public void setBiases(float[] biases) {
        if (biases.length != layers.length) {
            throw new IllegalArgumentException("Mismatch in the number of bias arrays.");
        }

        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            layer.biasesArray = new float[]{biases[i]};
        }
    }


}


//package org.example.NN;
//
//import java.util.Random;
//
//public class NN {
//    int[] networkShape = {4, 34, 3}; // network shape input layer, hidden layer, output layer
//    Layer[] layers;
//    float[] layerGradients;
//    Random rand = new Random();
//
//
//    public void Awake() {
//        layers = new Layer[networkShape.length - 1];
//
//        for (int i = 0; i < layers.length; i++) {
//            layers[i] = new Layer(networkShape[i], networkShape[i + 1]);
//        }
//    }
//
//    //This function is used to feed forward the inputs through the network, and return the output, which is the decision of the network, in this case, the direction to move in.
//    public float[] Brain(float[] inputs) {
//        for (int i = 0; i < layers.length; i++) {
//            if (i == 0) {
//                layers[i].Forward(inputs);
//                layers[i].Activation();
//            } else if (i == layers.length - 1) {
//                layers[i].Forward(layers[i - 1].nodeArray);
//            } else {
//                layers[i].Forward(layers[i - 1].nodeArray);
//                layers[i].Activation();
//            }
//        }
//
//        return (layers[layers.length - 1].nodeArray);
//    }
//
//    //This function is used to copy the weights and biases from one neural network to another.
//    public void copyFrom(NN other) {
//        for (int i = 0; i < layers.length; i++) {
//            Layer layer = layers[i];
//            Layer otherLayer = other.layers[i];
//            layer.copyFrom(otherLayer);
//        }
//    }
//
//    public class Layer {
//        public float[][] weightsArray;
//        public float[] biasesArray;
//        public float[] nodeArray;
//
//        public int n_inputs;
//        public int n_neurons;
//
//        public Layer(int n_inputs, int n_neurons) {
//            this.n_neurons = n_neurons;
//            this.n_inputs = n_inputs;
//
//            weightsArray = new float[n_neurons][n_inputs];
//            biasesArray = new float[n_neurons];
//        }
//
//        public void Forward(float[] inputsArray) {
//            nodeArray = new float[n_neurons];
//
//            for (int i = 0; i < n_neurons; i++) {
//                for (int j = 0; j < n_inputs; j++) {
//                    nodeArray[i] += weightsArray[i][j] * inputsArray[j];
//                }
//                nodeArray[i] += biasesArray[i];
//            }
//        }
//
//        public void Activation() {
//            //leaky relu function
//            for (int i = 0; i < nodeArray.length; i++) {
//                if (nodeArray[i] < 0) {
//                    nodeArray[i] = nodeArray[i] / 10;
//                }
//            }
//
//
//            //sigmoid function
//            for (int i = 0; i < nodeArray.length; i++) {
//                nodeArray[i] = (float) (1 / (1 + Math.expm1(-nodeArray[i])));
//            }
//
//            //tanh function
//            for (int i = 0; i < nodeArray.length; i++) {
//                nodeArray[i] = (float) Math.tanh(nodeArray[i]);
//            }
//
//            //relu function
//            for (int i = 0; i < nodeArray.length; i++) {
//                if (nodeArray[i] < 0) {
//                    nodeArray[i] = 0;
//                }
//            }
//        }
//
//        public void MutateLayer(float mutationChance, float mutationAmount) {
//            for (int i = 0; i < n_neurons; i++) {
//                for (int j = 0; j < n_inputs; j++) {
//                    if (rand.nextFloat() < mutationChance) {
//                        weightsArray[i][j] += (rand.nextFloat() * 2 - 1) * mutationAmount;
//                    }
//                }
//
//                if (rand.nextFloat() < mutationChance) {
//                    biasesArray[i] += (rand.nextFloat() * 2 - 1) * mutationAmount;
//                }
//            }
//        }
//
//        public void copyFrom(Layer other) {
//            for (int i = 0; i < n_neurons; i++) {
//                System.arraycopy(other.weightsArray[i], 0, weightsArray[i], 0, n_inputs);
//                biasesArray[i] = other.biasesArray[i];
//            }
//        }
//
//    }
//
//    public void initializeWithWeightsAndBiases(float[][] weights, float[] biases) {
//        for (int i = 0; i < layers.length; i++) {
//            for (int j = 0; j < layers[i].weightsArray.length; j++) {
//                System.arraycopy(weights[j], 0, layers[i].weightsArray[j], 0, layers[i].weightsArray[j].length);
//                layers[i].biasesArray[j] = biases[j];
//            }
//        }
//    }
//
//    public void mutate(float mutationChance, float mutationAmount) {
//        for (Layer layer : layers) {
//            layer.MutateLayer(mutationChance, mutationAmount);
//        }
//    }
//
//
//
//
//}
//
//
