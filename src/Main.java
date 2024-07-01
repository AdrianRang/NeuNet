package src;

import javax.swing.JFrame;
import javax.swing.JPanel;

import src.NeuralNet.InputNeuron;
import src.NeuralNet.Neuron;
import src.NeuralNet.OutputNeuron;

public class Main {
    public static void main(String[] args) {
        Neuron input = new InputNeuron("Input 1");

        Neuron[] outputNeurons = new Neuron[]{
            new OutputNeuron("Output 1"),
            new OutputNeuron("Output 2")
        };

        Network network = new Network(new Neuron[] {input}, 1, 3, 4, outputNeurons, 1, 1);

        double rand = Math.random();
        input.addInput(rand);
        double[] output = network.getOutput();

        System.out.println("Input: " + rand);
        System.out.println("Output 1: " + output[0]);
        System.out.println("Output 2: " + output[1]);

        input.reset();
        input.addInput(0.5);
        output[0] = network.getOutput()[0];
        output[1] = network.getOutput()[1];
        System.out.println("Input: " + 0.5);
        System.out.println("Output 1: " + output[0]);
        System.out.println("Output 2: " + output[1]);

        JFrame frame = new JFrame("Neural Network");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JPanel panel = Renderer.renderNetwork(network, 800, 600, 50, 20);
        frame.add(panel);
        frame.setBackground(Renderer.BACKGROUND_COLOR);
        frame.setVisible(true);
        frame.repaint();
    }
}
