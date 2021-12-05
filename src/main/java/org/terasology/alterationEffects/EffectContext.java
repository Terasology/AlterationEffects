// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects;

import org.terasology.engine.entitySystem.entity.EntityRef;

/**
 * Immutable context for implementing alteration effects.
 * <p>
 * This data class holds all information that is available when applying an alteration effect. It encapsulates the
 * arguments passed to {@link AlterationEffect#applyEffect(EntityRef, EntityRef, String, float, long)}.
 * <p>
 * The purpose of this class is to be able to pass fewer arguments to hook methods of {@link
 * ComponentBasedAlterationEffect}.
 */
public class EffectContext {
    /**
     * The entity causing the effect.
     */
    public final EntityRef instigator;
    /**
     * The entity the effect is applied to.
     */
    public final EntityRef entity;
    /**
     * The identifier for the effect.
     * <p>
     * May be null or the empty string.
     */
    public final String id;
    /**
     * The initially requested magnitude of the effect.
     */
    public final float magnitude;
    /**
     * The initially requested duration.
     * <p>
     * {@link AlterationEffects#DURATION_INDEFINITE} for indefinite duration.
     */
    public final long duration;

    /**
     * Create an effect context from the given arguments.
     *
     * @param instigator the entity causing the effect
     * @param entity the entity the effect is applied to
     * @param id the identifier of the effect, may be null or empty
     * @param magnitude the initially requested magnitude of the effect
     * @param duration the initially requested duration of the effect
     */
    public EffectContext(final EntityRef instigator, final EntityRef entity, final String id, final float magnitude,
                         final long duration) {
        this.instigator = instigator;
        this.entity = entity;
        this.id = id;
        this.magnitude = magnitude;
        this.duration = duration;
    }
}
