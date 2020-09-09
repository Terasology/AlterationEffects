// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.damageOverTime;

import org.terasology.engine.entitySystem.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This component is used for keeping track of the various damage over time (DOT) effects an entity can have currently
 * in effect.
 */
public class DamageOverTimeComponent implements Component {
    /**
     * This map keeps track of the various DOT effects currently in effect.
     */
    public Map<String, DamageOverTimeEffect> dots = new HashMap<String, DamageOverTimeEffect>();

    /**
     * This map keeps track of all the effectIDs of all the current DOT effects being applied to an entity.
     */
    public Map<String, Map<String, Boolean>> effectIDMap = new HashMap<String, Map<String, Boolean>>();
}
