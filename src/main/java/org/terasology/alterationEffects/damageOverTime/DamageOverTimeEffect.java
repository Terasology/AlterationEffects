// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.damageOverTime;

import org.terasology.reflection.MappedContainer;

/**
 * This class is used for storing a damage over time (DOT) effect.
 */
@MappedContainer
public class DamageOverTimeEffect {
    /**
     * The type of damage that will be inflicted.
     */
    public String damageType;

    /**
     * The amount of damage that will be inflicted upon on the entity per tick.
     */
    public int damageAmount;

    /**
     * The last time this effect dealt damage.
     */
    public long lastDamageTime;

    public DamageOverTimeEffect copy() {
        DamageOverTimeEffect dotEffect = new DamageOverTimeEffect();
        dotEffect.damageType = this.damageType;
        dotEffect.damageAmount = this.damageAmount;
        dotEffect.lastDamageTime = this.lastDamageTime;
        return dotEffect;
    }
}
