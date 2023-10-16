/*
 * CreateSysexSequence.java
 *
 * TODO: short description
 */

/*
 *  Copyright (c) 2000 by Matthias Pfisterer
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;


/**
 * Creates a Sequence with some sysex messages.
 * [F0 01 F7]
 * [F0 F7]
 * [F0 02]
 * [F7 F7]
 * [F0 03] ??
 * [F7] ??
 * [F7 F0 04 F7]
 */
public class CreateSysexSequence {
    public static void main(String[] args)
            throws IOException, InvalidMidiDataException {
        if (args.length != 1) {
            out("usage:");
            out("java CreateSysexSequence <midifile>");
            System.exit(1);
        }
        int nResolution = 480;
        String strFilename = args[0];
        Sequence sequence = new Sequence(Sequence.PPQ,
                nResolution);
        Track track = sequence.createTrack();
        SysexMessage sm;
        MetaMessage mm;
        MidiEvent me;
        byte[] abData;

        // [F0 01 F7]
        sm = new SysexMessage();
        abData = new byte[] {(byte) 0xF0, (byte) 0x01, (byte) 0xF7};
        sm.setMessage(abData, abData.length);
        me = new MidiEvent(sm, 0);
        track.add(me);

//   // [F0 F7]
//   sm = new SysexMessage();
//   abData = new byte[]{(byte) 0xF0, (byte) 0xF7};
//   sm.setMessage(abData, abData.length);
//   me = new MidiEvent(sm, 0);
//   track.add(me);

        // [F0 02]
        sm = new SysexMessage();
        abData = new byte[] {(byte) 0xF0, (byte) 0x02};
        sm.setMessage(abData, abData.length);
        me = new MidiEvent(sm, 0);
        track.add(me);

        // [F7 02 F7]
        sm = new SysexMessage();
        abData = new byte[] {(byte) 0xF7, (byte) 0x02, (byte) 0xF7};
        sm.setMessage(abData, abData.length);
        me = new MidiEvent(sm, 0);
        track.add(me);

//   // [F0 02]
//   sm = new SysexMessage();
//   abData = new byte[]{(byte) 0xF0, (byte) 0x02};
//   sm.setMessage(abData, abData.length);
//   me = new MidiEvent(sm, 0);
//   track.add(me);

//   // [F7 F7]
//   sm = new SysexMessage();
//   abData = new byte[]{(byte) 0xF7, (byte) 0xF7};
//   sm.setMessage(abData, abData.length);
//   me = new MidiEvent(sm, 0);
//   track.add(me);

//   // [F0 03]
//   sm = new SysexMessage();
//   abData = new byte[]{(byte) 0xF0, (byte) 0x03};
//   sm.setMessage(abData, abData.length);
//   me = new MidiEvent(sm, 0);
//   track.add(me);

//   // [F7]
//   sm = new SysexMessage();
//   abData = new byte[]{(byte) 0xF7};
//   sm.setMessage(abData, abData.length);
//   me = new MidiEvent(sm, 0);
//   track.add(me);

        // [F7 F0 04 F7]
        sm = new SysexMessage();
        abData = new byte[] {(byte) 0xF7, (byte) 0xF0, (byte) 0x04, (byte) 0xF7};
        sm.setMessage(abData, abData.length);
        me = new MidiEvent(sm, 0);
        track.add(me);

        mm = new MetaMessage();
        mm.setMessage(0x2F, new byte[0], 0);
        me = new MidiEvent(mm, 10);
        track.add(me);

        MidiSystem.write(sequence, 0, new File(strFilename));

        /*
         * This is only necessary because of a bug in the Sun jdk1.3
         */
        System.exit(0);
    }


    private static void out(String strMessage) {
        System.out.println(strMessage);
    }
}


/* CreateSysexSequence.java */
