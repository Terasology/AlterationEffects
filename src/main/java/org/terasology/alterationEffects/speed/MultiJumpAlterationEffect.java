/*
 * Copyright 2016 MovingBlocks
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

public class MultiJumpAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public MultiJumpAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Multi Jump effect on the entity - gives the ability to jump in mid-air
     *
     * @param instigator the entity which applied the Multi Jump effect
     * @param entity the entity the Multi Jump effect is applied on
     * @param magnitude the number of times the entity can jump in mid-air without landing
     * @param duration the duration for which the Multi Jump effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        boolean add = false;
        MultiJumpComponent multiJump = entity.getComponent(MultiJumpComponent.class);

        if (multiJump == null) {
            add = true;
            multiJump = new MultiJumpComponent();
        }

        multiJump.multiplier = magnitude;

        if (add) {
            entity.addComponent(multiJump);
        } else {
            entity.saveComponent(multiJump);
        }

        delayManager.addDelayedAction(entity,
                AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.MULTI_JUMP, duration);
    }

    /**
     * Applies the Multi Jump effect on the entity - gives the ability to jump in mid-air
     *
     * @param instigator the entity which applied the Multi Jump effect
     * @param entity the entity the Multi Jump effect is applied on
     * @param id inapplicable to the Multi Jump effect
     * @param magnitude the number of times the entity can jump in mid-air without landing
     * @param duration the duration for which the Multi Jump effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
