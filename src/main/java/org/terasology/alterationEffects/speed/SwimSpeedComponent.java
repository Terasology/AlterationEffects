// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This is the component added to entities with the swim speed effect.
 */
public class SwimSpeedComponent implements Component<SwimSpeedComponent> {
    /**
     * This affects how much the base swim speed is multiplied by. 1 is normal speed, 0 is immobility, and 2 is double
     * speed.
     */
    public float multiplier;

    @Override
    public void copyFrom(SwimSpeedComponent other) {
        this.multiplier = other.multiplier;
    }
}
