package src.NeuralNet;

public class OutputNeuron implements Neuron{
    double output;
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

    public void addInput(double input) {
        output = input;
    }
}
