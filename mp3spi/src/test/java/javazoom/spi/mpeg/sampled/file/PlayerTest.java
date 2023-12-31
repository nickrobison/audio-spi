/*
 *   PlayerTest - JavaZOOM : http://www.javazoom.net
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package javazoom.spi.mpeg.sampled.file;

import javazoom.spi.PropertiesContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple player (based on MP3 SPI) unit test.
 */
public class PlayerTest {

    private String filename = null;
    private Properties props = null;
    private Logger out;
    /** play time limit in milliseconds */
    private long time;

    @BeforeEach
    protected void setUp() throws Exception {
        props = new Properties();
        InputStream pin = getClass().getClassLoader().getResourceAsStream("test.mp3.properties");
        props.load(pin);
        filename = Objects.requireNonNull(getClass().getClassLoader().getResource("test.mp3")).getFile();
        out = Logger.getLogger(PlayerTest.class.getName());
        time = System.getProperty("vavi.test", "").equals("ide") ? 3000 * 1000 : 3 * 1000;
    }

    @Test
    public void testPlay0() throws Exception {
        AudioInputStream in = AudioSystem.getAudioInputStream(new File(filename));
        AudioFormat baseFormat = in.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                    baseFormat.getSampleRate(),
                                                    16,
                                                    baseFormat.getChannels(),
                                                    baseFormat.getChannels() * 2,
                                                    baseFormat.getSampleRate(),
                                                    false);
        AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
        rawplay(decodedFormat, din);
    }

    @Test
    public void testPlay() throws Exception {
        out.info("---  Start : " + filename + "  ---");
        File file = new File(filename);
        //URL file = new URL(props.getProperty("shoutcast"));
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
        out.info("Audio Type : " + aff.getType());
        AudioInputStream in = AudioSystem.getAudioInputStream(file);
        AudioInputStream din = null;
        if (in != null) {
            AudioFormat baseFormat = in.getFormat();
            out.info("Source Format : " + baseFormat.toString());
            AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                        baseFormat.getSampleRate(),
                                                        16,
                                                        baseFormat.getChannels(),
                                                        baseFormat.getChannels() * 2,
                                                        baseFormat.getSampleRate(),
                                                        false);
            if (out != null)
                out.info("Target Format : " + decodedFormat.toString());
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            assertInstanceOf(PropertiesContainer.class, din, "PropertiesContainer");
            rawplay(decodedFormat, din);
            in.close();
            out.info("---  Stop : " + filename + "  ---");
            assertTrue(true, "testPlay : OK");
        }
    }

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }

    private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null) {
            volume(line, .1d);
            // Start
            line.start();
            int nBytesRead = 0;
//            while (!later(time).come()) {
//                nBytesRead = din.read(data, 0, data.length);
//                if (nBytesRead != -1)
//                    line.write(data, 0, nBytesRead);
//                else
//                    break;
//            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }

    /**
     * @param gain number between 0 and 1 (loudest)
     * @before {@link DataLine#open()}
     */
    public static void volume(DataLine line, double gain) {
        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log10(gain) * 20.0);
        gainControl.setValue(dB);
    }
}
