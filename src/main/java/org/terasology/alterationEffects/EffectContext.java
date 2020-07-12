// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.alterationEffects;

import org.terasology.entitySystem.entity.EntityRef;

/**
 * Immutable context for implementing alteration effects.
 */
public class EffectContext {
    public final EntityRef instigator;
    public final EntityRef entity;
    public final String id;
    public final float magnitude;
    public final long duration;

    public EffectContext(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        this.instigator = instigator;
        this.entity = entity;
        this.id = id;
        this.magnitude = magnitude;
        this.duration = duration;
    }
}
