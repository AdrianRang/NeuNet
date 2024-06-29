package NeuralNet;

public interface Neuron {
    void addInput(double input);
    double getOutput();
    void reset();
}