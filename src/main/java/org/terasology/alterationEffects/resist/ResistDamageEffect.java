// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.resist;

import org.terasology.reflection.MappedContainer;

/**
 * This class is used for storing a damage type resistance.
 */
@MappedContainer
public class ResistDamageEffect {
    /**
     * The damage type that will be resisted.
     */
    public String resistType;

    /**
     * The amount of damage that'll be resisted (i.e. subtracted).
     */
    public int resistAmount;
}
