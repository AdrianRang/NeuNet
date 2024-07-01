package src;
import java.util.ArrayList;

import src.NeuralNet.*;

public class Network {
    Neuron[] inputNeurons;
    Neuron[] outputNeurons;
    ArrayList<ArrayList<Neuron>> hiddenLayers; // this is array list because we don't know how many hidden layers there will be
    ArrayList<Connector> connectors;
    int maxHiddenLayers;
    int maxNeurons;

    /**
     * A neural network.
     * @param inputNeurons The input neurons.
     * @param maxHiddenLayers The maximum number of hidden layers.
     * @param maxNeurons The maximum number of neurons per layer.
     * @param outputNeurons The output neurons.
     * @param connectors The connectors.
     */
    public Network(Neuron[] inputNeurons, int maxHiddenLayers, int maxNeurons, Neuron[] outputNeurons, ArrayList<Connector> connectors) {
        this.inputNeurons = inputNeurons;
        this.outputNeurons = outputNeurons;
        this.maxHiddenLayers = maxHiddenLayers;
        this.maxNeurons = maxNeurons;
        this.connectors = connectors;
        if (maxHiddenLayers > 0) {
            hiddenLayers = new ArrayList<>();
        }
    }

    /**
     * A neural network.
     * @param inputNeurons The input neurons.
     * @param maxHiddenLayers The maximum number of hidden layers.
     * @param maxNeurons The maximum number of neurons per layer.
     * @param outputNeurons The output neurons.
     */
    public Network(Neuron[] inputNeurons, int startingHiddenLayers, int maxHiddenLayers, int maxNeurons, Neuron[] outputNeurons, double chanceOfConnection, double chanceOfCreation) {
        this.inputNeurons = inputNeurons;
        this.outputNeurons = outputNeurons;
        this.maxHiddenLayers = maxHiddenLayers;
        this.maxNeurons = maxNeurons;
        this.connectors = new ArrayList<Connector>(1);

        if (maxHiddenLayers > 0) {
            hiddenLayers = new ArrayList<>(startingHiddenLayers);
            for (int i = 0; i < startingHiddenLayers; i++) {
                hiddenLayers.add(i, new ArrayList<Neuron>());
                int numNeurons = maxNeurons;
                for (int j = 0; j < numNeurons; j++) {
                    if (Math.random() < chanceOfCreation) continue;
                    hiddenLayers.get(i).add(new HiddenNeuron());
                }
            }
        }

        setRandomConnectors(chanceOfConnection);
    }

    void setRandomConnectors(double chanceOfConnection) {
        // Connect input neurons to first hidden layer
        int connectionCount = (int)(inputNeurons.length * Math.max(1, chanceOfConnection));
        connectionCount *= connectionCount;
        for (int i = 0; i < connectionCount; i++) {
            int inputIndex = (int)(Math.random() * inputNeurons.length);
            int outputIndex = (int)(Math.random() * outputNeurons.length);
            try {
                connectors.add(new Connector(inputNeurons[inputIndex], hiddenLayers.get(0).get(outputIndex), Math.random() * 2 - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Connect hidden layers
        for (int i = 0; i < hiddenLayers.size() - 1; i++) {
            connectionCount = (int)(hiddenLayers.get(i).size() * Math.max(1, chanceOfConnection * Math.random()));
            for (int j = 0; j < connectionCount; j++) {
                int inputIndex = (int)(Math.random() * hiddenLayers.get(i).size());
                int outputIndex = (int)(Math.random() * hiddenLayers.get(i + 1).size());
                connectors.add(new Connector(hiddenLayers.get(i).get(inputIndex), hiddenLayers.get(i + 1).get(outputIndex), Math.random() * 2 - 1));
            }
        }

        // Connect last hidden layer to output neurons
        connectionCount = (int)(hiddenLayers.get(hiddenLayers.size() - 1).size() * Math.max(1, chanceOfConnection * Math.random())) + 1;

        for (int i = 0; i < connectionCount; i++) {
            int inputIndex = (int)(Math.random() * hiddenLayers.get(hiddenLayers.size() - 1).size());
            int outputIndex = (int)(Math.random() * outputNeurons.length);
            try {
                connectors.add(new Connector(hiddenLayers.get(hiddenLayers.size() - 1).get(inputIndex), outputNeurons[outputIndex], Math.random() * 2 - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calculates the output of the neural network.
     * You should call this method after setting the inputs.
     * @return The output of the neural network in an array.
     */
    public double[] getOutput() {
        double[] output = new double[outputNeurons.length];
        resetAll();
        
        for(Connector connector : connectors) {
            connector.transmit();
        }

        for(int i = 0; i < outputNeurons.length; i++) {
            output[i] = outputNeurons[i].getOutput();
        }

        return output;
    }

    /**
     * Resets all neurons in the network except the input neurons.
     */
    void resetAll() {
        for(Neuron neuron : outputNeurons) {
            neuron.reset();
        }
        for(ArrayList<Neuron> layer : hiddenLayers) {
            for(Neuron neuron : layer) {
                neuron.reset();
            }
        }
    }
}
