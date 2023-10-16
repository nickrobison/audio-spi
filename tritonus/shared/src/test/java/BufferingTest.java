/*
 * BufferingTest.java
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


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;


public class BufferingTest {
    public static void main(String[] args)
            throws IOException {
//   byte[] abData = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
//   ByteArrayInputStream bais = new ByteArrayInputStream(abData);
//   System.out.println(bais.markSupported());
//   bais.mark(15);
//   bais.reset();

        FileInputStream fis = new FileInputStream("BufferingTest.java");
        System.out.println("FileInputStream supports mark: " + fis.markSupported());

        BufferedInputStream bis = new BufferedInputStream(fis, 5);
        byte[] abRead1 = new byte[9];
        byte[] abRead2 = new byte[9];
        byte[] abRead3 = new byte[9];
        byte[] abRead4 = new byte[9];
        bis.mark(9);
        bis.read(abRead1);
        bis.mark(9);
        bis.read(abRead2);
        bis.reset();
        bis.read(abRead3);
        bis.reset();
        bis.read(abRead4);

        for (int i = 0; i < abRead1.length; i++) {
            if (abRead1[i] != abRead4[i]) {
                System.out.println("1 difference!!");
            }
        }
        for (int i = 0; i < abRead1.length; i++) {
            if (abRead2[i] != abRead3[i]) {
                System.out.println("2 difference!!");
            }
        }
    }
}


/* BufferingTest.java */
