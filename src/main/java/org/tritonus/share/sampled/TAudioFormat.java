/*
 * TAudioFormat.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2003 by Matthias Pfisterer
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

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.share.sampled;

import javax.sound.sampled.AudioFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class TAudioFormat
        extends AudioFormat {
    private Map<String, Object> m_properties;
    private Map<String, Object> m_unmodifiableProperties;


    public TAudioFormat(Encoding encoding,
                        float sampleRate,
                        int sampleSizeInBits,
                        int channels,
                        int frameSize,
                        float frameRate,
                        boolean bigEndian,
                        Map<String, Object> properties) {
        super(encoding,
                sampleRate,
                sampleSizeInBits,
                channels,
                frameSize,
                frameRate,
                bigEndian);
        initMaps(properties);
    }

    /**
     * Create an instance of TAudioFormat as a copy of the supplied audio
     * format.
     *
     * @param format the instance to copy
     */
    public TAudioFormat(AudioFormat format) {
        this(format.getEncoding(),
                format.getSampleRate(),
                format.getSampleSizeInBits(),
                format.getChannels(),
                format.getFrameSize(),
                format.getFrameRate(),
                format.isBigEndian(),
                format.properties());
    }

    /**
     * Create an instance of TAudioFormat as a copy of the supplied audio
     * format, adding the given properties to any properties supplied by
     * <code>format</code>. Duplicate properties in the supplied
     * <code>properties</code> will overwrite the ones in <code>format</code>.
     *
     * @param format     the instance to copy
     * @param properties properties to be added to this TAudioFormat
     */
    public TAudioFormat(AudioFormat format,
                        Map<String, Object> properties) {
        this(format);
        m_properties.putAll(properties);
    }

    public TAudioFormat(float sampleRate,
                        int sampleSizeInBits,
                        int channels,
                        boolean signed,
                        boolean bigEndian,
                        Map<String, Object> properties) {
        super(sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
        initMaps(properties);
    }


    private void initMaps(Map<String, Object> properties) {
  /* Here, we make a shallow copy of the map. It's unclear if this
     is sufficient (or if a deep copy should be made).
  */
        m_properties = new HashMap<>();
        if (properties != null) {
            m_properties.putAll(properties);
        }
        m_unmodifiableProperties = Collections.unmodifiableMap(m_properties);
    }


    @Override
    public Map<String, Object> properties() {
        if (m_properties == null) {
            initMaps(null);
        }
        return m_unmodifiableProperties;
    }


    @Override
    public Object getProperty(String key) {
        if (m_properties == null) {
            return null;
        }
        return m_properties.get(key);
    }


    protected void setProperty(String key, Object value) {
        if (m_properties == null) {
            initMaps(null);
        }
        m_properties.put(key, value);
    }
}


/* TAudioFormat.java */
