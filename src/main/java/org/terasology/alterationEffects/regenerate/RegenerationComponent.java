// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.regenerate;

import org.terasology.engine.entitySystem.Component;

/**
 * This component is used for storing the health regeneration values an entity with the regeneration effect applied
 * has.
 */
public class RegenerationComponent implements Component {
    /**
     * The amount of healing that will be applied to the entity per tick.
     */
    public int regenerationAmount;

    /**
     * The last time this entity regenerated health via the regen effect.
     */
    public long lastRegenerationTime;
}
