package net.sourceforge.jaad;

import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.adts.ADTSDemultiplexer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.MP4Input;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import net.sourceforge.jaad.mp4.api.Track;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SimpleTest {

    String mp4 = "test.m4a";
    @Test
    void decodeMP4() throws Exception {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(mp4)) {
            SourceDataLine line = null;
            // create container
            MP4Input is = MP4Input.open(in);
            MP4Container cont = new MP4Container(is);
            Movie movie = cont.getMovie();
            // find AAC track
            List<Track> tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
            AudioTrack track = (AudioTrack) tracks.get(0);

            // create audio format
            AudioFormat aufmt = new AudioFormat(
                    track.getSampleRate(), track.getSampleSize(), track.getChannelCount(), true, true);
            line = AudioSystem.getSourceDataLine(aufmt);
            line.open();
//        volume(line, .1f);
            line.start();

            // create AAC decoder
            Decoder dec = Decoder.create(track.getDecoderSpecificInfo().getData());

            // decode
            Frame frame;
            SampleBuffer buf = new SampleBuffer();
//            while (track.hasMoreFrames() && !later(time).come()) {
//                frame = track.readNextFrame();
//                dec.decodeFrame(frame.getData(), buf);
//                byte[] b = buf.getData();
//                line.write(b, 0, b.length);
//            }
        }
    }

    @Test
    void decodeAAC() throws Exception {
        SourceDataLine line;
        ADTSDemultiplexer adts;
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("test.aac")) {
            line = null;
            adts = new ADTSDemultiplexer(in);

            Decoder dec = Decoder.create(adts.getDecoderInfo());
            SampleBuffer buf = new SampleBuffer();
//            while (!later(time).come()) {
//                try {
//                    byte[] b = adts.readNextFrame();
//                    dec.decodeFrame(b, buf);
//
//                    if (line == null) {
//                        // need to read the first frame for determine rate, ch etc.
//                        AudioFormat aufmt = new AudioFormat(
//                                buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, true);
//                        Debug.println("IN: " + aufmt);
//                        line = AudioSystem.getSourceDataLine(aufmt);
//                        line.open();
//                        volume(line, .1f);
//                        line.start();
//                    }
//                    b = buf.getData();
//                    line.write(b, 0, b.length);
//                } catch (EOFException e) {
//                    break;
//                }
//            }
        }
    }
}
