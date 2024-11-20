package src.NeuralNet;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class OutputNeuron implements Neuron{
    double output;
    int x;
    int y;

    private int id;
    
    ArrayList<Double> inputs = new ArrayList<Double>();

    public final String name;

    public OutputNeuron(String name) {
        this.id = (int)(Math.random() * 100000);
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

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("x", x);
        json.put("y", y);
        json.put("id", id);
        return json;
    }

    public static OutputNeuron fromJSON(JSONObject json) {
        String name = (String) json.get("name");
        int x = ((Long) json.get("x")).intValue();
        int y = ((Long) json.get("y")).intValue();
        int id = ((Long) json.get("id")).intValue();

        OutputNeuron neuron = new OutputNeuron(name);
        neuron.setPos(x, y);
        neuron.id = id;

        return neuron;
    }
}
