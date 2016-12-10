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
package org.terasology.alterationEffects.regenerate;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.math.TeraMath;

/**
 * This class defines an effect which gives a health regeneration buff to the entity to which it is applied.
 */
public class RegenerationAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    /**
     * Parametrized constructor.
     *
     * @param context A Context object representing the state before the boost
     */
    public RegenerationAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the regeneration effect to an entity.
     *
     * @param instigator The entity applying the effect
     * @param entity     The entity to which the effect is being applied
     * @param magnitude  The magnitude of the effect
     * @param duration   The duration of the effect
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        RegenerationComponent regeneration = entity.getComponent(RegenerationComponent.class);
        if (regeneration == null) {
            regeneration = new RegenerationComponent();
            regeneration.regenerationAmount = TeraMath.floorToInt(magnitude);
            regeneration.lastRegenerationTime = time.getGameTimeInMs();
            entity.addComponent(regeneration);
        } else {
            regeneration.regenerationAmount = TeraMath.floorToInt(magnitude);
            regeneration.lastRegenerationTime = time.getGameTimeInMs();
            entity.addComponent(regeneration);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.REGENERATION, duration);
    }

    /**
     * Applies the regeneration effect to an entity.
     *
     * @param instigator The instigator of the action
     * @param entity     The entity to which the effect is being applied
     * @param id         The ID of the effect
     * @param magnitude  The magnitude of the effect
     * @param duration   The duration of the effect
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
