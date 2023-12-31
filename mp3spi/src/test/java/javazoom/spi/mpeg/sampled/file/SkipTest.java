/*
 *   SkipTest - JavaZOOM : http://www.javazoom.net
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

import static javazoom.spi.mpeg.sampled.file.PlayerTest.volume;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Skip bytes before playing.
 */
class SkipTest {
    private static Logger logger = Logger.getLogger(SkipTest.class.getName());
    private String filename = null;
    private URL fileurl = null;
    private Properties props = null;

    @BeforeEach
    void setUp() throws Exception {
        props = new Properties();
        InputStream pin = getClass().getClassLoader().getResourceAsStream("test.mp3.properties");
        props.load(pin);
        fileurl = getClass().getClassLoader().getResource("test.mp3");
        filename = Objects.requireNonNull(fileurl).getFile();

    }

    @Test
    void testSkipFile() throws Exception {
        logger.info("-> Filename : " + filename + " <-");
        File file = new File(filename);
        AudioInputStream in = AudioSystem.getAudioInputStream(file);
        AudioInputStream din = null;
        AudioFormat baseFormat = in.getFormat();
        logger.info("Source Format : " + baseFormat.toString());
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                    baseFormat.getSampleRate(),
                                                    16,
                                                    baseFormat.getChannels(),
                                                    baseFormat.getChannels() * 2,
                                                    baseFormat.getSampleRate(),
                                                    false);
        logger.info("Target Format : " + decodedFormat.toString());
        din = AudioSystem.getAudioInputStream(decodedFormat, in);
        long toSkip = file.length() * 19 / 20;
        long skipped = skip(din, toSkip);
        logger.info("Skip : " + skipped + "/" + toSkip + " (Total=" + file.length() + ")");
        logger.info("Start playing");
        rawplay(decodedFormat, din);
        in.close();
        logger.info("Played");
        assertTrue(true, "testSkip : OK");
    }

    @Test
    void testSkipUrl() throws Exception {
        logger.info("-> URL : " + fileurl + " <-");
        AudioInputStream in = AudioSystem.getAudioInputStream(fileurl);
        AudioInputStream din = null;
        AudioFormat baseFormat = in.getFormat();
        logger.info("Source Format : " + baseFormat.toString());
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                                    baseFormat.getSampleRate(),
                                                    16,
                                                    baseFormat.getChannels(),
                                                    baseFormat.getChannels() * 2,
                                                    baseFormat.getSampleRate(),
                                                    false);
        logger.info("Target Format : " + decodedFormat.toString());
        din = AudioSystem.getAudioInputStream(decodedFormat, in);
        long toSkip = in.available() * 19L / 20;
        long skipped = skip(din, toSkip);
        logger.info("Skip : " + skipped + "/" + toSkip + " (Total=" + in.available() + ")");
        logger.info("Start playing");
        rawplay(decodedFormat, din);
        in.close();
        logger.info("Played");
        assertTrue(true, "testSkip : OK");
    }

    private long skip(AudioInputStream in, long bytes) throws IOException {
        long SKIP_INACCURACY_SIZE = 1200;
        long totalSkipped = 0;
        long skipped = 0;
        while (totalSkipped < (bytes - SKIP_INACCURACY_SIZE)) {
            skipped = in.skip(bytes - totalSkipped);
            if (skipped == 0)
                break;
            totalSkipped = totalSkipped + skipped;
        }
        return totalSkipped;
    }

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
        SourceDataLine res = null;
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
            while (nBytesRead != -1) {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1)
                    line.write(data, 0, nBytesRead);
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }
}
