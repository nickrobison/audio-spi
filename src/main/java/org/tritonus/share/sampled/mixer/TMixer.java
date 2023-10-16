/*
 * TMixer.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2004 by Matthias Pfisterer
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

/*
|<---            this code is formatted to fit into 80 columns             --->|
*/

package org.tritonus.share.sampled.mixer;

import org.tritonus.share.ArraySet;
import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.AudioFormats;
import org.tritonus.share.sampled.mixer.TLine;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


// TODO: global controls (that use the system mixer)
public abstract class TMixer
        extends TLine
        implements Mixer {
    private static Line.Info[] EMPTY_LINE_INFO_ARRAY = new Line.Info[0];
    private static Line[] EMPTY_LINE_ARRAY = new Line[0];

    private Mixer.Info m_mixerInfo;
    private Collection<AudioFormat> m_supportedSourceFormats;
    private Collection<AudioFormat> m_supportedTargetFormats;
    private Collection<Line.Info> m_supportedSourceLineInfos;
    private Collection<Line.Info> m_supportedTargetLineInfos;
    private final Set<SourceDataLine> m_openSourceDataLines;
    private final Set<TargetDataLine> m_openTargetDataLines;


    /**
     * Constructor for mixers that use setSupportInformation().
     */
    protected TMixer(Mixer.Info mixerInfo,
                     Line.Info lineInfo) {
        this(mixerInfo,
                lineInfo,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
    }


    /**
     * Constructor for mixers.
     */
    protected TMixer(Mixer.Info mixerInfo,
                     Line.Info lineInfo,
                     Collection<AudioFormat> supportedSourceFormats,
                     Collection<AudioFormat> supportedTargetFormats,
                     Collection<Line.Info> supportedSourceLineInfos,
                     Collection<Line.Info> supportedTargetLineInfos) {
        super(null, // TMixer
                lineInfo);
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.<init>(): begin");
        }
        m_mixerInfo = mixerInfo;
        setSupportInformation(
                supportedSourceFormats,
                supportedTargetFormats,
                supportedSourceLineInfos,
                supportedTargetLineInfos);
        m_openSourceDataLines = new ArraySet<>();
        m_openTargetDataLines = new ArraySet<>();
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.<init>(): end");
        }
    }


    protected void setSupportInformation(
            Collection<AudioFormat> supportedSourceFormats,
            Collection<AudioFormat> supportedTargetFormats,
            Collection<Line.Info> supportedSourceLineInfos,
            Collection<Line.Info> supportedTargetLineInfos) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.setSupportInformation(): begin");
        }
        m_supportedSourceFormats = supportedSourceFormats;
        m_supportedTargetFormats = supportedTargetFormats;
        m_supportedSourceLineInfos = supportedSourceLineInfos;
        m_supportedTargetLineInfos = supportedTargetLineInfos;
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.setSupportInformation(): end");
        }
    }


    public Mixer.Info getMixerInfo() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getMixerInfo(): begin");
        }
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getMixerInfo(): end");
        }
        return m_mixerInfo;
    }


    public Line.Info[] getSourceLineInfo() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceLineInfo(): begin");
        }
        Line.Info[] infos = m_supportedSourceLineInfos.toArray(EMPTY_LINE_INFO_ARRAY);
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceLineInfo(): end");
        }
        return infos;
    }


    public Line.Info[] getTargetLineInfo() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetLineInfo(): begin");
        }
        Line.Info[] infos = m_supportedTargetLineInfos.toArray(EMPTY_LINE_INFO_ARRAY);
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetLineInfo(): end");
        }
        return infos;
    }


    public Line.Info[] getSourceLineInfo(Line.Info info) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceLineInfo(Line.Info): info to test: " + info);
        }
        // TODO:
        return EMPTY_LINE_INFO_ARRAY;
    }


    public Line.Info[] getTargetLineInfo(Line.Info info) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetLineInfo(Line.Info): info to test: " + info);
        }
        // TODO:
        return EMPTY_LINE_INFO_ARRAY;
    }


    public boolean isLineSupported(Line.Info info) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.isLineSupported(): info to test: " + info);
        }
        Class<?> lineClass = info.getLineClass();
        if (lineClass.equals(SourceDataLine.class)) {
            return isLineSupportedImpl(info, m_supportedSourceLineInfos);
        } else if (lineClass.equals(TargetDataLine.class)) {
            return isLineSupportedImpl(info, m_supportedTargetLineInfos);
        } else if (lineClass.equals(Port.class)) {
            return isLineSupportedImpl(info, m_supportedSourceLineInfos) || isLineSupportedImpl(info, m_supportedTargetLineInfos);
        } else {
            return false;
        }
    }


    private static boolean isLineSupportedImpl(Line.Info info, Collection<Line.Info> supportedLineInfos) {
        for (Line.Info info2 : supportedLineInfos) {
            if (info2.matches(info)) {
                return true;
            }
        }
        return false;
    }


    public Line getLine(Line.Info info)
            throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getLine(): begin");
        }
        Class<?> lineClass = info.getLineClass();
        DataLine.Info dataLineInfo = null;
        Port.Info portInfo = null;
        AudioFormat[] aFormats = null;
        if (info instanceof DataLine.Info) {
            dataLineInfo = (DataLine.Info) info;
            aFormats = dataLineInfo.getFormats();
        } else if (info instanceof Port.Info) {
            portInfo = (Port.Info) info;
        }
        AudioFormat format;
        Line line;
        if (lineClass == SourceDataLine.class) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): type: SourceDataLine");
            }
            if (dataLineInfo == null) {
                throw new IllegalArgumentException("need DataLine.Info for SourceDataLine");
            }
            format = getSupportedSourceFormat(aFormats);
            line = getSourceDataLine(format, dataLineInfo.getMaxBufferSize());
        } else if (lineClass == Clip.class) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): type: Clip");
            }
            if (dataLineInfo == null) {
                throw new IllegalArgumentException("need DataLine.Info for Clip");
            }
            format = getSupportedSourceFormat(aFormats);
            line = getClip(format);
        } else if (lineClass == TargetDataLine.class) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): type: TargetDataLine");
            }
            if (dataLineInfo == null) {
                throw new IllegalArgumentException("need DataLine.Info for TargetDataLine");
            }
            format = getSupportedTargetFormat(aFormats);
            line = getTargetDataLine(format, dataLineInfo.getMaxBufferSize());
        } else if (lineClass == Port.class) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): type: TargetDataLine");
            }
            if (portInfo == null) {
                throw new IllegalArgumentException("need Port.Info for Port");
            }
            line = getPort(portInfo);
        } else {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): unknown line type, will throw exception");
            }
            throw new LineUnavailableException("unknown line class: " + lineClass);
        }
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getLine(): end");
        }
        return line;
    }


    protected SourceDataLine getSourceDataLine(AudioFormat format, int nBufferSize)
            throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceDataLine(): begin");
        }
        throw new IllegalArgumentException("this mixer does not support SourceDataLines");
    }


    protected Clip getClip(AudioFormat format)
            throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getClip(): begin");
        }
        throw new IllegalArgumentException("this mixer does not support Clips");
    }


    protected TargetDataLine getTargetDataLine(AudioFormat format, int nBufferSize)
            throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetDataLine(): begin");
        }
        throw new IllegalArgumentException("this mixer does not support TargetDataLines");
    }


    protected Port getPort(Port.Info info)
            throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetDataLine(): begin");
        }
        throw new IllegalArgumentException("this mixer does not support Ports");
    }


    private AudioFormat getSupportedSourceFormat(AudioFormat[] aFormats) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSupportedSourceFormat(): begin");
        }
        AudioFormat format = null;
        for (AudioFormat aFormat : aFormats) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getSupportedSourceFormat(): checking " + aFormat + "...");
            }
            if (isSourceFormatSupported(aFormat)) {
                if (TDebug.TraceMixer) {
                    TDebug.out("TMixer.getSupportedSourceFormat(): ...supported");
                }
                format = aFormat;
                break;
            } else {
                if (TDebug.TraceMixer) {
                    TDebug.out("TMixer.getSupportedSourceFormat(): ...no luck");
                }
            }
        }
        if (format == null) {
            throw new IllegalArgumentException("no line matchine one of the passed formats");
        }
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSupportedSourceFormat(): end");
        }
        return format;
    }


    private AudioFormat getSupportedTargetFormat(AudioFormat[] aFormats) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSupportedTargetFormat(): begin");
        }
        AudioFormat format = null;
        for (AudioFormat aFormat : aFormats) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getSupportedTargetFormat(): checking " + aFormat + " ...");
            }
            if (isTargetFormatSupported(aFormat)) {
                if (TDebug.TraceMixer) {
                    TDebug.out("TMixer.getSupportedTargetFormat(): ...supported");
                }
                format = aFormat;
                break;
            } else {
                if (TDebug.TraceMixer) {
                    TDebug.out("TMixer.getSupportedTargetFormat(): ...no luck");
                }
            }
        }
        if (format == null) {
            throw new IllegalArgumentException("no line matchine one of the passed formats");
        }
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSupportedTargetFormat(): end");
        }
        return format;
    }



/*
  not implemented here:
  getMaxLines(Line.Info)
*/


    public Line[] getSourceLines() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceLines(): called");
        }
        return m_openSourceDataLines.toArray(EMPTY_LINE_ARRAY);
    }


    public Line[] getTargetLines() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetLines(): called");
        }
        return m_openTargetDataLines.toArray(EMPTY_LINE_ARRAY);
    }


    public void synchronize(Line[] aLines,
                            boolean bMaintainSync) {
        throw new UnsupportedOperationException("synchronization not supported");
    }


    public void unsynchronize(Line[] aLines) {
        throw new UnsupportedOperationException("synchronization not supported");
    }


    public boolean isSynchronizationSupported(Line[] aLines,
                                              boolean bMaintainSync) {
        return false;
    }


    protected boolean isSourceFormatSupported(AudioFormat format) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.isSourceFormatSupported(): format to test: " + format);
        }
        for (AudioFormat supportedFormat : m_supportedSourceFormats) {
            if (AudioFormats.matches(supportedFormat, format)) {
                return true;
            }
        }
        return false;
    }


    protected boolean isTargetFormatSupported(AudioFormat format) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.isTargetFormatSupported(): format to test: " + format);
        }
        for (AudioFormat supportedFormat : m_supportedTargetFormats) {
            if (AudioFormats.matches(supportedFormat, format)) {
                return true;
            }
        }
        return false;
    }


    /*package*/ void registerOpenLine(Line line) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.registerOpenLine(): line to register: " + line);
        }
        if (line instanceof SourceDataLine) {
            synchronized (m_openSourceDataLines) {
                m_openSourceDataLines.add((SourceDataLine) line);
            }
        } else if (line instanceof TargetDataLine) {
            synchronized (m_openSourceDataLines) {
                m_openTargetDataLines.add((TargetDataLine) line);
            }
        }
    }


    /*package*/ void unregisterOpenLine(Line line) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.unregisterOpenLine(): line to unregister: " + line);
        }
        if (line instanceof SourceDataLine) {
            synchronized (m_openSourceDataLines) {
                m_openSourceDataLines.remove(line);
            }
        } else if (line instanceof TargetDataLine) {
            synchronized (m_openTargetDataLines) {
                m_openTargetDataLines.remove(line);
            }
        }
    }
}


/* TMixer.java */

