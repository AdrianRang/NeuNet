package src.NeuralNet;

/**
 * Represents an input neuron in a neural network.
 */
public class InputNeuron implements Neuron {
    private double output;
    private int x;
    private int y;

    public final String name;

    /**
     * Creates an input neuron with an initial output of 0.
     */
    public InputNeuron(String name) {
        this.name = name;
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

    /**
     * Sets the position of the neuron.
     * Only used for visualization purposes.
     *
     * @param x the x-coordinate of the neuron
     * @param y the y-coordinate of the neuron
     */
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-coordinate of the neuron.
     *
     * @return the x-coordinate of the neuron
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the neuron.
     *
     * @return the y-coordinate of the neuron
     */
    public int getY() {
        return y;
    }
}
