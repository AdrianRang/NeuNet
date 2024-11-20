package src;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import src.NeuralNet.InputNeuron;
import src.NeuralNet.Neuron;
import src.NeuralNet.OutputNeuron;

public class Main {
    static final String BOLD = "\u001B[1m";
    static final String BLACK = "\u001B[30m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String BLUE = "\u001B[34m";
    static final String PURPLE = "\u001B[35m";
    static final String CYAN = "\u001B[36m";
    static final String WHITE = "\u001B[37m";
    static final String RESET = "\033[0m";


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
    
    static int FRAME_COUNT = 2000;
    static final int AGENTS = 200;
    static final int[] COMPOSITION = new int[]{ // How much will each place reproduce https://www.desmos.com/calculator/tbmv58rlbs // Note to self think of the way you are going to do it BEFORE spending an hour learning n-anian sumation in desmos
        80, // 1st place, 1 is equal
        44, // 2nd place, 1 is equal
        30,  // 3rd place, 1 is equal
        26,  // So on
        20,
    }; // The others die
    static final int GENERATIONS = 1000;


    private static Neuron xPositionNeuron = new InputNeuron("x Position");
    private static Neuron xSpeedNeuron = new InputNeuron("x Speed");
    private static Neuron pendulumAngleNeuron = new InputNeuron("Pendulum Angle");
    private static Neuron pendulumSpeedNeuron = new InputNeuron("Pendulum Speed");

    
    public static final Network.Chances CHANCES = new Network.Chances(
        new Network.Chances.ConnectorChances(0.1, 0.12, 0.8, 0.8, new double[] {-1, 1}),
        new Network.Chances.HiddenNeuronChances(0.1, 0.05, 0.8, new double[] {-1, 1})
    );

    @SuppressWarnings("unused")
    private static final Network.Chances CHANCES_ZERO = new Network.Chances(
        new Network.Chances.ConnectorChances(0, 0, 0, 0, new double[] {-1, 1}),
        new Network.Chances.HiddenNeuronChances(0, 0.3, 0, new double[] {-1, 1})
    );
    
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
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

        //! DEBUG
        // Network netw = new Network(inputs, 1, 6, 4, outputNeurons, 1, 1);
        // runGame(netw, true);
        // for(int i = 0; i < 5; i++) {
        //     Network cpy = new Network(netw, CHANCES_ZERO);

        //     runGame(cpy, true);
        // }

        // // runGame(net, true);

        int bestScore = 0;

        for (int i = 0; i < GENERATIONS; i++) {
            long stTime = System.currentTimeMillis();
            for(Network net : startingNets) {
                net.resetAll();
            }
            ArrayList<Network> networks = newGame(startingNets);
            
            results.write("-- GEN --\n");
            for (Network network : networks) {
                // System.out.println(network.score);
                results.write(Double.toString(network.score) + "\n");
            }

            bestScore = (int)startingNets.get(0).score;

            startingNets.clear();

            for(int j = 0 ; j < 5; j++) {
                startingNets.add(new Network(networks.get(j)));
                for(int ii = 1; ii < COMPOSITION[j]; ii++) {
                    startingNets.add(new Network(networks.get(j), CHANCES));
                }
            }

            long elapsedTime = System.currentTimeMillis() - stTime;
            int progress = (int) ((i / (double) GENERATIONS) * 100);
            String progressBar = String.format("[%s%s" + RESET +"] %d%%", (CYAN + BOLD + "=").repeat(progress / 2), " ".repeat(50 - progress / 2), progress);
            long estimatedTime = elapsedTime * (GENERATIONS - i);
            String t = "";

            String col = RED;
            if (estimatedTime < 1800000) col = YELLOW;
            if (estimatedTime < 300000) col = GREEN;
            long hours = estimatedTime / 3600000;
            estimatedTime %= 3600000;
            long minutes = estimatedTime / 60000;
            estimatedTime %= 60000;
            long seconds = estimatedTime / 1000;
            if (hours > 0) {
                t += String.format("%02d hours ", hours);
            }
            if (minutes > 0) {
                t += String.format("%02d min ", minutes);
            }
            if (seconds > 0) {
                t += String.format("%02d sec ", seconds);
            }
            System.out.print("\r" + progressBar + RESET + " : " + i + "/" + GENERATIONS + " | Estimated time remaining: " + col + t + RESET);
        }

        System.out.println();

        String time = "";

        long elapsedTime = System.currentTimeMillis() - startTime;
        long hours = elapsedTime / 3600000;
        elapsedTime %= 3600000;
        long minutes = elapsedTime / 60000;
        elapsedTime %= 60000;
        long seconds = elapsedTime / 1000;
        long milliseconds = elapsedTime % 1000;

        String timePerGen = elapsedTime/GENERATIONS + " ms";

        if (hours > 0) {
            time += String.format("%02d hours ", hours);
        }
        if (minutes > 0) {
            time += String.format("%02d min ", minutes);
        }
        if (seconds > 0) {
            time += String.format("%02d sec ", seconds);
        }
        if (milliseconds > 0) {
            time += String.format("%03d ms", milliseconds);
        }

        // System.out.println(
        //     GREEN + BOLD + 
        //     "╔════════════════════════════════════════════╗\n" +
        //     "║      "+RESET+BOLD+"        Training Succes      "+GREEN+"         ║\n" +
        //     "║"+RESET+"         Trained for " + RED + BOLD + GENERATIONS + RESET + " Generations       "+GREEN+BOLD+"║\n" +
        //     "║"+RESET+"   Each Generation Consisted of " +RED+BOLD+ AGENTS +RESET+ " Agents  "+GREEN+BOLD+"║\n" +
        //     "║" + RESET + "          Trained for " + RED + BOLD + FRAME_COUNT + RESET + " frames           "+GREEN+BOLD+"║\n" +
        //     "║"+RESET+" Best Agent achieved a score of " + RED + BOLD + bestScore + RESET + " Points "+GREEN+BOLD+"║\n" +
        //     "║"+RESET+" Took " + time + " to train which is on average " + timePerGen + GREEN+BOLD+" per generation║\n" +
        //     "╚════════════════════════════════════════════╝" + RESET
        // );

        Network rand = new Network(inputs, 1, 6, 4, outputNeurons, 1, 1);
        double randScore = runGame(rand, false);
        String[] lines = {
            "Training Success",
            "Trained for " + RED + BOLD + GENERATIONS + RESET + " Generations",
            "Each Generation Consisted of " + RED + BOLD + AGENTS + RESET + " Agents",
            "Trained for " + RED + BOLD + FRAME_COUNT + RESET + " frames",
            "Best Agent achieved a score of " + RED + BOLD + bestScore + RESET + " Points",
            String.format("That is"+BOLD+" %.2f%%"+RESET+" better than a random Network", ((bestScore / randScore) * 100)),
            "Took " + time + " to train which is on average " + timePerGen + " per generation"
        };
        


        printBox(lines);

        FRAME_COUNT = 5000;
        startingNets.get(0).outputAsJSON();
        runGame(startingNets.get(0), true);

        results.close();
    }

    public static void printBox(String[] lines) {
        int maxLength = 0;
        for (String line : lines) {
            if (line.length() > maxLength) {
                maxLength = line.length();
            }
        }

        String border = GREEN + BOLD + "╔" + "═".repeat(maxLength + 2) + "╗" + RESET;
        System.out.println(border);

        for (String line : lines) {
            String strippedLine = line.replaceAll("\u001B\\[[;\\d]*m", ""); // Remove ANSI escape codes
            boolean odd = (maxLength - strippedLine.length() % 2) == 0;
            int padding = (maxLength - strippedLine.length()) / 2;
            System.out.println(GREEN + BOLD + "║ " + " ".repeat(padding) + RESET + line + " ".repeat(padding + (odd ? 5 : 0)) + BOLD + GREEN + " ║" + RESET);
        }

        String bottomBorder = GREEN + BOLD + "╚" + "═".repeat(maxLength + 2) + "╝" + RESET;
        System.out.println(bottomBorder);
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
                // System.out.println("Score " + thisScore);
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
        double distanceToCenter = Math.abs(xPosition - 400);
        return a * Math.abs(((b * pendulumAngle) % (Math.PI * 2)) - Math.PI) + M - distanceToCenter/500 + 0.2; // https://www.desmos.com/calculator/dc1lqebg9n
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
