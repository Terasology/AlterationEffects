// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.resist;

import org.terasology.gestalt.entitysystem.component.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This component is used for keeping track of the various resist damage effects an entity can have currently in effect.
 */
public class ResistDamageComponent implements Component<ResistDamageComponent> {
    /** This map keeps track of the various damage resist effects currently in effect. */
    public Map<String, ResistDamageEffect> rdes = new HashMap<>();

    @Override
    public void copyFrom(ResistDamageComponent other) {
        this.rdes.clear();
        for (Map.Entry<String, ResistDamageEffect> entry : other.rdes.entrySet()) {
            ResistDamageEffect value = entry.getValue();
            ResistDamageEffect resistDamageEffect = new ResistDamageEffect();
            resistDamageEffect.resistAmount = value.resistAmount;
            resistDamageEffect.resistType = value.resistType;
            this.rdes.put(entry.getKey(), resistDamageEffect);
        }
    }
}
