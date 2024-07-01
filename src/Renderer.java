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
    static final Color POS_CONNECTION_COLOR = Color.GREEN;
    static final Color NEG_CONNECTION_COLOR = Color.RED;
    static final Color BACKGROUND_COLOR = Color.BLACK;

    public static JPanel renderNetwork(Network network, int width, int height, int padding, int neuronSize) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        // g.setColor(Color.RED);
        // g.drawLine(0, (int)(height/2), width, (int)(height/2));
        // g.drawLine((int)(width/2), 0, (int)(width/2), height);
        g.setColor(BACKGROUND_COLOR);
        //g.fillRect(0, 0, width, height);
        g.setColor(NEURON_COLOR);
        int numNeuronsIn = network.inputNeurons.length;
        int numNeuronsOut = network.outputNeurons.length;
        int numHiddenLayers = network.hiddenLayers.size();

        int spaceBetweenLayers = (width - 2 * padding) / (numHiddenLayers + 2);
        int spaceBetweenNeuronsIn = (height - 2 * padding) / (numNeuronsIn);
        int[] boundingBoxIn = new int[] {neuronSize, neuronSize + (numNeuronsIn - 1) * spaceBetweenNeuronsIn};
        
        for (int i = 0; i < numNeuronsIn; i++) {
            g.fillOval(padding, padding + i * spaceBetweenNeuronsIn + (height - boundingBoxIn[1]) / 2 - (int)(neuronSize * 2.5), neuronSize, neuronSize);
            g.setFont(Font.getFont("Arial"));
            network.inputNeurons[i].setPos(padding, padding + i * spaceBetweenNeuronsIn + (height - boundingBoxIn[1]) / 2 - (int)(neuronSize * 2.5));
            InputNeuron in = (InputNeuron)(network.inputNeurons[i]);
            g.drawString(in.name, padding + neuronSize + 5, padding + i * spaceBetweenNeuronsIn + (height - boundingBoxIn[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize);
        }

        for (int i = 0; i < numHiddenLayers; i++) {
            int numNeurons = network.hiddenLayers.get(i).size();
            int spaceBetweenNeurons = (height - 2 * padding) / (numNeurons + 1);
            int[] boundingBox = new int[] {neuronSize, neuronSize + (numNeurons - 1) * spaceBetweenNeurons};

            for (int j = 0; j < numNeurons; j++) {
                network.hiddenLayers.get(i).get(j).setPos(padding + i * spaceBetweenLayers + (width - 2 * padding - (spaceBetweenLayers * numHiddenLayers))/2, padding + j * spaceBetweenNeurons + (height - boundingBox[1]) / 2 - (int)(neuronSize * 2.5));
                g.fillOval((padding + i * spaceBetweenLayers + (width - 2 * padding - (spaceBetweenLayers * numHiddenLayers))/2), padding + j * spaceBetweenNeurons + (height - boundingBox[1]) / 2 - (int)(neuronSize * 2.5), neuronSize, neuronSize);
                g.drawString("output: " + String.valueOf((double)((int)(network.hiddenLayers.get(i).get(j).getOutput() * 1000))/1000), (padding + i * spaceBetweenLayers + (width - 2 * padding - (spaceBetweenLayers * numHiddenLayers))/2) + neuronSize + 5, padding + j * spaceBetweenNeurons + (height - boundingBox[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize);
                g.drawString("bias: " + String.valueOf((double)((int)(((HiddenNeuron)(network.hiddenLayers.get(i).get(j))).getBias() * 1000))/1000), (padding + i * spaceBetweenLayers + (width - 2 * padding - (spaceBetweenLayers * numHiddenLayers))/2) + neuronSize + 5, padding + j * spaceBetweenNeurons + (height - boundingBox[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize + 15);
            }
        }

        int spaceBetweenNeuronsOut = (height - 2 * padding) / (numNeuronsOut);
        int[] boundingBoxOut = new int[] {neuronSize, neuronSize + (numNeuronsOut - 1) * spaceBetweenNeuronsOut};
        for (int i = 0; i < numNeuronsOut; i++) {
            g.fillOval(width - padding - neuronSize, padding + i * spaceBetweenNeuronsOut + (height - boundingBoxOut[1]) / 2 - (int)(neuronSize * 2.5), neuronSize, neuronSize);
            g.setFont(Font.getFont("Arial"));
            network.outputNeurons[i].setPos(width - padding - neuronSize, padding + i * spaceBetweenNeuronsOut + (height - boundingBoxOut[1]) / 2 - (int)(neuronSize * 2.5));
            OutputNeuron out = (OutputNeuron)(network.outputNeurons[i]);
            g.drawString(String.valueOf((double)(int)(out.getOutput() * 100000) / 100000), width - padding - neuronSize - 5 - String.valueOf((double)(int)(out.getOutput() * 100000) / 100000).length() * 5, padding + i * spaceBetweenNeuronsOut + (height - boundingBoxOut[1]) / 2 - (int)(neuronSize * 2.5) - 5);
            g.drawString(out.name, width - padding - neuronSize - 5 - out.name.length() * 5, padding + i * spaceBetweenNeuronsOut + (height - boundingBoxOut[1]) / 2 - (int)(neuronSize * 2.5) + neuronSize + 15);
        }

        for (Connector con : network.connectors) {
            Graphics2D g2 = image.createGraphics();
            g2.setColor(con.getWeight() < 0 ? NEG_CONNECTION_COLOR : POS_CONNECTION_COLOR);
            g2.setStroke(new BasicStroke((int)Math.abs((con.getWeight() * 10))));
            g2.setXORMode(BACKGROUND_COLOR);
            
            g2.drawLine(con.getFrom().getX() + (int)(neuronSize/2), con.getFrom().getY() + (int)(neuronSize/2), con.getTo().getX() + (int)(neuronSize/2), con.getTo().getY() + (int)(neuronSize/2));
        }


        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };


        return panel;
    }

    public static BufferedImage renderGame(double pivot, double angle, double min, double max, int length, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().setColor(Color.BLACK);
        image.getGraphics().drawRect(0, 0, width, height);
        image.getGraphics().setColor(Color.DARK_GRAY);
        image.getGraphics().drawLine((int)min, 0, (int)max, 0);
        image.getGraphics().setColor(Color.WHITE);
        image.getGraphics().drawOval((int)pivot, 0, 5, 5);
        image.getGraphics().setColor(Color.LIGHT_GRAY);
        image.getGraphics().drawLine((int)pivot, 0, (int)(Math.cos(angle) * length + pivot), (int)(Math.sin(angle) * length));
        image.getGraphics().drawOval((int)(Math.cos(angle) * length + pivot), (int)(Math.sin(angle) * length), 25, 25);
        return image;
    }
}
