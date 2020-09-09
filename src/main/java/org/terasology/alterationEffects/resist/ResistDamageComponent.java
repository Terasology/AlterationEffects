// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.resist;

import org.terasology.engine.entitySystem.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This component is used for keeping track of the various resist damage effects an entity can have currently in
 * effect.
 */
public class ResistDamageComponent implements Component {
    /**
     * This map keeps track of the various damage resist effects currently in effect.
     */
    public Map<String, ResistDamageEffect> rdes = new HashMap<String, ResistDamageEffect>();
}
