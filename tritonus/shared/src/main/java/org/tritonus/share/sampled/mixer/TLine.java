/*
 * TLine.java
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

import org.tritonus.share.TDebug;
import org.tritonus.share.TNotifier;

import javax.sound.sampled.*;
import java.util.*;


/**
 * Base class for classes implementing Line.
 */
public abstract class TLine
        implements Line {
    private static final Control[] EMPTY_CONTROL_ARRAY = new Control[0];

    private Info m_info;
    private boolean m_bOpen;
    private final List<Control> m_controls;
    private final Set<LineListener> m_lineListeners;
    private TMixer m_mixer;


    protected TLine(TMixer mixer,
                    Info info) {
        setLineInfo(info);
        setOpen(false);
        m_controls = new ArrayList<>();
        m_lineListeners = new HashSet<>();
        m_mixer = mixer;
    }


    protected TLine(TMixer mixer,
                    Info info,
                    Collection<Control> controls) {
        this(mixer, info);
        m_controls.addAll(controls);
    }


    protected TMixer getMixer() {
        return m_mixer;
    }


    public Info getLineInfo() {
        return m_info;
    }


    protected void setLineInfo(Info info) {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.setLineInfo(): setting: " + info);
        }
        synchronized (this) {
            m_info = info;
        }
    }


    public void open()
            throws LineUnavailableException {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.open(): called");
        }
        if (!isOpen()) {
            if (TDebug.TraceLine) {
                TDebug.out("TLine.open(): opening");
            }
            openImpl();
            if (getMixer() != null) {
                getMixer().registerOpenLine(this);
            }
            setOpen(true);
        } else {
            if (TDebug.TraceLine) {
                TDebug.out("TLine.open(): already open");
            }
        }
    }


    /**
     * Subclasses should override this method.
     */
    protected void openImpl()
            throws LineUnavailableException {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.openImpl(): called");
        }
    }


    public void close() {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.close(): called");
        }
        if (isOpen()) {
            if (TDebug.TraceLine) {
                TDebug.out("TLine.close(): closing");
            }
            if (getMixer() != null) {
                getMixer().unregisterOpenLine(this);
            }
            closeImpl();
            setOpen(false);
        } else {
            if (TDebug.TraceLine) {
                TDebug.out("TLine.close(): not open");
            }
        }
    }


    /**
     * Subclasses should override this method.
     */
    protected void closeImpl() {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.closeImpl(): called");
        }
    }


    public boolean isOpen() {
        return m_bOpen;
    }


    protected void setOpen(boolean bOpen) {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.setOpen(): called, value: " + bOpen);
        }
        boolean bOldValue = isOpen();
        m_bOpen = bOpen;
        if (bOldValue != isOpen()) {
            if (isOpen()) {
                if (TDebug.TraceLine) {
                    TDebug.out("TLine.setOpen(): opened");
                }
                notifyLineEvent(LineEvent.Type.OPEN);
            } else {
                if (TDebug.TraceLine) {
                    TDebug.out("TLine.setOpen(): closed");
                }
                notifyLineEvent(LineEvent.Type.CLOSE);
            }
        }
    }


    protected void addControl(Control control) {
        synchronized (m_controls) {
            m_controls.add(control);
        }
    }


    protected void removeControl(Control control) {
        synchronized (m_controls) {
            m_controls.remove(control);
        }
    }


    public Control[] getControls() {
        synchronized (m_controls) {
            return m_controls.toArray(EMPTY_CONTROL_ARRAY);
        }
    }


    public Control getControl(Control.Type controlType) {
        synchronized (m_controls) {
            for (Control control : m_controls) {
                if (control.getType().equals(controlType)) {
                    return control;
                }
            }
            throw new IllegalArgumentException("no control of type " + controlType);
        }
    }


    public boolean isControlSupported(Control.Type controlType) {
        // TDebug.out("TLine.isSupportedControl(): called");
        try {
            return getControl(controlType) != null;
        } catch (IllegalArgumentException e) {
            if (TDebug.TraceAllExceptions) {
                TDebug.out(e);
            }
            // TDebug.out("TLine.isSupportedControl(): returning false");
            return false;
        }
    }


    public void addLineListener(LineListener listener) {
        // TDebug.out("%% TChannel.addListener(): called");
        synchronized (m_lineListeners) {
            m_lineListeners.add(listener);
        }
    }


    public void removeLineListener(LineListener listener) {
        synchronized (m_lineListeners) {
            m_lineListeners.remove(listener);
        }
    }


    private Set<LineListener> getLineListeners() {
        synchronized (m_lineListeners) {
            return new HashSet<>(m_lineListeners);
        }
    }


    // is overridden in TDataLine to provide a position
    protected void notifyLineEvent(LineEvent.Type type) {
        notifyLineEvent(new LineEvent(this, type, AudioSystem.NOT_SPECIFIED));
    }


    protected void notifyLineEvent(LineEvent event) {
        // TDebug.out("%% TChannel.notifyChannelEvent(): called");
        // Channel.Event event = new Channel.Event(this, type, getPosition());
        TNotifier.notifier.addEntry(event, getLineListeners());
    }
}


/* TLine.java */
