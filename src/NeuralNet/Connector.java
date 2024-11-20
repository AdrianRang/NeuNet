package src.NeuralNet;

import org.json.simple.JSONObject;

public class Connector {
    private Neuron from;
    private Neuron to;
    private double weight;

    public Connector(Neuron from, Neuron to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Neuron getFrom() {
        return from;
    }

    public Neuron getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void transmit() {
        to.addInput(from.getOutput() * weight);
    }

    public Neuron getFromNeuron() {
        return from;
    }

    public Neuron getToNeuron() {
        return to;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("from", from.getId());
        json.put("to", to.getId());
        json.put("weight", weight);
        return json;
    }

    public static Connector fromJSON(JSONObject json, Neuron[] neurons) {
        int fromId = ((Long) json.get("from")).intValue();
        int toId = ((Long) json.get("to")).intValue();
        double weight = (Double) json.get("weight");

        Neuron from = null;
        Neuron to = null;

        for (Neuron neu : neurons) {
            if(neu.getId() == fromId) from = neu;
            else if (neu.getId() == toId) to = neu;
        }

        return new Connector(from, to, weight);
    }
}