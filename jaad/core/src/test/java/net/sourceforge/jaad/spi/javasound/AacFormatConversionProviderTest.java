/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package net.sourceforge.jaad.spi.javasound;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static vavi.sound.SoundUtil.volume;
import static vavix.util.DelayedWorker.later;


/**
 * AacFormatConversionProviderTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023/05/23 umjammer initial version <br>
 */
@SuppressWarnings("DataFlowIssue")
class AacFormatConversionProviderTest {

    static final Logger logger = LoggerFactory.getLogger(AacFormatConversionProviderTest.class);

    static long time;

    static {
        System.setProperty("vavi.util.logging.VaviFormatter.extraClassMethod", "org\\.tritonus\\.share\\.TDebug#out");

        time = System.getProperty("vavi.test", "").equals("ide") ? 1000 * 1000 : 9 * 1000;
    }

    static final String mp4 = "test.m4a";
    static final String aac = "test.aac";
    static final String mid = "test.mid";
    static final String alac = "alac.m4a";
    static final String caf = "test.caf";

    @Test
    @DisplayName("unsupported exception is able to detect in 3 ways")
    public void test1() throws Exception {

        final URL testFile = getClass().getClassLoader().getResource(mid);
        Path path = Paths.get(testFile.toURI());

        assertThrows(UnsupportedAudioFileException.class, () -> {
            // don't replace with Files#newInputStream(Path)
            try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path.toFile().toPath()))) {
                new AACAudioFileReader().getAudioInputStream(in);
            }
        });

        assertThrows(UnsupportedAudioFileException.class, () -> new AACAudioFileReader().getAudioInputStream(path.toFile()));

        assertThrows(UnsupportedAudioFileException.class, () -> new AACAudioFileReader().getAudioInputStream(path.toUri().toURL()));
    }

    @Test
    @DisplayName("not aac")
    public void test11() throws Exception {
        Path path = Paths.get(getClass().getClassLoader().getResource(mid).toURI());
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(path))) {
            assertThrows(UnsupportedAudioFileException.class, () -> new AACAudioFileReader().getAudioInputStream(bufferedInputStream));
        }
    }

    @Test
    @DisplayName("movie does not contain any AAC track")
    public void test12() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(alac)) {
            assertThrows(UnsupportedAudioFileException.class, () -> new AACAudioFileReader().getAudioInputStream(inputStream));
        }
    }

    @Test
    @Disabled("couldn't find good sample")
    @DisplayName("a file consumes input stream all")
    public void test13() throws Exception {
        Path path = Paths.get(AacFormatConversionProviderTest.class.getResource(caf).toURI());
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path))) {
            assertThrows(UnsupportedAudioFileException.class, () -> new AACAudioFileReader().getAudioInputStream(in));
        }
    }

    @Test
    @DisplayName("aac -> pcm")
    public void test2() throws Exception {

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(aac)) {
            AudioInputStream aacAis = AudioSystem.getAudioInputStream(inputStream);
            logger.debug(() -> "INS: " + aacAis);
            AudioFormat inAudioFormat = aacAis.getFormat();
            logger.debug(() -> "INF: " + inAudioFormat);
            AudioFormat outAudioFormat = new AudioFormat(
                    AudioSystem.NOT_SPECIFIED,
                    16,
                    AudioSystem.NOT_SPECIFIED,
                    true,
                    false);

            assertTrue(AudioSystem.isConversionSupported(outAudioFormat, inAudioFormat));

            SourceDataLine line;
            try (AudioInputStream pcmAis = AudioSystem.getAudioInputStream(outAudioFormat, aacAis)) {
                logger.debug(() -> "OUTS: " + pcmAis);
                logger.debug(() -> "OUT: " + pcmAis.getFormat());
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, pcmAis.getFormat());
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(pcmAis.getFormat());
                volume(line, .05d);
                line.start();

                byte[] buf = new byte[8192];
                while (!later(time).come()) {
                    int r = pcmAis.read(buf, 0, buf.length);
                    if (r < 0) {
                        break;
                    }
                    line.write(buf, 0, r);
                }
            }
            line.drain();
            line.stop();
            line.close();
        }
    }

    @Test
    @DisplayName("mp4 -> pcm")
    public void test3() throws Exception {

        final URL resource = getClass().getClassLoader().getResource(mp4);
        try (AudioInputStream aacAis = AudioSystem.getAudioInputStream(resource)) {
            logger.debug(() -> "INS: " + aacAis);
            AudioFormat inAudioFormat = aacAis.getFormat();
            logger.debug(() -> "INF: " + inAudioFormat);
            AudioFormat outAudioFormat = new AudioFormat(
                    inAudioFormat.getSampleRate(),
                    16,
                    inAudioFormat.getChannels(),
                    true,
                    false);

            assertTrue(AudioSystem.isConversionSupported(outAudioFormat, inAudioFormat));

            SourceDataLine line;
            try (AudioInputStream pcmAis = AudioSystem.getAudioInputStream(outAudioFormat, aacAis)) {

                logger.debug(() -> "OUTS: " + pcmAis);
                logger.debug(() -> "OUT: " + pcmAis.getFormat());
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, pcmAis.getFormat());
                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(pcmAis.getFormat());
                volume(line, .05d);
                line.start();
                byte[] buf = new byte[1024];
                while (!later(time).come()) {
                    int r = pcmAis.read(buf, 0, 1024);
                    if (r < 0) {
                        break;
                    }
                    line.write(buf, 0, r);
                }
            }
            line.drain();
            line.stop();
            line.close();
        }
    }

    @Test
    @DisplayName("another input type 2")
    void test62() throws Exception {
        URL url = getClass().getClassLoader().getResource(mp4);
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
            assertEquals(AACAudioFileReader.AAC_ENCODING, ais.getFormat().getEncoding());
        }
    }

    @Test
    @DisplayName("another input type 3")
    void test63() throws Exception {
        URL url = getClass().getClassLoader().getResource(mp4);
        File file = Paths.get(url.toURI()).toFile();
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            assertEquals(AACAudioFileReader.AAC_ENCODING, ais.getFormat().getEncoding());
        }
    }

    @Test
    @DisplayName("clip")
    void test4() throws Exception {

        final URL resource = getClass().getClassLoader().getResource(mp4);

        Clip clip;
        CountDownLatch cdl;
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(resource)) {

            clip = AudioSystem.getClip();
            cdl = new CountDownLatch(1);
            clip.addLineListener(ev -> {
                if (ev.getType() == LineEvent.Type.STOP)
                    cdl.countDown();
            });
            clip.open(AudioSystem.getAudioInputStream(new AudioFormat(44100, 16, 2, true, false), ais));
        }
        volume(clip, 0.1f);
        clip.start();
        if (!System.getProperty("vavi.test", "").equals("ide")) {
            Thread.sleep(10 * 1000);
            clip.stop();
        } else {
            cdl.await();
        }
        clip.drain();
        clip.stop();
        clip.close();
    }
}
