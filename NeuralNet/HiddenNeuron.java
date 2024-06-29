package NeuralNet;

import java.util.ArrayList;

/**
 * Represents a hidden neuron in a neural network.
 * Implements the Neuron interface.
 */
/**
 * Represents a hidden neuron in a neural network.
 */
public class HiddenNeuron implements Neuron {
    private ArrayList<Double> inputs = new ArrayList<>();
    private double bias;

    /**
     * Constructs a new HiddenNeuron object with a random bias value.
     */
    public HiddenNeuron() {
        this.bias = Math.random();
    }

    /**
     * Constructs a new HiddenNeuron object with the specified bias value.
     *
     * @param bias The bias value to be set.
     */
    public HiddenNeuron(double bias) {
        this.bias = bias;
    }

    /**
     * Calculates and returns the output of the hidden neuron.
     *
     * @return The output of the hidden neuron.
     */
    public double getOutput() {
        return calculateOutput();
    }

    /**
     * Sets the bias value of the hidden neuron.
     *
     * @param bias The bias value to be set.
     */
    public void setBias(double bias) {
        this.bias = bias;
    }

    /**
     * Gets the bias value of the hidden neuron.
     *
     * @return The bias value of the hidden neuron.
     */
    public double getBias() {
        return bias;
    }

    /**
     * Resets the inputs of the hidden neuron.
     */
    public void reset() {
        inputs.clear();
    }

    private double calculateOutput() {
        double sum = 0;
        for (double input : inputs) {
            sum += input;
        }
        sum += bias;
        return doActivationFunction(sum);
    }

    /**
     * Adds an input value to the hidden neuron.
     * The input value must be already weighted.
     * 
     * @param input The input value to be added.
     */
    public void addInput(double input) {
        this.inputs.add(input);
    }

    private double doActivationFunction(double input) {
        switch (NetConstants.activationFunction) {
            case SIGMOID:
                return 1 / (1 + Math.exp(-input));
            case RELU:
                return Math.max(0, input);
            case TANH:
                return Math.tanh(input);
            case LEAKY_RELU:
                return Math.max(0.01 * input, input);
            default:
                return input;
        }
    }

    /**
     * Represents the activation functions that can be used by the hidden neuron.
     */
    public static enum ActivationFunction {
        SIGMOID,
        RELU,
        TANH,
        LEAKY_RELU
    }
}
