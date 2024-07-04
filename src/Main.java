package src;

import javax.swing.JFrame;
import javax.swing.JPanel;

import src.NeuralNet.InputNeuron;
import src.NeuralNet.Neuron;
import src.NeuralNet.OutputNeuron;

public class Main {
    public static void main(String[] args) {
        Neuron[] inputs = new Neuron[] {
            new InputNeuron("X Pos"),
            new InputNeuron("X Speed"),
            new InputNeuron("Pendulum Angle"),
            new InputNeuron("Pendulum Speed")
        };

        Neuron[] outputNeurons = new Neuron[]{
            new OutputNeuron("x Speed")
        };

        Network network = new Network(inputs, 3, 3, 5, outputNeurons, 1, 1);

        

        JFrame frame = new JFrame("Neural Network");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JPanel panel = Renderer.renderNetwork(network, 800, 600, 50, 20);
        frame.add(panel);
        frame.setBackground(Renderer.BACKGROUND_COLOR);
        frame.setVisible(true);
        frame.repaint();
        System.out.println("Hello, World!");
    }
}
