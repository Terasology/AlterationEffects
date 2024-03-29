// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * This is the component added to entities with the item use speed effect.
 */
public class ItemUseSpeedComponent implements Component<ItemUseSpeedComponent> {
    /** This will affect how much the base item use speed is added by. */
    public float modifier; // TODO: Won't work until AlterationEffects has something else in addition to magnitude.

    /**
     * This affects how much the base item use speed is multiplied by. 1 is normal speed, 0 is immobility, and 2 is
     * double speed.
     */
    public float multiplier;

    @Override
    public void copyFrom(ItemUseSpeedComponent other) {
        this.modifier = other.modifier;
        this.multiplier = other.multiplier;
    }
}
