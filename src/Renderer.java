package src;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import src.NeuralNet.Connector;
import src.NeuralNet.HiddenNeuron;
import src.NeuralNet.InputNeuron;
import src.NeuralNet.OutputNeuron;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class Renderer {
    static final Color NEURON_COLOR = Color.WHITE;
    static final Color POS_NEURON_COLOR = new Color(0x15803d);
    static final Color NEG_NEURON_COLOR = new Color(0x450a0a);
    static final Color POS_CONNECTION_COLOR = new Color(0x4d7c0f);
    static final Color NEG_CONNECTION_COLOR = Color.RED;
    static final Color BACKGROUND_COLOR = Color.BLACK;

    static final double MAX_EXPECTED_OUTPUT = 1;
    static final double MIN_EXPECTED_OUTPUT = -1;

    public static JPanel renderNetwork(Network network, int width, int height, int padding, int neuronSize) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        // g.setColor(Color.RED);
        // g.drawLine(0, (int)(height/2), width, (int)(height/2));
        // g.drawLine((int)(width/2), 0, (int)(width/2), height);
        int numNeuronsIn = network.inputNeurons.length;
        int numNeuronsOut = network.outputNeurons.length;
        int numHiddenLayers = network.hiddenLayers.size();

        int spaceBetweenLayers = (width - 2 * padding) / (numHiddenLayers + 2);
        int spaceBetweenNeuronsIn = (height - 2 * padding) / (numNeuronsIn);
        int[] boundingBoxIn = new int[] {neuronSize, neuronSize + (numNeuronsIn - 1) * spaceBetweenNeuronsIn};
        
        for (int i = 0; i < numNeuronsIn; i++) {
            double output = network.inputNeurons[i].getOutput();
            int re, gr, bl;
            if (output >= 0) {
                re = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getRed() - NEURON_COLOR.getRed()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getRed()));
                gr = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getGreen() - NEURON_COLOR.getGreen()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getGreen()));
                bl = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getBlue() - NEURON_COLOR.getBlue()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getBlue()));
            } else {
                re = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getRed() - NEURON_COLOR.getRed()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getRed()));
                gr = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getGreen() - NEURON_COLOR.getGreen()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getGreen()));
                bl = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getBlue() - NEURON_COLOR.getBlue()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getBlue()));
            }
            
            g.setColor(new Color(re, gr, bl));
            g.fillOval(padding, padding + i * spaceBetweenNeuronsIn + (height - boundingBoxIn[1]) / 2 - (int)(neuronSize * 2.5), neuronSize, neuronSize);
            network.inputNeurons[i].setPos(padding, padding + i * spaceBetweenNeuronsIn + (height - boundingBoxIn[1]) / 2 - (int)(neuronSize * 2.5));
            InputNeuron in = (InputNeuron)(network.inputNeurons[i]);
            g.setFont(Font.getFont("Arial"));
            g.setFont(g.getFont().deriveFont(Font.BOLD).deriveFont(15.0f));
            g.setColor(Color.WHITE);
            g.drawString(in.name, padding, padding + i * spaceBetweenNeuronsIn + (height - boundingBoxIn[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize + 15);
            g.drawString("Output: " + String.valueOf((double)((int)(in.getOutput() * 1000))/1000), padding, padding + i * spaceBetweenNeuronsIn + (height - boundingBoxIn[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize + 30);
        }

        for (int i = 0; i < numHiddenLayers; i++) {
            int numNeurons = network.hiddenLayers.get(i).size();
            int spaceBetweenNeurons = (height - 2 * padding) / (numNeurons + 1);
            int[] boundingBox = new int[] {neuronSize, neuronSize + (numNeurons - 1) * spaceBetweenNeurons};

            for (int j = 0; j < numNeurons; j++) {
                network.hiddenLayers.get(i).get(j).setPos(padding + i * spaceBetweenLayers + (width - 2 * padding - (spaceBetweenLayers * numHiddenLayers))/2, padding + j * spaceBetweenNeurons + (height - boundingBox[1]) / 2 - (int)(neuronSize * 2.5));

                double output = network.hiddenLayers.get(i).get(j).getOutput();
                int re, gr, bl;
                if (output >= 0) {
                    re = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getRed() - NEURON_COLOR.getRed()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getRed()));
                    gr = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getGreen() - NEURON_COLOR.getGreen()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getGreen()));
                    bl = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getBlue() - NEURON_COLOR.getBlue()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getBlue()));
                } else {
                    re = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getRed() - NEURON_COLOR.getRed()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getRed()));
                    gr = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getGreen() - NEURON_COLOR.getGreen()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getGreen()));
                    bl = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getBlue() - NEURON_COLOR.getBlue()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getBlue()));
                }

                g.setColor(new Color(re, gr, bl));

                g.fillOval((padding + i * spaceBetweenLayers + (width - 2 * padding - (spaceBetweenLayers * numHiddenLayers))/2), padding + j * spaceBetweenNeurons + (height - boundingBox[1]) / 2 - (int)(neuronSize * 2.5), neuronSize, neuronSize);

                g.setColor(Color.WHITE);
                g.drawString("Output: " + String.valueOf((double)((int)(network.hiddenLayers.get(i).get(j).getOutput() * 1000))/1000), (padding + i * spaceBetweenLayers + (width - 2 * padding - (spaceBetweenLayers * numHiddenLayers))/2), padding + j * spaceBetweenNeurons + (height - boundingBox[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize + 15);
                g.drawString("Bias: " + String.valueOf((double)((int)(((HiddenNeuron)(network.hiddenLayers.get(i).get(j))).getBias() * 1000))/1000), (padding + i * spaceBetweenLayers + (width - 2 * padding - (spaceBetweenLayers * numHiddenLayers))/2), padding + j * spaceBetweenNeurons + (height - boundingBox[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize + 30);
            }
        }

        int spaceBetweenNeuronsOut = (height - 2 * padding) / (numNeuronsOut);
        int[] boundingBoxOut = new int[] {neuronSize, neuronSize + (numNeuronsOut - 1) * spaceBetweenNeuronsOut};
        for (int i = 0; i < numNeuronsOut; i++) {
            double output = network.outputNeurons[i].getOutput();
            int re, gr, bl;
            if (output >= 0) {
                re = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getRed() - NEURON_COLOR.getRed()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getRed()));
                gr = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getGreen() - NEURON_COLOR.getGreen()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getGreen()));
                bl = (int) Math.min(255, Math.abs(output * (POS_NEURON_COLOR.getBlue() - NEURON_COLOR.getBlue()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getBlue()));
            } else {
                re = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getRed() - NEURON_COLOR.getRed()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getRed()));
                gr = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getGreen() - NEURON_COLOR.getGreen()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getGreen()));
                bl = (int) Math.min(255, Math.abs(output * Math.abs(NEG_NEURON_COLOR.getBlue() - NEURON_COLOR.getBlue()) / (MAX_EXPECTED_OUTPUT/2 - MIN_EXPECTED_OUTPUT/2) + NEURON_COLOR.getBlue()));
            }
            g.setColor(new Color(re, gr, bl));

            g.fillOval(width - padding - neuronSize, padding + i * spaceBetweenNeuronsOut + (height - boundingBoxOut[1]) / 2 - (int)(neuronSize * 2.5), neuronSize, neuronSize);

            network.outputNeurons[i].setPos(width - padding - neuronSize, padding + i * spaceBetweenNeuronsOut + (height - boundingBoxOut[1]) / 2 - (int)(neuronSize * 2.5));

            OutputNeuron out = (OutputNeuron)(network.outputNeurons[i]);
            g.setColor(Color.WHITE);

            g.drawString(String.valueOf((double)(int)(out.getOutput() * 100000) / 100000), width - padding - neuronSize - 5 - String.valueOf((double)(int)(out.getOutput() * 100000) / 100000).length() * 5, padding + i * spaceBetweenNeuronsOut + (height - boundingBoxOut[1]) / 2 - (int)(neuronSize * 2.5) - 5);
            g.drawString(out.name, width - padding - neuronSize - 5 - out.name.length() * 5, padding + i * spaceBetweenNeuronsOut + (height - boundingBoxOut[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize + 15);
        }

        BufferedImage img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img2.createGraphics();
        for (Connector con : network.connectors) {
            g2.setColor(con.getWeight() < 0 ? NEG_CONNECTION_COLOR : POS_CONNECTION_COLOR);
            g2.setStroke(new BasicStroke((int)Math.abs((con.getWeight() * 10))));
            
            g2.drawLine(con.getFrom().getX() + (int)(neuronSize/2), con.getFrom().getY() + (int)(neuronSize/2), con.getTo().getX() + (int)(neuronSize/2), con.getTo().getY() + (int)(neuronSize/2));
        }


        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                g2.drawImage(image, 0, 0, null);
                g.drawImage(img2, 0, 0, null);
            }
        };


        return panel;
    }

    public static JPanel renderGame(double pivot, double angle, double min, double max, double length, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().setColor(Color.DARK_GRAY);
        image.getGraphics().drawLine((int)min, (int)(height/2), (int)max, (int)(height/2));
        image.getGraphics().setColor(Color.WHITE);
        image.getGraphics().drawOval((int)(pivot + 2.5), (int)(height/2 - 2.5), 5, 5);
        double angleRV = Math.PI/2 - angle;

        image.getGraphics().setColor(Color.LIGHT_GRAY);
        image.getGraphics().drawLine((int)(pivot + 2.5) + (int)(2.5), (int)(height/2), (int)(Math.cos(angleRV) * length + pivot + 2.5), (int)(Math.sin(angleRV) * length) + (int)(height/2));
        image.getGraphics().drawOval((int)(Math.cos(angleRV) * length + pivot - 25/2 + 2.5), (int)(Math.sin(angleRV) * length + height/2 - 25/2 + 2.5), 25, 25);

        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        return panel;
    }

    public static JPanel renderGame(double pivot, double angle, double min, double max, double length, int width, int height, double xSpeed) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().setColor(Color.WHITE);
        image.getGraphics().setFont(Font.getFont("Arial"));
        image.getGraphics().setFont(image.getGraphics().getFont().deriveFont(Font.BOLD).deriveFont(15.0f));
        image.getGraphics().drawString("Input: " + xSpeed, width/2, 50);
        image.getGraphics().setColor(Color.DARK_GRAY);
        image.getGraphics().drawLine((int)min, (int)(height/2), (int)max, (int)(height/2));
        image.getGraphics().setColor(Color.WHITE);
        image.getGraphics().drawOval((int)(pivot + 2.5), (int)(height/2 - 2.5), 5, 5);
        double angleRV = Math.PI/2 - angle;

        image.getGraphics().setColor(Color.LIGHT_GRAY);
        image.getGraphics().drawLine((int)(pivot + 2.5) + (int)(2.5), (int)(height/2), (int)(Math.cos(angleRV) * length + pivot + 2.5), (int)(Math.sin(angleRV) * length) + (int)(height/2));
        image.getGraphics().drawOval((int)(Math.cos(angleRV) * length + pivot - 25/2 + 2.5), (int)(Math.sin(angleRV) * length + height/2 - 25/2 + 2.5), 25, 25);

        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        return panel;
    }
}
