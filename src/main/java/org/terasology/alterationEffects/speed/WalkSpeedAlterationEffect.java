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

public class WalkSpeedAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public WalkSpeedAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Walk Speed effect on the entity - affects the walking speed of the entity
     *
     * @param instigator the entity which applied the Walk Speed effect
     * @param entity the entity the Walk Speed effect is applied on
     * @param magnitude the multiplier applied to the walking speed
     * @param duration the duration for which the Walk Speed effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        boolean add = false;
        WalkSpeedComponent walkSpeed = entity.getComponent(WalkSpeedComponent.class);
        if (walkSpeed == null) {
            add = true;
            walkSpeed = new WalkSpeedComponent();
        }
        walkSpeed.multiplier = magnitude;

        if (add) {
            entity.addComponent(walkSpeed);
        } else {
            entity.saveComponent(walkSpeed);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.WALK_SPEED, duration);
    }

    /**
     * Applies the Walk Speed effect on the entity - affects the walking speed of the entity
     *
     * @param instigator the entity which applied the Walk Speed effect
     * @param entity the entity the Walk Speed effect is applied on
     * @param magnitude the multiplier applied to the walking speed
     * @param duration the duration for which the Walk Speed effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
