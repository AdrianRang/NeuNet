package src.NeuralNet;

public class OutputNeuron implements Neuron{
    double output;
    int x;
    int y;

    public final String name;

    public OutputNeuron(String name) {
        this.name = name;
        output = 0;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void reset() {
        output = 0;
    }

    //TODO: needs to be the same as hidden neuron
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
