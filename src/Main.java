package src;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import src.NeuralNet.InputNeuron;
import src.NeuralNet.Neuron;
import src.NeuralNet.OutputNeuron;

public class Main {
    static double xPosition = 250;
    static double xSpeed = 0;
    static double pendulumAngle = 0; // Relative to vertical
    static double pendulumSpeed = 0;
    static double lastFrameTime = 0;
    static double lastFrameX = 250;
    static double force = 0;
    static double acceleration = 0;
    static double angleAcceleration = 0;
    
    static final double GRAVITY = 0.001;
    static final double MASS = 1;
    static final double LENGTH = 60;
    static final double FRICTION = 0.001;

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

        
        JFrame frame = new JFrame("Pendulum");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JPanel panel = Renderer.renderGame(xPosition, pendulumAngle, 10, 790, LENGTH, 800, 600);
        frame.add(panel);
        frame.setBackground(Renderer.BACKGROUND_COLOR);
        frame.setVisible(true);
        frame.repaint();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_LEFT) {
                    acceleration = 0;
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    acceleration = 0.01;
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    acceleration = -0.01;
                }
            }
        });
        lastFrameTime = System.currentTimeMillis();
        for (int i = 0; i < 5000; i++) {
            frame.getContentPane().removeAll();
            frame.add(Renderer.renderGame(xPosition, pendulumAngle, 10, 790, LENGTH, 800, 600));
            frame.repaint();
            frame.revalidate();
            System.out.println("Score " + gameStep());
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

    static double gameStep() {
        // double deltaTime = System.currentTimeMillis() - lastFrameTime;

        if (xPosition < 10 || xPosition > 790) {
            xSpeed = 0;
            xPosition = xPosition < 10 ? 10 : 790;
        }
        
        force = -GRAVITY * Math.sin(pendulumAngle) + -acceleration * Math.cos(pendulumAngle)/10;
        force -= FRICTION * pendulumSpeed;
        angleAcceleration = force / MASS;
        pendulumSpeed += angleAcceleration;
        pendulumAngle += pendulumSpeed;
        
        xSpeed += acceleration;
        xPosition += xSpeed;
        
        lastFrameTime = System.currentTimeMillis();
        lastFrameX = xPosition;

        return 0; //TODO: Return score https://www.desmos.com/calculator/dc1lqebg9n
    }
}
