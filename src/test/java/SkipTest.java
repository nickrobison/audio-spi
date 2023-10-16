/*
 * SkipTest.java
 */

/*
 *  Copyright (c) 1999, 2000 by Matthias Pfisterer
 *
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
 *
 */


import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class SkipTest {
    private static final int LOAD_METHOD_STREAM = 1;
    private static final int LOAD_METHOD_FILE = 2;
    private static final int LOAD_METHOD_URL = 3;


    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }
        int nLoadMethod = LOAD_METHOD_FILE;
        boolean bCheckAudioInputStream;
        int nCurrentArg = 0;
        while (nCurrentArg < args.length) {
            if (args[nCurrentArg].equals("-h")) {
                printUsageAndExit();
            }
/*
   else if (args[nCurrentArg].equals("-s"))
   {
    nLoadMethod = LOAD_METHOD_STREAM;
   }
   else if (args[nCurrentArg].equals("-f"))
   {
    nLoadMethod = LOAD_METHOD_FILE;
   }
   else if (args[nCurrentArg].equals("-u"))
   {
    nLoadMethod = LOAD_METHOD_URL;
   }
   else if (args[nCurrentArg].equals("-i"))
   {
    bCheckAudioInputStream = true;
   }
*/

            nCurrentArg++;
        }
        bCheckAudioInputStream = true;
        String strSource = args[nCurrentArg - 2];
        long lSkip = Long.parseLong(args[nCurrentArg - 1]);
        String strFilename = null;
        AudioFileFormat aff = null;
        AudioInputStream ais = null;
        try {
            switch (nLoadMethod) {
            case LOAD_METHOD_STREAM:
                InputStream inputStream = System.in;
                aff = AudioSystem.getAudioFileFormat(inputStream);
                strFilename = "<standard input>";
                if (bCheckAudioInputStream) {
                    ais = AudioSystem.getAudioInputStream(inputStream);
                }
                break;

            case LOAD_METHOD_FILE:
                File file = new File(strSource);
                aff = AudioSystem.getAudioFileFormat(file);
                strFilename = file.getCanonicalPath();
                if (bCheckAudioInputStream) {
                    ais = AudioSystem.getAudioInputStream(file);
                }
                break;

            case LOAD_METHOD_URL:
                URL url = new URL(strSource);
                aff = AudioSystem.getAudioFileFormat(url);
                strFilename = url.toString();
                if (bCheckAudioInputStream) {
                    ais = AudioSystem.getAudioInputStream(url);
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (aff == null) {
            System.out.println("Cannot determine format");
        } else {
/*
   AudioFormat format = aff.getFormat();
   System.out.println("---------------------------------------------------------------------------");
   System.out.println("Source: " + strFilename);
   System.out.println("Type: " + aff.getType());
   System.out.println("AudioFormat: " + format);
   System.out.println("---------------------------------------------------------------------------");
   String strAudioLength = null;
   if (aff.getFrameLength() != AudioSystem.NOT_SPECIFIED)
   {
    strAudioLength = "" + aff.getFrameLength() + " frames (= " + aff.getFrameLength() * format.getFrameSize() + " bytes)";
   }
   else
   {
    strAudioLength = "unknown";
   }
   System.out.println("Length of audio data: " + strAudioLength);
   String strFileLength = null;
   if (aff.getByteLength() != AudioSystem.NOT_SPECIFIED)
   {
    strFileLength = "" + aff.getByteLength() + " bytes)";
   }
   else
   {
    strFileLength = "unknown";
   }
   System.out.println("Total length of file (including headers): " + strFileLength);
*/
            if (bCheckAudioInputStream) {
                // System.out.println("[AudioInputStream says:] Length of audio data: " + ais.getFrameLength() + " frames (= " + ais.getFrameLength() * ais.getFormat().getFrameSize() + " bytes)");
                System.out.println("frame length: " + ais.getFrameLength());
                System.out.println("frame size: " + ais.getFormat().getFrameSize());
                System.out.println("AIS class:" + ais);
                System.out.println("now skipping...");
                long lSkipped = 0;
                try {
                    lSkipped = ais.skip(lSkip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("skipped: " + lSkipped);
            }
            System.out.println("---------------------------------------------------------------------------");
        }
    }


    private static void printUsageAndExit() {
        System.out.println("SkipTest: usage:");
        System.out.println("\tjava SkipTest <audiofile> <skip>");
        System.exit(1);
    }


}


/* SkipTest.java */
