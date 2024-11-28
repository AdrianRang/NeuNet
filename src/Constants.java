package src;
public class Constants {
    static final int FRAME_COUNT = 2000;
    static final int AGENTS = 400;
    static final int[] COMPOSITION = new int[]{ // How much will each place reproduce https://www.desmos.com/calculator/tbmv58rlbs // Note to self think of the way you are going to do it BEFORE spending an hour learning n-anian sumation in desmos
        160, // 1st place, 1 is equal
        88, // 2nd place, 1 is equal
        60,  // 3rd place, 1 is equal
        52,  // So on
        40,
    }; // The others die
    static final int GENERATIONS = 1000;

    public static final Network.Chances CHANCES = new Network.Chances(
        new Network.Chances.ConnectorChances(0.1, 0.1, 0.5, 0.5, new double[] {-1, 1}),
        new Network.Chances.HiddenNeuronChances(0.1, 0.1, 0.5, new double[] {-1, 1}),
        new Network.Chances.LayerChances(0.05, 0)
    );
}
