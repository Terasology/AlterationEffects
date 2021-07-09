// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.damageOverTime;

import com.google.common.collect.Maps;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This component is used for keeping track of the various damage over time (DOT) effects an entity can have currently
 * in effect.
 */
public class DamageOverTimeComponent implements Component<DamageOverTimeComponent> {
    /** This map keeps track of the various DOT effects currently in effect. */
    public Map<String, DamageOverTimeEffect> dots = new HashMap<String, DamageOverTimeEffect>();

    /** This map keeps track of all the effectIDs of all the current DOT effects being applied to an entity. */
    public Map<String, Map<String, Boolean>> effectIDMap = new HashMap<String, Map<String, Boolean>>();

    @Override
    public void copy(DamageOverTimeComponent other) {
        dots.clear();
        other.dots.forEach((k, v) -> this.dots.put(k, v.copy()));
        effectIDMap.clear();
        other.effectIDMap.forEach((k, v) -> this.effectIDMap.put(k, Maps.newHashMap(v)));
    }
}
