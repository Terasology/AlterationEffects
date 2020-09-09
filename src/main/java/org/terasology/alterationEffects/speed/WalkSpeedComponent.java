// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.engine.entitySystem.Component;

/**
 * This is the component added to entities with the walk speed effect.
 */
public class WalkSpeedComponent implements Component {
    /**
     * This affects how much the base walk speed is multiplied by. 1 is normal speed, 0 is immobility, and 2 is double
     * speed.
     */
    public float multiplier;
}
