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
    static double scalingFactor = 0.01;
    
    static final double GRAVITY = 0.001;
    static final double MASS = 1;
    static final double LENGTH = 60;
    static final double FRICTION = 0.001;

    public static void main(String[] args) {
        Neuron xPositionNeuron = new InputNeuron("x Position");
        Neuron xSpeedNeuron = new InputNeuron("x Speed");
        Neuron pendulumAngleNeuron = new InputNeuron("Pendulum Angle");
        Neuron pendulumSpeedNeuron = new InputNeuron("Pendulum Speed");

        Neuron xSpeedOutput = new OutputNeuron("x Speed");

        Neuron[] inputs = new Neuron[] {
            xPositionNeuron,
            xSpeedNeuron,
            pendulumAngleNeuron,
            pendulumSpeedNeuron
        };

        Neuron[] outputNeurons = new Neuron[]{
            xSpeedOutput
        };

        Network network = new Network(inputs, 1, 3, 2, outputNeurons, 1, 1);

        JFrame frame = new JFrame("Pendulum");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        JPanel panel = Renderer.renderGame(xPosition, pendulumAngle, 10, 790, LENGTH, 800, 600);
        frame.add(panel);
        frame.setBackground(Renderer.BACKGROUND_COLOR);
        frame.setVisible(true);
        frame.repaint();

        JFrame nframe = new JFrame("Neural Network");

        nframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nframe.setSize(600, 600);
        nframe.setLocation(800, 0);
        nframe.setBackground(Renderer.BACKGROUND_COLOR);
        nframe.setVisible(true);
        nframe.repaint();
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
        double score = 0;
        for (int i = 0; i < 5000; i++) {
            frame.getContentPane().removeAll();
            frame.add(Renderer.renderGame(xPosition, pendulumAngle, 10, 790, LENGTH, 800, 600, acceleration));
            frame.repaint();
            frame.revalidate();

            nframe.getContentPane().removeAll();
            nframe.add(Renderer.renderNetwork(network, 600, 600, 50, 20));
            nframe.repaint();
            nframe.revalidate();

            xPositionNeuron.addInput(xPosition);
            xSpeedNeuron.addInput(xSpeed);
            pendulumAngleNeuron.addInput(pendulumAngle);
            pendulumSpeedNeuron.addInput(pendulumSpeed);
            double thisScore = gameStep(network.getOutput()[0]);
            System.out.println("Score " + thisScore);
            score += thisScore;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        
        System.out.println(score);
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

        double a = -5;
        double b = 1;
        double M = 5.4;
        return a * Math.abs(((b * pendulumAngle) % (Math.PI * 2)) - Math.PI) + M; // https://www.desmos.com/calculator/dc1lqebg9n
    }

    static double gameStep(double output) {
        // double deltaTime = System.currentTimeMillis() - lastFrameTime;
        acceleration = Math.min(0.1, Math.max(output * scalingFactor, -0.1));

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

        double a = -5;
        double b = 1;
        double M = 5.4;
        return a * Math.abs(((b * pendulumAngle) % (Math.PI * 2)) - Math.PI) + M; // https://www.desmos.com/calculator/dc1lqebg9n
    }
}
