package src.NeuralNet;

import org.json.simple.JSONObject;

public interface Neuron {
    public void addInput(double input);
    public double getOutput();
    public void reset();

    // For visualization purposes
    public void setPos(int x, int y);
    public int getX();
    public int getY();

    public JSONObject toJSON();
    public int getId();
    public void setId(int id);
}