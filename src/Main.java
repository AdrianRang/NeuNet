package src;

import javax.swing.JFrame;
import javax.swing.JPanel;

import src.NeuralNet.InputNeuron;
import src.NeuralNet.Neuron;
import src.NeuralNet.OutputNeuron;

public class Main {
    static double xPosition = 250;
    static double xSpeed = 0;
    static double pendulumAngle = Math.PI/4; // Relative to vertical
    static double pendulumSpeed = 0;
    static double lastFrameTime = 0;
    static double force = 0;
    static double acceleration = 0;
    static double angleAcceleration = 0;
    
    static final double GRAVITY = 0.001;
    static final double MASS = 1;
    static final double LENGTH = 60;
    static final double FRICTION = 0;

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

        // Network network = new Network(inputs, 3, 3, 5, outputNeurons, 1, 1);

        
        JFrame frame = new JFrame("Pendulum");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JPanel panel = Renderer.renderGame(xPosition, pendulumAngle, 10, 790, LENGTH, 800, 600);
        frame.add(panel);
        frame.setBackground(Renderer.BACKGROUND_COLOR);
        frame.setVisible(true);
        frame.repaint();
        lastFrameTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            System.out.println("Frame " + i);
            System.out.println("angle: " + pendulumAngle);
            frame.getContentPane().removeAll();
            frame.add(Renderer.renderGame(xPosition, pendulumAngle, 10, 790, LENGTH, 800, 600));
            frame.repaint();
            frame.revalidate();
            gameStep();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // JFrame frame = new JFrame("Neural Network");
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(800, 600);
        // JPanel panel = Renderer.renderNetwork(network, 800, 600, 50, 20);
        // frame.add(panel);
        // frame.setBackground(Renderer.BACKGROUND_COLOR);
        // frame.setVisible(true);
        // frame.repaint();
        System.out.println("Hello, World!");
    }

    static void gameStep() {
        // double deltaTime = System.currentTimeMillis() - lastFrameTime;

        force = -GRAVITY * Math.sin(pendulumAngle);
        force -= FRICTION * pendulumSpeed;
        angleAcceleration = force / MASS;
        pendulumSpeed += angleAcceleration;
        pendulumAngle += pendulumSpeed;


        lastFrameTime = System.currentTimeMillis();
    }
}
