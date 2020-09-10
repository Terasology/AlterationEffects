// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.buff;

import org.terasology.nui.reflection.MappedContainer;

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
     * The amount that the damage of this type will be additively increased or buffed by.
     */
    public int buffAmount;
}
