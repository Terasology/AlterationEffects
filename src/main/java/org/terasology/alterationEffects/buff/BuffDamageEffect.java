// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.buff;

import org.terasology.reflection.MappedContainer;

/**
 * This class is used for storing a damage type buff or buff.
 */
@MappedContainer
public class BuffDamageEffect {
    /**
     * The damage type that will be buffed.
     */
    public String damageType;

    /**
     * The amount that the damage to this type will be additively increased or buffed by.
     */
    public int buffAmount;

    BuffDamageEffect copy() {
        BuffDamageEffect newBuffEffect = new BuffDamageEffect();
        newBuffEffect.damageType = this.damageType;
        newBuffEffect.buffAmount = this.buffAmount;
        return newBuffEffect;
    }
}
