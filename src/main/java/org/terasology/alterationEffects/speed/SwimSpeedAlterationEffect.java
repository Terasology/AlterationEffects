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
package org.terasology.alterationEffects.speed;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

public class SwimSpeedAlterationEffect implements AlterationEffect {

    private final DelayManager delayManager;

    public SwimSpeedAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Swim Speed effect on the entity - affects the swimming speed of the entity
     *
     * @param instigator the entity which applied the Swim Speed effect
     * @param entity the entity the Swim Speed effect is applied on
     * @param magnitude the multiplier applied to the swimming speed
     * @param duration the duration for which the Swim Speed effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        boolean add = false;
        SwimSpeedComponent swimSpeed = entity.getComponent(SwimSpeedComponent.class);
        if (swimSpeed == null) {
            add = true;
            swimSpeed = new SwimSpeedComponent();
        }
        swimSpeed.multiplier = magnitude;

        if (add) {
            entity.addComponent(swimSpeed);
        } else {
            entity.saveComponent(swimSpeed);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.SWIM_SPEED, duration);
    }

    /**
     * Applies the Swim Speed effect on the entity - affects the swimming speed of the entity
     *
     * @param instigator the entity which applied the Swim Speed effect
     * @param entity the entity the Swim Speed effect is applied on
     * @param id inapplicable to the Swim Speed effect
     * @param magnitude the multiplier applied to the swimming speed
     * @param duration the duration for which the Swim Speed effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
