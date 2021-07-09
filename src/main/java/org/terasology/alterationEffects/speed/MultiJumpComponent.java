// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This is the component added to entities with the multi jump effect.
 */
public class MultiJumpComponent implements Component<MultiJumpComponent> {
    /** This will affect how much the base number of multi jumps is added by. */
    public float modifier; // TODO: Won't work until AlterationEffects has something else in addition to magnitude.

    /** This affects how much the base number of max jumps is multiplied by. */
    public float multiplier;

    @Override
    public void copy(MultiJumpComponent other) {
        this.modifier = other.modifier;
        this.multiplier = other.multiplier;
    }
}

