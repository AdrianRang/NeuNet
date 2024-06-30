package src;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Graphics2D;

public class Renderer {
    static final Color NEURON_COLOR = Color.WHITE;
    static final Color CONNECTION_COLOR = Color.LIGHT_GRAY;
    static final Color BACKGROUND_COLOR = Color.BLACK;

    public static JPanel renderNetwork(Network network, int width, int height, int padding, int neuronSize) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.drawLine(0, (int)(height/2), width, (int)(height/2));
        g.setColor(Color.RED);
        g.setColor(BACKGROUND_COLOR);
        //g.fillRect(0, 0, width, height);
        g.setColor(NEURON_COLOR);
        int numNeuronsIn = network.inputNeurons.length;
        int numNeuronsOut = network.outputNeurons.length;
        int numHiddenLayers = network.hiddenLayers.size();

        int spaceBetweenLayers = (width - 2 * padding) / (numHiddenLayers + 2);
        int spaceBetweenNeuronsIn = (height - 2 * padding) / (numNeuronsIn);
        int[] boundingBoxIn = new int[] {neuronSize, neuronSize + numNeuronsIn * spaceBetweenNeuronsIn};
        
        g.drawRect(padding, padding, boundingBoxIn[0], boundingBoxIn[1]);
        for (int i = 0; i < numNeuronsIn; i++) {
            g.fillOval(padding, padding + i * spaceBetweenNeuronsIn, neuronSize, neuronSize);
        }

        for (int i = 0; i < numHiddenLayers; i++) {
            int numNeurons = network.hiddenLayers.get(i).size();
            int spaceBetweenNeurons = (height - 2 * padding) / (numNeurons + 1);
            for (int j = 0; j < numNeurons; j++) {
                g.fillOval(padding + (i + 1) * spaceBetweenLayers, padding + j * spaceBetweenNeurons, neuronSize, neuronSize);
            }
        }

        int spaceBetweenNeuronsOut = (height - 2 * padding) / (numNeuronsOut);
        for (int i = 0; i < numNeuronsOut; i++) {
            g.fillOval(width - padding - neuronSize, padding + i * spaceBetweenNeuronsOut, neuronSize, neuronSize);
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
