package src.NeuralNet;

import java.util.ArrayList;

import org.json.simple.JSONObject;

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

    private int x;
    private int y;

    private int id;

    /**
     * Constructs a new HiddenNeuron object with a random bias value.
     */
    public HiddenNeuron() {
        this.id = (int)(Math.random() * 100000);
        this.bias = Math.random() * 2 - 1;
    }

    /**
     * Constructs a new HiddenNeuron object with the specified bias value.
     *
     * @param bias The bias value to be set.
     */
    public HiddenNeuron(double bias) {
        this.id = (int)(Math.random() * 100000);
        this.bias = bias;
    }
    
    public HiddenNeuron(HiddenNeuron og) {
        this.id = (int)(Math.random() * 100000);
        this.bias = og.bias;
        this.inputs = og.inputs;
        this.x = og.x;
        this.y = og.y;
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
     * Sets the position of the neuron.
     * Only used for visualization purposes.
     *
     * @param x The x-coordinate of the neuron.
     * @param y The y-coordinate of the neuron.
     */
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the neuron.
     *
     * @return The x-coordinate of the neuron.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the neuron.
     *
     * @return The y-coordinate of the neuron.
     */

    public int getY() {
        return y;
    }

    /**
     * Converts the input neuron to a JSON string.
     *
     * @return the JSON representation of the input neuron
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("bias", this.bias);
        json.put("x", this.x);
        json.put("y", this.y);
        json.put("id", this.id);
        return json;
    }

    public static HiddenNeuron fromJSON(JSONObject json) {
        HiddenNeuron neuron = new HiddenNeuron();
        neuron.bias = (double) json.get("bias");
        neuron.x = ((Long) json.get("x")).intValue();
        neuron.y = ((Long) json.get("y")).intValue();
        neuron.id = ((Long) json.get("id")).intValue();
        return neuron;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
