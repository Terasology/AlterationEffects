/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.alterationEffects;

import org.terasology.entitySystem.entity.EntityRef;

/**
 * A general interface for an alteration effect.
 */
public interface AlterationEffect {
    /**
     * Apply an effect to an entity.
     *
     * @param instigator The entity applying the effect
     * @param entity     The entity to which the effect is being applied
     * @param magnitude  The magnitude of the effect
     * @param duration   The duration of the effect
     */
    void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration);

    /**
     * Apply an effect with a specific ID to an entity.
     *
     * @param instigator The instigator of the action
     * @param entity     The entity to which the effect is being applied
     * @param id         The ID of the effect
     * @param magnitude  The magnitude of the effect
     * @param duration   The duration of the effect
     */
    void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration);
}
