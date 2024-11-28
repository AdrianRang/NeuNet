package src;
import java.util.ArrayList;

import src.NeuralNet.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Network {
    public Neuron[] inputNeurons;
    public Neuron[] outputNeurons;
    public ArrayList<ArrayList<Neuron>> hiddenLayers; // this is array list because we don't know how many hidden layers there will be
    public ArrayList<Connector> connectors;
    public int maxHiddenLayers;
    public int maxNeurons;

    public double score = 0;

    /**
     * Set of chances for mutation in a network
     */
    public static class Chances {
        public static class ConnectorChances {
            public double creation;
            public double deletion;
            public double change;
            public double weight;
            public double[] weightRange;

            public ConnectorChances(double creation, double deletion, double change, double weight, double[] weighRange) /*throws Exception*/ {
            this.creation = creation;
            this.deletion = deletion;
            this.change = change;
            this.weight = weight;
            // if(weighRange.length != 2) throw new Exception("Array needs to be two elements long");
            this.weightRange = weighRange;
        }
        }

        public static class HiddenNeuronChances {
            public double creation;
            public double deletion;
            public double bias;
            public double[] biasRange;

            public HiddenNeuronChances(double creation, double deletion, double bias, double[] biasRange) /*throws Exception*/ {
                this.creation = creation;
                this.deletion = deletion;
                this.bias = bias;
                // if(biasRange.length != 2) throw new Exception("Array needs to be two elements long");
                this.biasRange = biasRange;
            }
        }

        public static class LayerChances {
            public double creationChance;
            public double deletionChance;

            public LayerChances(double creationChance, double deletionChance) {
                this.creationChance = creationChance;
                this.deletionChance = deletionChance;
            }
        }
        
        public ConnectorChances connectorChances;
        public HiddenNeuronChances hiddenNeuronChances;
        public LayerChances layerChances;

        public Chances(ConnectorChances connectorChances, HiddenNeuronChances hiddenNeuronChances, LayerChances layerChances) {
            this.connectorChances = connectorChances;
            this.hiddenNeuronChances = hiddenNeuronChances;
            this.layerChances = layerChances;
        }
    }

    public Network(String path) throws FileNotFoundException {
        FileReader json = new FileReader(path);
        try {
            JSONObject jsonObject = (JSONObject) new org.json.simple.parser.JSONParser().parse(json);
            JSONArray inputNeuronsArray = (JSONArray) jsonObject.get("inputNeurons");
            JSONArray outputNeuronsArray = (JSONArray) jsonObject.get("outputNeurons");
            JSONArray hiddenLayersArray = (JSONArray) jsonObject.get("hiddenLayers");
            JSONArray connectorsArray = (JSONArray) jsonObject.get("connectors");

            this.inputNeurons = new Neuron[inputNeuronsArray.size()];
            for (int i = 0; i < inputNeuronsArray.size(); i++) {
                this.inputNeurons[i] = InputNeuron.fromJSON((JSONObject) inputNeuronsArray.get(i));
            }

            this.outputNeurons = new Neuron[outputNeuronsArray.size()];
            for (int i = 0; i < outputNeuronsArray.size(); i++) {
            this.outputNeurons[i] = OutputNeuron.fromJSON((JSONObject) outputNeuronsArray.get(i));
            }

            this.hiddenLayers = new ArrayList<>();
            for (int i = 0; i < hiddenLayersArray.size(); i++) {
            JSONArray layerArray = (JSONArray) hiddenLayersArray.get(i);
            ArrayList<Neuron> layer = new ArrayList<>();
            for (int j = 0; j < layerArray.size(); j++) {
                layer.add(HiddenNeuron.fromJSON((JSONObject) layerArray.get(j)));
            }
            this.hiddenLayers.add(layer);
            }

            this.connectors = new ArrayList<>();
            for (int i = 0; i < connectorsArray.size(); i++) {
                this.connectors.add(Connector.fromJSON((JSONObject) connectorsArray.get(i), getNeurons()));
            }

            this.maxHiddenLayers = ((Long) jsonObject.get("maxHiddenLayers")).intValue();
            this.maxNeurons = ((Long) jsonObject.get("maxNeurons")).intValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Network(Network net) {
        this.inputNeurons = net.inputNeurons;
        this.outputNeurons = net.outputNeurons;
        this.maxHiddenLayers = net.maxHiddenLayers;
        this.maxNeurons = net.maxNeurons;

        this.hiddenLayers = new ArrayList<>();
        this.connectors = new ArrayList<>();

        ArrayList<Integer> addedNeurons = new ArrayList<>();
        for (Connector connector : net.connectors) {
            Neuron fromNeuron = connector.getFromNeuron();
            int fromId = fromNeuron.getId();
            Neuron toNeuron = connector.getToNeuron();
            int toId = toNeuron.getId();

            if (fromNeuron instanceof HiddenNeuron && !addedNeurons.contains(fromNeuron.getId())) {
                addedNeurons.add(fromNeuron.getId());
                fromNeuron = new HiddenNeuron((HiddenNeuron)fromNeuron);
                fromNeuron.setId(fromId);
                addNeuronToLayer((HiddenNeuron) fromNeuron, net.getLayerIndex(fromId));
            }

            if (toNeuron instanceof HiddenNeuron && !addedNeurons.contains(toNeuron.getId())) {
                addedNeurons.add(toNeuron.getId());
                toNeuron = new HiddenNeuron((HiddenNeuron)toNeuron);
                toNeuron.setId(toId);
                addNeuronToLayer((HiddenNeuron) toNeuron, net.getLayerIndex(toId));
            }

            this.connectors.add(new Connector(findById(fromNeuron.getId()), findById(toNeuron.getId()), connector.getWeight()));
        }
    }

    /**
     * A neural network.
     * @param inputNeurons The input neurons.
     * @param maxHiddenLayers The maximum number of hidden layers.
     * @param maxNeurons The maximum number of neurons per layer.
     * @param outputNeurons The output neurons.
     * @param connectors The connectors.
     */
    public Network(Neuron[] inputNeurons, int maxHiddenLayers, int maxNeurons, Neuron[] outputNeurons, ArrayList<Connector> connectors) {
        this.inputNeurons = inputNeurons;
        this.outputNeurons = outputNeurons;
        this.maxHiddenLayers = maxHiddenLayers;
        this.maxNeurons = maxNeurons;
        this.connectors = connectors;
        if (maxHiddenLayers > 0) {
            hiddenLayers = new ArrayList<>();
        }
    }

    public Network(Network net, Chances chances) {
        this.inputNeurons = net.inputNeurons;
        this.outputNeurons = net.outputNeurons;
        this.maxHiddenLayers = net.maxHiddenLayers;
        this.maxNeurons = net.maxNeurons;

        this.hiddenLayers = new ArrayList<>();
        hiddenLayers.add(new ArrayList<>());
        this.connectors = new ArrayList<>();

        ArrayList<Neuron> addedNeurons = new ArrayList<>();
        for (Connector connector : net.connectors) {
            Neuron fromNeuron = connector.getFromNeuron();
            int fromId = fromNeuron.getId();
            Neuron toNeuron = connector.getToNeuron();
            int toId = toNeuron.getId();

            if(addedNeurons.contains(fromNeuron) && addedNeurons.contains(toNeuron)) continue;

            if (fromNeuron instanceof HiddenNeuron && !addedNeurons.contains(fromNeuron)) {
                fromNeuron = new HiddenNeuron((HiddenNeuron)fromNeuron);
                addedNeurons.add(fromNeuron);
                fromNeuron.setId(fromId);
                addNeuronToLayer((HiddenNeuron) fromNeuron, net.getLayerIndex(fromId));
            }

            if (toNeuron instanceof HiddenNeuron && !addedNeurons.contains(toNeuron)) {
                addedNeurons.add(toNeuron);
                toNeuron = new HiddenNeuron((HiddenNeuron)toNeuron);
                toNeuron.setId(toId);
                addNeuronToLayer((HiddenNeuron) toNeuron, net.getLayerIndex(toId));
            }

            this.connectors.add(new Connector(findById(fromId), findById(toId), connector.getWeight()));
        }

        // Apply mutation according to chances
        for (ArrayList<Neuron> layer : hiddenLayers) {
            ArrayList<Neuron> toBeRemoved = new ArrayList<>();
            ArrayList<Neuron> toBeAdded = new ArrayList<>();
            for (Neuron neuron : layer) {
            if (neuron instanceof HiddenNeuron) {
                HiddenNeuron hiddenNeuron = (HiddenNeuron) neuron;
                if (Math.random() < chances.hiddenNeuronChances.creation) {
                // Create a new neuron
                hiddenNeuron = new HiddenNeuron();
                toBeAdded.add(hiddenNeuron);
                }
                if (Math.random() < chances.hiddenNeuronChances.deletion && layer.size() - toBeRemoved.size() > 0) {
                // Schedule for deletion
                toBeRemoved.add(hiddenNeuron);
                }
                if (Math.random() < chances.hiddenNeuronChances.bias) {
                // Change the bias of the neuron
                hiddenNeuron.setBias(chances.hiddenNeuronChances.biasRange[0] + Math.random() * (chances.hiddenNeuronChances.biasRange[1] - chances.hiddenNeuronChances.biasRange[0]));
                }
            }
            }

            layer.removeAll(toBeRemoved);
            layer.addAll(toBeAdded);
            for (Neuron removedNeuron : toBeRemoved) {
            connectors.removeIf(connector -> connector.getFromNeuron() == removedNeuron || connector.getToNeuron() == removedNeuron);
            }
        }

        ArrayList<ArrayList<Neuron>> tbRemoved = new ArrayList<>();
        for(ArrayList<Neuron> layer : hiddenLayers) {
            if (layer.size() == 0) tbRemoved.add(layer);
        }
        hiddenLayers.removeAll(tbRemoved);

        if (hiddenLayers.size() == 0) hiddenLayers.add(new ArrayList<>());
        ArrayList<Connector> toBeRemoved = new ArrayList<>();
        ArrayList<Connector> toBeAdded = new ArrayList<>();
        for (Connector connector : connectors) {
            if (Math.random() < chances.connectorChances.creation) {
                if(hiddenLayers.size() == 0) hiddenLayers.add(new ArrayList<>());
                // if(Math.random() < 0.5) {

                //     Neuron fromNeuron = inputNeurons[(int) (Math.random() * inputNeurons.length)];
                //     Neuron toNeuron = hiddenLayers.get(0).get((int) (Math.random() * hiddenLayers.get(0).size()));

                //     toBeAdded.add(new Connector(fromNeuron, toNeuron, chances.connectorChances.weightRange[0] + Math.random() * (chances.connectorChances.weightRange[1] - chances.connectorChances.weightRange[0])));
                    
                // } else {
                //     // Create a new connector
                //     Neuron fromNeuron = hiddenLayers.get(0).get((int) (Math.random() * hiddenLayers.get(0).size()));
                //     Neuron toNeuron = outputNeurons[(int) (Math.random() * outputNeurons.length)];
                    
                //     toBeAdded.add(new Connector(fromNeuron, toNeuron, chances.connectorChances.weightRange[0] + Math.random() * (chances.connectorChances.weightRange[1] - chances.connectorChances.weightRange[0])));
                // }
                double rand = Math.random();
                if (rand < 1/((double)hiddenLayers.size() + 1)) {
                    Neuron fromNeuron = inputNeurons[(int) (Math.random() * inputNeurons.length)];
                    Neuron toNeuron = hiddenLayers.get(0).get((int)(Math.random() * (hiddenLayers.get(0).size())));

                    toBeAdded.add(new Connector(fromNeuron, toNeuron, chances.connectorChances.weightRange[0] + Math.random() * (chances.connectorChances.weightRange[1] - chances.connectorChances.weightRange[0])));
                } else if (rand < (2/((double)hiddenLayers.size() + 1))) {
                    Neuron fromNeuron = hiddenLayers.get(hiddenLayers.size()-1).get((int) (Math.random() * (hiddenLayers.get(hiddenLayers.size()-1).size())));
                    Neuron toNeuron = outputNeurons[(int) (Math.random() * outputNeurons.length)];
                    
                    toBeAdded.add(new Connector(fromNeuron, toNeuron, chances.connectorChances.weightRange[0] + Math.random() * (chances.connectorChances.weightRange[1] - chances.connectorChances.weightRange[0])));
                } else {
                    int layer = (int)((hiddenLayers.size()-1) * Math.random());
                    if (hiddenLayers.get(layer).size() == 0) hiddenLayers.get(layer).add(new HiddenNeuron());
                    if (hiddenLayers.get(layer+1).size() == 0) hiddenLayers.get(layer+1).add(new HiddenNeuron()); 
                    Neuron fromNeuron = hiddenLayers.get(layer).get((int) (Math.random() * hiddenLayers.get(layer).size()));
                    Neuron toNeuron = hiddenLayers.get(layer+1).get((int) (Math.random() * hiddenLayers.get(layer+1).size()));
                    toBeAdded.add(new Connector(fromNeuron, toNeuron, chances.connectorChances.weightRange[0] + Math.random() * (chances.connectorChances.weightRange[1] - chances.connectorChances.weightRange[0])));
                }

            }
            if (Math.random() < chances.connectorChances.deletion) {
                // Delete the connector
                toBeRemoved.add(connector);
            }
            if (Math.random() < chances.connectorChances.change) {
                // Change the weight of the connector
                connector.setWeight(chances.connectorChances.weightRange[0] + Math.random() * (chances.connectorChances.weightRange[1] - chances.connectorChances.weightRange[0]));
            }
        }
        
        connectors.removeAll(toBeRemoved);
        connectors.addAll(toBeAdded);

        // Remove duplicate connectors
        ArrayList<Connector> uniqueConnectors = new ArrayList<>();
        for (Connector connector : connectors) {
            boolean isDuplicate = false;
            for (Connector uniqueConnector : uniqueConnectors) {
            if (connector.getFromNeuron().equals(uniqueConnector.getFromNeuron()) &&
                connector.getToNeuron().equals(uniqueConnector.getToNeuron())) {
                isDuplicate = true;
                break;
            }
            }
            if (!isDuplicate) {
                uniqueConnectors.add(connector);
            }
        }
        uniqueConnectors.removeIf(c -> !containsNeuron(c.getFromNeuron()) || !containsNeuron(c.getToNeuron()));
        connectors = uniqueConnectors;

        tbRemoved = new ArrayList<>();
        for(ArrayList<Neuron> layer : hiddenLayers) {
            if (layer.size() == 0) tbRemoved.add(layer);
        }
        hiddenLayers.removeAll(tbRemoved);

        if (hiddenLayers.size() == 0) {
            ArrayList<Neuron> newLayer = new ArrayList<>();
            newLayer.add(new HiddenNeuron());
            hiddenLayers.add(newLayer);
        }
        if(Math.random() < chances.layerChances.creationChance) {
            ArrayList<Neuron> newLayer = new ArrayList<>();
            newLayer.add(new HiddenNeuron());
            connectors.add(new Connector(newLayer.get(0), outputNeurons[(int)(outputNeurons.length * Math.random())], Math.random() * 2 - 1));
            int index = hiddenLayers.size() != 0 ? hiddenLayers.size() - 1 : 0;
            connectors.add(new Connector(hiddenLayers.get(index).get((int)(Math.random() * hiddenLayers.get(index).size())), newLayer.get(0), Math.random() * 2 - 1));
            hiddenLayers.add(newLayer);
        }

        // if(Math.random() < chances.layerChances.deletionChance) {
            // removeLayer((int)(Math.random() * hiddenLayers.size()));
            // TODO: Make this function (Make sure to remove connectors that go either from or to that layer)
        // }

        
        
    }

    /**
     * A neural network.
     * @param inputNeurons The input neurons.
     * @param maxHiddenLayers The maximum number of hidden layers.
     * @param maxNeurons The maximum number of neurons per layer.
     * @param outputNeurons The output neurons.
     */
    public Network(Neuron[] inputNeurons, int startingHiddenLayers, int maxHiddenLayers, int maxNeurons, Neuron[] outputNeurons, double chanceOfConnection, double chanceOfCreation) {
        this.inputNeurons = inputNeurons;
        this.outputNeurons = outputNeurons;
        this.maxHiddenLayers = maxHiddenLayers;
        this.maxNeurons = maxNeurons;
        this.connectors = new ArrayList<Connector>(1);

        if (maxHiddenLayers > 0) {
            hiddenLayers = new ArrayList<>(startingHiddenLayers);
            for (int i = 0; i < startingHiddenLayers; i++) {
                hiddenLayers.add(i, new ArrayList<Neuron>());
                int numNeurons = maxNeurons;
                for (int j = 0; j < numNeurons; j++) {
                    if (Math.random() > chanceOfCreation) continue;
                    hiddenLayers.get(i).add(new HiddenNeuron());
                }
            }
        }

        setRandomConnectors(chanceOfConnection);
    }

    void setRandomConnectors(double chanceOfConnection) {
        
        // Connect input neurons to first hidden layer
        int connectionCount = (int)((inputNeurons.length + hiddenLayers.get(0).size()) * Math.max(1, chanceOfConnection));
        ArrayList<int[]> currCons = new ArrayList<int[]>();
        for (int i = 0; i < connectionCount; i++) {
            int inputIndex = (int)(Math.random() * inputNeurons.length);
            int outputIndex = (int)(Math.random() * hiddenLayers.get(0).size());
            if(contains(currCons, new int[] {inputIndex, outputIndex})) continue;
            connectors.add(new Connector(inputNeurons[inputIndex], hiddenLayers.get(0).get(outputIndex), Math.random() * 2 - 1));
            currCons.add(new int[] {inputIndex, outputIndex});
        }
        // System.out.println(connectors.size());

        currCons.clear();

        // Connect hidden layers
        for (int i = 0; i < hiddenLayers.size() - 1; i++) {
            connectionCount = (int)(hiddenLayers.get(i).size() * Math.max(1, chanceOfConnection * Math.random()));
            for (int j = 0; j < connectionCount; j++) {
                int inputIndex = (int)(Math.random() * hiddenLayers.get(i).size());
                int outputIndex = (int)(Math.random() * hiddenLayers.get(i + 1).size());
                if(contains(currCons, new int[] {inputIndex, outputIndex})) continue;
                connectors.add(new Connector(hiddenLayers.get(i).get(inputIndex), hiddenLayers.get(i + 1).get(outputIndex), Math.random() * 2 - 1));
                currCons.add(new int[] {inputIndex, outputIndex});
            }
        }
        // System.out.println(connectors.size());

        currCons.clear();

        // Connect last hidden layer to output neurons
        connectionCount = (int)(hiddenLayers.get(hiddenLayers.size() - 1).size() * Math.max(1, chanceOfConnection * Math.random())) + 1;

        for (int i = 0; i < connectionCount; i++) {
            int inputIndex = (int)(Math.random() * hiddenLayers.get(hiddenLayers.size() - 1).size());
            int outputIndex = (int)(Math.random() * outputNeurons.length);
            try {
                if(contains(currCons, new int[] {inputIndex, outputIndex})) {continue;};
                connectors.add(new Connector(hiddenLayers.get(hiddenLayers.size() - 1).get(inputIndex), outputNeurons[outputIndex], Math.random() * 2 - 1));
                currCons.add(new int[] {inputIndex, outputIndex});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Calculates the output of the neural network.
     * You should call this method after setting the inputs.
     * @return The output of the neural network in an array.
     */
    public double[] getOutput() {
        double[] output = new double[outputNeurons.length];
        resetAll();
        
        for(Connector connector : connectors) {
            connector.transmit();
        }

        for(int i = 0; i < outputNeurons.length; i++) {
            output[i] = outputNeurons[i].getOutput();
        }

        return output;
    }

    /**
     * Resets all neurons in the network except the input neurons.
     */
    void resetAll() {
        for(Neuron neuron : outputNeurons) {
            neuron.reset();
        }
        for(ArrayList<Neuron> layer : hiddenLayers) {
            for(Neuron neuron : layer) {
                neuron.reset();
            }
        }
    }

    Neuron findById(int id) {
        for (Neuron neuron : inputNeurons) {
            if (neuron.getId() == id) {
            return neuron;
            }
        }
        for (ArrayList<Neuron> layer : hiddenLayers) {
            for (Neuron neuron : layer) {
            if (neuron.getId() == id) {
                return neuron;
            }
            }
        }
        for (Neuron neuron : outputNeurons) {
            if (neuron.getId() == id) {
            return neuron;
            }
        }
        return null;
    }
    
    Neuron[] getNeurons() {
        int hiddenLayerNeuronCount = 0;
        for(ArrayList<Neuron> layer : hiddenLayers) hiddenLayerNeuronCount += layer.size();
        Neuron[] neurons = new Neuron[inputNeurons.length + hiddenLayerNeuronCount + outputNeurons.length];

        int index = 0;
        for (Neuron neuron : inputNeurons) {
            neurons[index++] = neuron;
        }
        for (ArrayList<Neuron> layer : hiddenLayers) {
            for (Neuron neuron : layer) {
            neurons[index++] = neuron;
            }
        }
        for (Neuron neuron : outputNeurons) {
            neurons[index++] = neuron;
        }
        return neurons;
    }

    @SuppressWarnings("unchecked")
    void outputAsJSON() {
        JSONObject json = new JSONObject();
        JSONArray inputNeuronsArray = new JSONArray();
        for (Neuron neuron : inputNeurons) {
            inputNeuronsArray.add(neuron.toJSON());
        }
        json.put("inputNeurons", inputNeuronsArray);

        JSONArray outputNeuronsArray = new JSONArray();
        for (Neuron neuron : outputNeurons) {
            outputNeuronsArray.add(neuron.toJSON());
        }
        json.put("outputNeurons", outputNeuronsArray);

        JSONArray hiddenLayersArray = new JSONArray();
        for (ArrayList<Neuron> layer : hiddenLayers) {
            JSONArray layerArray = new JSONArray();
            for (Neuron neuron : layer) {
                layerArray.add(neuron.toJSON());
            }
            hiddenLayersArray.add(layerArray);
        }
        json.put("hiddenLayers", hiddenLayersArray);

        JSONArray connectorsArray = new JSONArray();
        for (Connector connector : connectors) {
            connectorsArray.add(connector.toJSON());
        }
        json.put("connectors", connectorsArray);

        json.put("maxHiddenLayers", maxHiddenLayers);
        json.put("maxNeurons", maxNeurons);

        try (FileWriter file = new FileWriter("network.json")) {
            file.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean contains(ArrayList<int[]> cons, int[] arr){
        for(int[] a : cons){
            if(a[0] == arr[0] && a[1] == arr[1]){
                return true;
            }
        }
        return false;
    }

    boolean contains(ArrayList<int[]> cons, int a, int b){
        for(int[] arr : cons){
            if(arr[0] == a && arr[1] == b){
                return true;
            }
        }

        return false;
    }

    boolean containsNeuron(Neuron neuron) {
        for (ArrayList<Neuron> layer : this.hiddenLayers) {
            if (layer.contains(neuron)) {
                return true;
            }
        }
        for (Neuron neu : inputNeurons) {
            if(neuron == neu) return true;
        }
        for (Neuron neu : outputNeurons) {
            if(neuron == neu) return true;
        }
        return false;
    }

    void addNeuronToLayer(HiddenNeuron neuron, int layerIndex) {
        while (hiddenLayers.size() <= layerIndex) hiddenLayers.add(new ArrayList<>());
        hiddenLayers.get(layerIndex).add(neuron);
    }

    public int getLayerIndex(Neuron neuron) {
        for (int i = 0; i < hiddenLayers.size(); i++) {
            if (hiddenLayers.get(i).contains(neuron)) {
                return i;
            }
        }
        return -1;
    }

    public int getLayerIndex(int neuronId) {
        for (int i = 0; i < hiddenLayers.size(); i++) {
            for (Neuron neuron : hiddenLayers.get(i)) {
                if (neuron.getId() == neuronId) {
                    return i;
                }
            }
        }
        return -1;
    }
}
