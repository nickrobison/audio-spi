/*
 * TNonSeekableDataOutputStreamTestCase.java
 */

/*
 *  Copyright (c) 2004 by Matthias Pfisterer
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

package org.tritonus.test.tritonus.share.sampled.file;

import org.tritonus.share.sampled.file.TDataOutputStream;
import org.tritonus.share.sampled.file.TNonSeekableDataOutputStream;

import java.io.ByteArrayOutputStream;


public class TNonSeekableDataOutputStreamTestCase
        extends BaseDataOutputStreamTestCase {
    ByteArrayOutputStream m_baos;


    public TNonSeekableDataOutputStreamTestCase() {
        super(false);  // non seekable
    }


    protected TDataOutputStream createDataOutputStream()
            throws Exception {
        m_baos = new ByteArrayOutputStream();
        return new TNonSeekableDataOutputStream(m_baos);
    }


    protected byte[] getWrittenData()
            throws Exception {
        return m_baos.toByteArray();
    }
}


/* TNonSeekableDataOutputStreamTestCase.java */
