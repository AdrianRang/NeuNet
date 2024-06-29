package src.NeuralNet;

/**
 * Represents an input neuron in a neural network.
 */
public class InputNeuron implements Neuron {
    private double output;

    /**
     * Creates an input neuron with an initial output of 0.
     */
    public InputNeuron() {
        output = 0;
    }

    /**
     * Returns the output value of the input neuron.
     *
     * @return the output value of the input neuron
     */
    public double getOutput() {
        return output;
    }

    /**
     * Resets the output value of the input neuron to 0.
     */
    public void reset() {
        output = 0;
    }

    /**
     * Adds an input value to the input neuron.
     *
     * @param input the input value to be added
     */
    public void addInput(double input) {
        output = input;
    }
}
