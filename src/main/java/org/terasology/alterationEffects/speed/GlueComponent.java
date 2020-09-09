// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.engine.entitySystem.Component;

/**
 * Meant to represent a stickying effect of some sort, as if stuck in a spiderweb.
 */
public class GlueComponent implements Component {
    /**
     * This affects how strong the glue effect is by multiplying against the base value.
     */
    public float multiplier;
}
