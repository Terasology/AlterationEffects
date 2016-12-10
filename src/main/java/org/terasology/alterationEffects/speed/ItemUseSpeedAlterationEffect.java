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

public class ItemUseSpeedAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public ItemUseSpeedAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Item Use Speed effect on the entity - affects the cooldowns of items
     *
     * @param instigator the entity which applied the Item Use Speed effect
     * @param entity the entity the Item Use Speed effect is applied on
     * @param magnitude the multiplier applied to item cooldowns
     * @param duration the duration for which the Item Use Speed effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        boolean add = false;
        ItemUseSpeedComponent jumpSpeed = entity.getComponent(ItemUseSpeedComponent.class);

        if (jumpSpeed == null) {
            add = true;
            jumpSpeed = new ItemUseSpeedComponent();
        }

        jumpSpeed.multiplier = magnitude;

        if (add) {
            entity.addComponent(jumpSpeed);
        } else {
            entity.saveComponent(jumpSpeed);
        }

        delayManager.addDelayedAction(entity,
                AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.ITEM_USE_SPEED, duration);
    }

    /**
     * Applies the Item Use Speed effect on the entity - affects the cooldowns of items
     *
     * @param instigator the entity which applied the Item Use Speed effect
     * @param entity the entity the Item Use Speed effect is applied on
     * @param id inapplicable to the Item Use Speed effect
     * @param magnitude the multiplier applied to item cooldowns
     * @param duration the duration for which the Item Use Speed effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
