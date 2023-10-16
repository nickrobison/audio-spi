/*
 * TCompoundControlType.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 2001 by Matthias Pfisterer
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

package org.tritonus.share.sampled.mixer;

import javax.sound.sampled.CompoundControl;


/**
 * CompoundControl.Type class.
 * This class is only needed to provide a public constructor.
 */
public class TCompoundControlType
        extends CompoundControl.Type {
    /**
     * Constructor.
     * Constructs a CompoundControl.Type with the
     * name given.
     *
     * @param strName The name of the control.
     */
    public TCompoundControlType(String strName) {
        super(strName);
    }
}


/* TCompoundControlType.java */
