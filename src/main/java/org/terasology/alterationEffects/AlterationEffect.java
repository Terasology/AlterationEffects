// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects;

import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Interface for the application of various alteration effects (with our without an ID) over a period of time.
 */
public interface AlterationEffect {
    /**
     * This will apply an effect on the given entity for the specified magnitude on a given duration of time.
     *
     * @param instigator The entity who applied the effect.
     * @param entity The entity that the effect is being applied on.
     * @param magnitude The magnitude of the effect.
     * @param duration The duration of the effect.
     */
    void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration);

    /**
     * This will apply an effect (sub-typed with an ID) on the given entity for the specified magnitude on a given
     * duration of time.
     *
     * @param instigator The entity who applied the effect.
     * @param entity The entity that the effect is being applied on.
     * @param id The ID of this effect. This is used for determining what sub-type this effect is part of. Only
     *         applicable to effects that support sub-types.
     * @param magnitude The magnitude of the effect.
     * @param duration The duration of the effect.
     */
    void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration);
}
