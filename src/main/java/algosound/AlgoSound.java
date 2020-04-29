package algosound;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.function.Function;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Companion source code for the NL-JUG Java Magazine #3 2020 article 'Fun met Functies: Geluid'.
 * 
 * @author E.Hooijmeijer
 */
public class AlgoSound {

    private static final int ONE_SECOND_IN_MS = 1000;

    /**
     * Plays the sounds from the article. 
     * Please keep the volume modest to protect your ears and audio system.
     * Substitute play with show to put the waveform on screen.
     */
    public static void main(String[] args) throws LineUnavailableException {
        play(8000, 1000, (t) -> (int) (127.0 * Math.sin(t / 4.0)));
        play(8000, 1000, (t) -> {
            double result = 0;
            for (int h = 1; h <= 9; h += 2) {
                result += 1.0 / h * Math.sin(t / 4.0 * h);
            }
            return (int) (127 * result);
        });
        play(8000, 1000, (t) -> (t % 32 < 16 ? 127 : -128));
        play(8000, 1000, (t) -> t ^ t << 1);
        play(8000, 5000, (t) -> (t & t % 255));
        play(8000, 4000, (t) -> t * 12 & t / 36);
    }

    /**
     * plays the return value of a function as sound using the sample number as input.
     * @param frequency the sample frequency in hertz.
     * @param durationInMs the duration of the sound in milliseconds.
     * @param fun the function.
     * @throws LineUnavailableException
     */
    public static void play(int frequency, int durationInMs, Function<Integer, Integer> fun) throws LineUnavailableException {
        AudioFormat af = new AudioFormat((float) frequency, 8, 1, true, false);
        try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
            sdl.open();
            sdl.start();
            byte[] buf = new byte[1];
            for (int t = 0; t < durationInMs * frequency / ONE_SECOND_IN_MS; t++) {
                buf[0] = (byte) (fun.apply(t) & 0xFF);
                sdl.write(buf, 0, 1);
            }
            sdl.drain();
            sdl.stop();
        }
    }

    /**
     * visualizes the output of the function on screen. 
     * @param frequency the sample frequency in hertz.
     * @param durationInMs the duration of the sound in milliseconds.
     * @param fun the function.
     */
    public static void show(int frequency, int durationInMs, Function<Integer, Integer> fun) {
        JFrame frame = new JFrame();
        int frameWidth = (int) (durationInMs * (float) frequency / ONE_SECOND_IN_MS);
        JPanel owner = new JPanel() {

            @Override
            public void paint(Graphics g) {
                drawMeasures(frequency, g);
                drawSamples(fun, (Graphics2D) g);
            }

            private void drawSamples(Function<Integer, Integer> fun, Graphics2D g) {
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(2.0f));
                int py = 0;
                byte out = 0;
                for (int t = 0; t < getWidth(); t++) {
                    try {
                        out = (byte) (fun.apply(t) & 0xFF);
                    } catch (ArithmeticException ex) {
                        // Ignore
                    }
                    g.drawLine(t, py, t, -out);
                    py = -out;
                }
            }

            private void drawMeasures(int frequency, Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.translate(0, getHeight() / 2);
                g.setColor(Color.GRAY);
                g.drawLine(0, 0, getWidth(), 0);
                for (int t = 0; t < getWidth(); t += frequency / 100) {
                    g.drawLine(t, 128, t, -128);
                }
            }
        };
        owner.setPreferredSize(new Dimension(frameWidth, 512));

        JPanel outer = new JPanel();
        outer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        outer.setBackground(Color.WHITE);

        JScrollPane sp = new JScrollPane(owner);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setPreferredSize(new Dimension(720, 540));

        outer.add(sp, BorderLayout.CENTER);

        frame.setContentPane(outer);
        frame.setSize(800, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
