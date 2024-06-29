import java.awt.image.BufferedImage;
import java.awt.Color;

public class Renderer {
    public BufferedImage renderNetwork(Network network, int width, int height) {
        return null;
    }

    public BufferedImage renderGame(double pivot, double angle, double min, double max, int length, int width, int height) {
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
        return null;
    }
}
