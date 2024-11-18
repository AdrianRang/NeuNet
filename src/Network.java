package src;
import java.util.ArrayList;

import src.NeuralNet.*;

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
            public double[] weighRange;

            public ConnectorChances(double creation, double deletion, double change, double weight, double[] weighRange) /*throws Exception*/ {
            this.creation = creation;
            this.deletion = deletion;
            this.change = change;
            this.weight = weight;
            // if(weighRange.length != 2) throw new Exception("Array needs to be two elements long");
            this.weighRange = weighRange;
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
        
        public ConnectorChances connectorChances;
        public HiddenNeuronChances hiddenNeuronChances;

        public Chances(ConnectorChances connectorChances, HiddenNeuronChances hiddenNeuronChances) {
            this.connectorChances = connectorChances;
            this.hiddenNeuronChances = hiddenNeuronChances;
        }
    }

    public Network(Network net) {
        this.inputNeurons = net.inputNeurons;
        this.outputNeurons = net.outputNeurons;
        this.maxHiddenLayers = net.maxHiddenLayers;
        this.maxNeurons = net.maxNeurons;

        this.hiddenLayers = new ArrayList<>();
        this.connectors = new ArrayList<>();

        ArrayList<Neuron> addedNeurons = new ArrayList<>();
        for (Connector connector : net.connectors) {
            Neuron fromNeuron = connector.getFromNeuron();
            Neuron toNeuron = connector.getToNeuron();

            if (fromNeuron instanceof HiddenNeuron && !addedNeurons.contains(fromNeuron)) {
                addedNeurons.add(fromNeuron);
                fromNeuron = new HiddenNeuron((HiddenNeuron)fromNeuron);
                addNeuronToLayer((HiddenNeuron) fromNeuron, hiddenLayers);
            }

            if (toNeuron instanceof HiddenNeuron && !addedNeurons.contains(toNeuron)) {
                addedNeurons.add(toNeuron);
                toNeuron = new HiddenNeuron((HiddenNeuron)toNeuron);
                addNeuronToLayer((HiddenNeuron) toNeuron, hiddenLayers);
            }

            this.connectors.add(new Connector(fromNeuron, toNeuron, connector.getWeight()));
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
        this.connectors = new ArrayList<>();

        ArrayList<Neuron> addedNeurons = new ArrayList<>();
        for (Connector connector : net.connectors) {
            Neuron fromNeuron = connector.getFromNeuron();
            Neuron toNeuron = connector.getToNeuron();

            if (fromNeuron instanceof HiddenNeuron && !addedNeurons.contains(fromNeuron)) {
            addedNeurons.add(fromNeuron);
            fromNeuron = new HiddenNeuron((HiddenNeuron)fromNeuron);
            addNeuronToLayer((HiddenNeuron) fromNeuron, hiddenLayers);
            }

            if (toNeuron instanceof HiddenNeuron && !addedNeurons.contains(toNeuron)) {
            addedNeurons.add(toNeuron);
            toNeuron = new HiddenNeuron((HiddenNeuron)toNeuron);
            addNeuronToLayer((HiddenNeuron) toNeuron, hiddenLayers);
            }

            this.connectors.add(new Connector(fromNeuron, toNeuron, connector.getWeight()));
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
                if (Math.random() < chances.hiddenNeuronChances.deletion) {
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


        // this.inputNeurons = net.inputNeurons;
        // this.outputNeurons = net.outputNeurons;
        // this.maxHiddenLayers = net.maxHiddenLayers;
        // this.maxNeurons = net.maxNeurons;
        // this.connectors = net.connectors;
        // this.hiddenLayers = new ArrayList<>();

        // for(ArrayList<Neuron> layer : net.hiddenLayers) {
        //     ArrayList<Neuron> newLayer = new ArrayList<>();
        //     for(Neuron neuron : layer) {
        //         newLayer.add(new HiddenNeuron((HiddenNeuron)neuron));
        //     }
        // }

        // // Apply mutation according to chances
        // for (ArrayList<Neuron> layer : hiddenLayers) {
        //     ArrayList<Neuron> toBeRemoved = new ArrayList<>();
        //     ArrayList<Neuron> toBeAdded = new ArrayList<>();
        //     for (Neuron neuron : layer) {
        //     if (neuron instanceof HiddenNeuron) {
        //         HiddenNeuron hiddenNeuron = (HiddenNeuron) neuron;
        //         if (Math.random() < chances.hiddenNeuronChances.creation) {
        //         // Create a new neuron
        //             hiddenNeuron = new HiddenNeuron();
        //             toBeAdded.add(hiddenNeuron);
        //         }
        //         if (Math.random() < chances.hiddenNeuronChances.deletion) {
        //             // Schedule for deletion
        //             toBeRemoved.add(hiddenNeuron);
        //         }
        //         if (Math.random() < chances.hiddenNeuronChances.bias) {
        //             // Change the bias of the neuron
        //             hiddenNeuron.setBias(chances.hiddenNeuronChances.biasRange[0] + Math.random() * (chances.hiddenNeuronChances.biasRange[1] - chances.hiddenNeuronChances.biasRange[0]));
        //         }
        //     }
        //     }

        //     layer.removeAll(toBeRemoved);
        //     layer.addAll(toBeAdded);
        //     for (Neuron removedNeuron : toBeRemoved) {
        //         connectors.removeIf(connector -> connector.getFromNeuron() == removedNeuron || connector.getToNeuron() == removedNeuron);
        //     }
        // }

        ArrayList<Connector> toBeRemoved = new ArrayList<>();
        ArrayList<Connector> toBeAdded = new ArrayList<>();
        for (Connector connector : connectors) {
            if (Math.random() < chances.connectorChances.creation) {
                if(Math.random() < 0.5) {
                    // Create a new connector
                    Neuron fromNeuron = inputNeurons[(int) (Math.random() * inputNeurons.length)];
                    Neuron toNeuron = hiddenLayers.get(0).get((int) (Math.random() * hiddenLayers.get(0).size()));
                    toBeAdded.add(new Connector(fromNeuron, toNeuron, chances.connectorChances.weighRange[0] + Math.random() * (chances.connectorChances.weighRange[1] - chances.connectorChances.weighRange[0])));
                } else {
                    // Create a new connector
                    Neuron fromNeuron = hiddenLayers.get(0).get((int) (Math.random() * hiddenLayers.get(0).size()));
                    Neuron toNeuron = outputNeurons[(int) (Math.random() * outputNeurons.length)];
                    toBeAdded.add(new Connector(fromNeuron, toNeuron, chances.connectorChances.weighRange[0] + Math.random() * (chances.connectorChances.weighRange[1] - chances.connectorChances.weighRange[0])));
                }
            }
            if (Math.random() < chances.connectorChances.deletion) {
            // Delete the connector
            toBeRemoved.add(connector);
            }
            if (Math.random() < chances.connectorChances.change) {
            // Change the weight of the connector
            connector.setWeight(chances.connectorChances.weighRange[0] + Math.random() * (chances.connectorChances.weighRange[1] - chances.connectorChances.weighRange[0]));
            }
        }

        connectors.removeAll(toBeRemoved);
        connectors.addAll(toBeAdded);
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
        return false;
    }

    void addNeuronToLayer(HiddenNeuron neuron, ArrayList<ArrayList<Neuron>> layers) {
        if(layers.size() == 0) {
            layers.add(new ArrayList<>());
        }
        for (ArrayList<Neuron> layer : layers) {
            // if (layer.size() < maxNeurons) {
                layer.add(neuron);
                return;
            // }
        }
        // if (layers.size() < maxHiddenLayers) {
        //     ArrayList<Neuron> newLayer = new ArrayList<>();
        //     newLayer.add(neuron);
        //     layers.add(newLayer);
        // }
    }
}
