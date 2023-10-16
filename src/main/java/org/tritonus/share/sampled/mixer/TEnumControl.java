/*
 * TEnumControl.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2001 by Matthias Pfisterer
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
import org.tritonus.share.sampled.mixer.TCompoundControl;
import org.tritonus.share.sampled.mixer.TControlController;
import org.tritonus.share.sampled.mixer.TControllable;

import javax.sound.sampled.EnumControl;


/**
 * Base class for classes implementing Line.
 */
public class TEnumControl
        extends EnumControl
        implements TControllable {
    private org.tritonus.share.sampled.mixer.TControlController m_controller;


    public TEnumControl(Type type,
                        Object[] aValues,
                        Object value) {
        super(type,
                aValues,
                value);
        if (TDebug.TraceControl) {
            TDebug.out("TEnumControl.<init>: begin");
        }
        m_controller = new TControlController();
        if (TDebug.TraceControl) {
            TDebug.out("TEnumControl.<init>: end");
        }
    }


    public void setParentControl(org.tritonus.share.sampled.mixer.TCompoundControl compoundControl) {
        m_controller.setParentControl(compoundControl);
    }


    public TCompoundControl getParentControl() {
        return m_controller.getParentControl();
    }


    public void commit() {
        m_controller.commit();
    }
}


/* TEnumControl.java */
