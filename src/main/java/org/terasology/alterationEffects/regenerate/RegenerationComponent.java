// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.regenerate;

import com.google.common.collect.Maps;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.naming.Name;

import java.util.HashMap;
import java.util.Map;

/**
 * This component is used for storing the health regeneration values an entity with the regeneration effect applied has.
 */
public class RegenerationComponent implements Component<RegenerationComponent> {
    /**
     * The amount of healing that will be applied to the entity per tick.
     */
    public int regenerationAmount;

    /**
     * The last time this entity regenerated health via the regen effect.
     */
    public long lastRegenerationTime;

    public Map<Name, Integer> activeRegenerations = new HashMap<>();

    @Override
    public void copyFrom(RegenerationComponent other) {
        this.regenerationAmount = other.regenerationAmount;
        this.lastRegenerationTime = other.lastRegenerationTime;
        this.activeRegenerations = Maps.newHashMap(other.activeRegenerations);
    }
}
