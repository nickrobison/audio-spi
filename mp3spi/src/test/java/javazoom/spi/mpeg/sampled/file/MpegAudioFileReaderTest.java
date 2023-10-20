/*
 *   MpegAudioFileReaderTest - JavaZOOM : http://www.javazoom.net
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;


/**
 * MpegAudioFileReader unit test.
 * It matches test.mp3 properties to test.mp3.properties expected results.
 */
public class MpegAudioFileReaderTest {

    private static Logger logger = Logger.getLogger(MpegAudioFileReaderTest.class.getName());
    private String filename = null;
    private URL fileurl = null;
    private Properties props = null;

    @BeforeEach
    protected void setUp() throws Exception {
        props = new Properties();
        InputStream pin = getClass().getClassLoader().getResourceAsStream("test.mp3.properties");
        fileurl = getClass().getClassLoader().getResource("test.mp3");
        props.load(pin);
        filename = fileurl.getFile();
    }

    @DisplayName("Test for AudioFileFormat getAudioFileFormat(File)")
    @Test
    public void _testGetAudioFileFormatFile() {
        logger.info("*** testGetAudioFileFormatFile ***");
        try {
            File file = new File(filename);
            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(file);
            dumpAudioFileFormat(baseFileFormat, file.toString());
            assertEquals(Integer.parseInt(props.getProperty("FrameLength")), baseFileFormat.getFrameLength(), "FrameLength");
            assertEquals(Integer.parseInt(props.getProperty("ByteLength")), baseFileFormat.getByteLength(), "ByteLength");
        } catch (UnsupportedAudioFileException | IOException e) {
            fail("testGetAudioFileFormatFile: " + e);
        }
    }

    @DisplayName("Test for AudioFileFormat getAudioFileFormat(URL)")
    @Test
    public void _testGetAudioFileFormatURL() {
        logger.info("*** testGetAudioFileFormatURL ***");
        try {
            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(fileurl);
            dumpAudioFileFormat(baseFileFormat, fileurl.toString());
            assertEquals(-1, baseFileFormat.getFrameLength(), "FrameLength");
            assertEquals(-1, baseFileFormat.getByteLength(), "ByteLength");
        } catch (UnsupportedAudioFileException | IOException e) {
            fail("testGetAudioFileFormatURL: " + e);
        }
    }

    @DisplayName("Test for AudioFileFormat getAudioFileFormat(InputStream)")
    @Test
    public void _testGetAudioFileFormatInputStream() throws URISyntaxException {
        logger.info("*** testGetAudioFileFormatInputStream ***");
        try {
            InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(fileurl.toURI())));
            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(in);
            dumpAudioFileFormat(baseFileFormat, in.toString());
            in.close();
            assertEquals(-1, baseFileFormat.getFrameLength(), "FrameLength");
            assertEquals(-1, baseFileFormat.getByteLength(), "ByteLength");
        } catch (UnsupportedAudioFileException | IOException e) {
            fail("testGetAudioFileFormatInputStream: " + e);
        }
    }

    @DisplayName("Test for AudioInputStream getAudioInputStream(InputStream)")
    @Test
    public void _testGetAudioInputStreamInputStream() throws URISyntaxException {
        logger.info("*** testGetAudioInputStreamInputStream ***");
        try {
            InputStream fin = new BufferedInputStream(Files.newInputStream(Paths.get(fileurl.toURI())));
            AudioInputStream in = AudioSystem.getAudioInputStream(fin);
            dumpAudioInputStream(in, fin.toString());
            assertEquals(-1, in.getFrameLength(), "FrameLength");
            assertEquals(Integer.parseInt(props.getProperty("Available")), in.available(), "Available");
            fin.close();
            in.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            fail("testGetAudioInputStreamInputStream: " + e);
        }
    }

    @DisplayName("Test for AudioInputStream getAudioInputStream(File)")
    @Test
    public void _testGetAudioInputStreamFile() {
        logger.info("*** testGetAudioInputStreamFile ***");
        try {
            File file = new File(filename);
            AudioInputStream in = AudioSystem.getAudioInputStream(file);
            dumpAudioInputStream(in, file.toString());
            assertEquals(-1, in.getFrameLength(), "FrameLength");
            assertEquals(Integer.parseInt(props.getProperty("Available")), in.available(), "Available");
            in.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            fail("testGetAudioInputStreamFile:" + e);
        }
    }

    @DisplayName("Test for AudioInputStream getAudioInputStream(URL)")
    @Test
    public void _testGetAudioInputStreamURL() {
        logger.info("*** testGetAudioInputStreamURL ***");
        try {
            AudioInputStream in = AudioSystem.getAudioInputStream(fileurl);
            dumpAudioInputStream(in, fileurl.toString());
            assertEquals(-1, in.getFrameLength(), "FrameLength");
            assertEquals(Integer.parseInt(props.getProperty("Available")), in.available(), "Available");
            in.close();
        } catch (UnsupportedAudioFileException | IOException e) {
            fail("testGetAudioInputStreamURL: " + e);
        }
    }

    private void dumpAudioFileFormat(AudioFileFormat baseFileFormat,
                                     String info) throws UnsupportedAudioFileException {
        AudioFormat baseFormat = baseFileFormat.getFormat();
        // AudioFileFormat
        logger.info("  -----  " + info + "  -----");
        logger.info("    ByteLength=" + baseFileFormat.getByteLength());
        logger.info("    FrameLength=" + baseFileFormat.getFrameLength());
        logger.info("    Type=" + baseFileFormat.getType());
        // AudioFormat
        logger.info("    SourceFormat=" + baseFormat.toString());
        logger.info("    Channels=" + baseFormat.getChannels());
        logger.info("    FrameRate=" + baseFormat.getFrameRate());
        logger.info("    FrameSize=" + baseFormat.getFrameSize());
        logger.info("    SampleRate=" + baseFormat.getSampleRate());
        logger.info("    SampleSizeInBits=" + baseFormat.getSampleSizeInBits());
        logger.info("    Encoding=" + baseFormat.getEncoding());
        assertEquals(props.getProperty("Type"), baseFileFormat.getType().toString(), "Type");
        assertTrue(baseFormat.toString().startsWith(props.getProperty("SourceFormat")), "SourceFormat");
        assertEquals(Integer.parseInt(props.getProperty("Channels")), baseFormat.getChannels(), "Channels");
        assertEquals(Float.parseFloat(props.getProperty("FrameRate")), baseFormat.getFrameRate(), "FrameRate");
        assertEquals(Integer.parseInt(props.getProperty("FrameSize")), baseFormat.getFrameSize(), "FrameSize");
        assertEquals(Float.parseFloat(props.getProperty("SampleRate")), baseFormat.getSampleRate(), "SampleRate");
        assertEquals(Integer.parseInt(props.getProperty("SampleSizeInBits")),
                     baseFormat.getSampleSizeInBits(),
                     "SampleSizeInBits");
        assertEquals(props.getProperty("Encoding"), baseFormat.getEncoding().toString(), "Encoding");
    }

    private void dumpAudioInputStream(AudioInputStream in, String info) throws IOException {
        AudioFormat baseFormat = in.getFormat();
        logger.info("  -----  " + info + "  -----");
        logger.info("    Available=" + in.available());
        logger.info("    FrameLength=" + in.getFrameLength());
        // AudioFormat
        logger.info("    SourceFormat=" + baseFormat.toString());
        logger.info("    Channels=" + baseFormat.getChannels());
        logger.info("    FrameRate=" + baseFormat.getFrameRate());
        logger.info("    FrameSize=" + baseFormat.getFrameSize());
        logger.info("    SampleRate=" + baseFormat.getSampleRate());
        logger.info("    SampleSizeInBits=" + baseFormat.getSampleSizeInBits());
        logger.info("    Encoding=" + baseFormat.getEncoding());
        assertTrue(baseFormat.toString().startsWith(props.getProperty("SourceFormat")));
        assertEquals(Integer.parseInt(props.getProperty("Channels")), baseFormat.getChannels(), "Channels");
        assertEquals(Float.parseFloat(props.getProperty("FrameRate")), baseFormat.getFrameRate(), "FrameRate");
        assertEquals(Integer.parseInt(props.getProperty("FrameSize")), baseFormat.getFrameSize(), "FrameSize");
        assertEquals(Float.parseFloat(props.getProperty("SampleRate")), baseFormat.getSampleRate(), "SampleRate");
        assertEquals(Integer.parseInt(props.getProperty("SampleSizeInBits")),
                     baseFormat.getSampleSizeInBits(),
                     "SampleSizeInBits");
        assertEquals(props.getProperty("Encoding"), baseFormat.getEncoding().toString(), "Encoding");
    }
}
