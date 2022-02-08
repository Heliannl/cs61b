package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {

    public static void main(String[] args) {
        /* create 37 guitar strings, for concert q2we... */
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] strings = new GuitarString[keyboard.length()];
        double[] concerts = new double[keyboard.length()];
        for (int i = 0; i < keyboard.length(); i++) {
            concerts[i] = 440.0 * Math.pow(2, (i - 24.0) / 12.0);
            strings[i] = new GuitarString(concerts[i]);
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int keyIndex = keyboard.indexOf(key);
                if (keyIndex == -1) {
                    continue;
                }
                strings[keyIndex].pluck();
            }

            /* compute the superposition of samples */
            double sample = 0;
            for (int i = 0; i < keyboard.length(); i++) {
                sample += strings[i].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < keyboard.length(); i++) {
                strings[i].tic();
            }
        }
    }
}

