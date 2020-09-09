// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.engine.entitySystem.Component;

/**
 * This is the component added to entities with the swim speed effect.
 */
public class SwimSpeedComponent implements Component {
    /**
     * This affects how much the base swim speed is multiplied by. 1 is normal speed, 0 is immobility, and 2 is double
     * speed.
     */
    public float multiplier;
}
