// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.buff;

import org.terasology.gestalt.entitysystem.component.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This component is used for keeping track of the various buff damage effects an entity can have currently in effect.
 */
public class BuffDamageComponent implements Component<BuffDamageComponent> {
    /** This map keeps track of the various damage buff effects currently in effect. */
    public Map<String, BuffDamageEffect> bdes = new HashMap<String, BuffDamageEffect>();

    @Override
    public void copy(BuffDamageComponent other) {
        bdes.clear();
        other.bdes.forEach((k, v)-> this.bdes.put(k, v.copy()));
    }
}
