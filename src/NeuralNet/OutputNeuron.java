package src.NeuralNet;

import java.util.ArrayList;

public class OutputNeuron implements Neuron{
    double output;
    int x;
    int y;
    ArrayList<Double> inputs = new ArrayList<Double>();

    public final String name;

    public OutputNeuron(String name) {
        this.name = name;
        output = 0;
    }

    public double getOutput() {
        double sum = 0;
        for (double input : inputs) {
            sum += input;
        }
        output = sum;
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void reset() {
        this.inputs.clear();
    }

    public void addInput(double input) {
        this.inputs.add(input);
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
