/*
 * TMixerProvider.java
 *
 * This file is part of Tritonus: http://www.tritonus.org/
 */

/*
 *  Copyright (c) 1999 - 2004 by Matthias Pfisterer
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

import org.tritonus.share.TDebug;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;
import java.util.*;


public abstract class TMixerProvider
        extends MixerProvider {
    private static final Mixer.Info[] EMPTY_MIXER_INFO_ARRAY = new Mixer.Info[0];

    private static Map<Class<?>, MixerProviderStruct> sm_mixerProviderStructs = new HashMap<>();

    private boolean m_bDisabled = false;


    public TMixerProvider() {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.<init>(): begin");
        }
        // currently does nothing
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.<init>(): end");
        }
    }


    /*
      Override this method if you want a thread-safe static initializaiton.
     */
    protected void staticInit() {
    }


    private MixerProviderStruct getMixerProviderStruct() {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixerProviderStruct(): begin");
        }
        Class<?> cls = this.getClass();
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixerProviderStruct(): called from " + cls);
        }
        // Thread.dumpStack();
        synchronized (TMixerProvider.class) {
            MixerProviderStruct struct = sm_mixerProviderStructs.get(cls);
            if (struct == null) {
                if (TDebug.TraceMixerProvider) {
                    TDebug.out("TMixerProvider.getMixerProviderStruct(): creating new MixerProviderStruct for " + cls);
                }
                struct = new MixerProviderStruct();
                sm_mixerProviderStructs.put(cls, struct);
            }
            if (TDebug.TraceMixerProvider) {
                TDebug.out("TMixerProvider.getMixerProviderStruct(): end");
            }
            return struct;
        }
    }


    protected void disable() {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("disabling " + getClass().getName());
        }
        m_bDisabled = true;
    }


    protected boolean isDisabled() {
        return m_bDisabled;
    }


    protected void addMixer(Mixer mixer) {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.addMixer(): begin");
        }
        MixerProviderStruct struct = getMixerProviderStruct();
        synchronized (struct) {
            struct.m_mixers.add(mixer);
            if (struct.m_defaultMixer == null) {
                struct.m_defaultMixer = mixer;
            }
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.addMixer(): end");
        }
    }


    protected void removeMixer(Mixer mixer) {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.removeMixer(): begin");
        }
        MixerProviderStruct struct = getMixerProviderStruct();
        synchronized (struct) {
            struct.m_mixers.remove(mixer);
            // TODO: should search for another mixer
            if (struct.m_defaultMixer == mixer) {
                struct.m_defaultMixer = null;
            }
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.removeMixer(): end");
        }
    }


    // $$mp 2003/01/11: TODO: this implementation may become obsolete once the overridden method in spi.MixerProvider is implemented in a way documented officially.
    public boolean isMixerSupported(Mixer.Info info) {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.isMixerSupported(): begin");
        }
        boolean bIsSupported = false;
        Mixer.Info[] infos = getMixerInfo();
        for (Mixer.Info value : infos) {
            if (value.equals(info)) {
                bIsSupported = true;
                break;
            }
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.isMixerSupported(): end");
        }
        return bIsSupported;
    }


    /**
     *
     */
    public Mixer getMixer(Mixer.Info info) {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixer(): begin");
        }
        MixerProviderStruct struct = getMixerProviderStruct();
        Mixer mixerResult = null;
        synchronized (struct) {
            if (info == null) {
                mixerResult = struct.m_defaultMixer;
            } else {
                for (Mixer mixer : struct.m_mixers) {
                    if (mixer.getMixerInfo().equals(info)) {
                        mixerResult = mixer;
                        break;
                    }
                }
            }
        }
        if (mixerResult == null) {
            throw new IllegalArgumentException("no mixer available for " + info);
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixer(): end");
        }
        return mixerResult;
    }


    public Mixer.Info[] getMixerInfo() {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixerInfo(): begin");
        }
        Set<Mixer.Info> mixerInfos = new HashSet<>();
        MixerProviderStruct struct = getMixerProviderStruct();
        synchronized (struct) {
            for (Mixer mixer : struct.m_mixers) {
                mixerInfos.add(mixer.getMixerInfo());
            }
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixerInfo(): end");
        }
        return mixerInfos.toArray(EMPTY_MIXER_INFO_ARRAY);
    }


    private static class MixerProviderStruct {
        public List<Mixer> m_mixers;
        public Mixer m_defaultMixer;


        public MixerProviderStruct() {
            m_mixers = new ArrayList<>();
            m_defaultMixer = null;
        }
    }
}


/* TMixerProvider.java */
