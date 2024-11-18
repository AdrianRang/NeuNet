package src;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
    
    static final int FRAME_COUNT = 2000;
    static final int AGENTS = 150;
    static final int[] COMPOSITION = new int[]{ // How much will each place reproduce https://www.desmos.com/calculator/tbmv58rlbs // Note to self think of the way you are going to do it BEFORE spending an hour learning n-anian sumation in desmos
        40, // 1st place, 1 is equal
        22, // 2nd place, 1 is equal
        16,  // 3rd place, 1 is equal
        12,  // So on
        10,
    }; // The others die
    static final int GENERATIONS = 2000;


    private static Neuron xPositionNeuron = new InputNeuron("x Position");
    private static Neuron xSpeedNeuron = new InputNeuron("x Speed");
    private static Neuron pendulumAngleNeuron = new InputNeuron("Pendulum Angle");
    private static Neuron pendulumSpeedNeuron = new InputNeuron("Pendulum Speed");

    
    public static final Network.Chances CHANCES = new Network.Chances(
        new Network.Chances.ConnectorChances(0.1, 0.08, 0.3, 0.8, new double[] {-1, 1}),
        new Network.Chances.HiddenNeuronChances(0.01, 0.01, 0.8, new double[] {-1, 1})
    );
    
    public static void main(String[] args) throws IOException {
        new File("out/out.txt");
        FileWriter results = new FileWriter("out/out.txt");

        ArrayList<Network> startingNets = new ArrayList<>();

        Neuron[] inputs = new Neuron[] {
            xPositionNeuron,
            xSpeedNeuron,
            pendulumAngleNeuron,
            pendulumSpeedNeuron
        };

        Neuron xSpeedOutput = new OutputNeuron("x Speed");

        Neuron[] outputNeurons = new Neuron[]{
            xSpeedOutput
        };

        for(int i = 0; i < AGENTS; i++) {
            startingNets.add(new Network(inputs, 1, 6, 4, outputNeurons, 1, 1));
        }

        // Network net = new Network(inputs, 1, 6, 4, outputNeurons, 1, 1);
        // Network cpy = new Network(net, CHANCES);

        // runGame(net, true);
        // runGame(cpy, true);

        for (int i = 0; i < GENERATIONS; i++) {
            ArrayList<Network> networks = newGame(startingNets);
            
            results.write("-- GEN --\n");
            for (Network network : networks) {
                // System.out.println(network.score);
                results.write(Double.toString(network.score) + "\n");
            }

            startingNets.clear();

            for(int j = 0 ; j < 5; j++) {
                startingNets.add(new Network(networks.get(j)));
                for(int ii = 1; ii < COMPOSITION[j]; ii++) {
                    startingNets.add(new Network(networks.get(j), CHANCES));
                }
            }

            System.err.println("Trained generation " + i);
        }

        runGame(startingNets.get(0), true);

        results.close();
    }
    
    public static ArrayList<Network> newGame(ArrayList<Network> nets) {
        ArrayList<Network> networks = new ArrayList<>();
        for(Network net : nets) {
            net.score = runGame(net, false);
            networks.add(net);
        }

        networks.sort((a, b) -> Double.compare(b.score, a.score));

        // runGame(networks.get(0), true);

        // for(Network net : networks) {
        //     System.out.println(net.score);
        // }

        
        return networks;
    }



    static double runGame(Network network, boolean render) {
        xPosition = 250;
        xSpeed = 0;
        pendulumAngle = 0;
        pendulumSpeed = 0;
        lastFrameTime = 0;
        lastFrameX = 250;
        force = 0;
        acceleration = 0;
        angleAcceleration = 0;
        scalingFactor = 0.01;

        if (render) {
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
            for (int i = 0; i < FRAME_COUNT; i++) {
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
            return score;
        } else {
            double score = 0;

            for(int i = 0; i < FRAME_COUNT; i++) {
                xPositionNeuron.addInput(xPosition);
                xSpeedNeuron.addInput(xSpeed);
                pendulumAngleNeuron.addInput(pendulumAngle);
                pendulumSpeedNeuron.addInput(pendulumSpeed);
                double thisScore = gameStep(network.getOutput()[0]);
                // System.out.println("Score " + thisScore);
                score += thisScore;
            }

            // System.out.println(score);
            return score;
        }
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
        double distanceToCenter = Math.abs(xPosition - 400);
        return a * Math.abs(((b * pendulumAngle) % (Math.PI * 2)) - Math.PI) + M - distanceToCenter/500 + 0.2; // https://www.desmos.com/calculator/dc1lqebg9n
    }
}
