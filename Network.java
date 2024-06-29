import java.util.ArrayList;

import NeuralNet.*;

public class Network {
    InputNeuron[] inputNeurons;
    OutputNeuron[] outputNeurons;
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
    public Network(InputNeuron[] inputNeurons, int maxHiddenLayers, int maxNeurons, OutputNeuron[] outputNeurons, ArrayList<Connector> connectors) {
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
    public Network(InputNeuron[] inputNeurons, int startingHiddenLayers, int maxHiddenLayers, int maxNeurons, OutputNeuron[] outputNeurons, double chanceOfConnection, double chanceOfCreation) {
        this.inputNeurons = inputNeurons;
        this.outputNeurons = outputNeurons;
        this.maxHiddenLayers = maxHiddenLayers;
        this.maxNeurons = maxNeurons;
        this.connectors = new ArrayList<>();
        setRandomConnectors();
        if (maxHiddenLayers > 0) {
            hiddenLayers = new ArrayList<>();
            for (int i = 0; i < startingHiddenLayers; i++) {
                hiddenLayers.add(new ArrayList<>());
                int numNeurons = (int) (Math.random() * chanceOfConnection * maxNeurons);
                for (int j = 0; j < numNeurons; j++) {
                    hiddenLayers.get(i).add(new HiddenNeuron());
                }
            }
        }
    }

    void setRandomConnectors() {
        for (int i = 0; i < inputNeurons.length; i++) {
            for (int j = 0; j < outputNeurons.length; j++) {
                connectors.add(new Connector(inputNeurons[i], outputNeurons[j], Math.random()));
            }
        }
    }
}
