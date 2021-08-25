// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.gestalt.entitysystem.component.Component;

/** Meant to represent a stickying effect of some sort, as if stuck in a spiderweb. */
public class GlueComponent implements Component<GlueComponent> {
    /** This affects how strong the glue effect is by multiplying against the base value. */
    public float multiplier;

    @Override
    public void copyFrom(GlueComponent other) {
        this.multiplier = other.multiplier;
    }
}
