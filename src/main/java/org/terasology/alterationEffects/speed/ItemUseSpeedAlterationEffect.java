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
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

public class ItemUseSpeedAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public ItemUseSpeedAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        ItemUseSpeedComponent itemUseSpeed = entity.getComponent(ItemUseSpeedComponent.class);
        if (itemUseSpeed == null) {
            itemUseSpeed = new ItemUseSpeedComponent();
            itemUseSpeed.multiplier = magnitude;
            entity.addComponent(itemUseSpeed);
        } else {
            itemUseSpeed.multiplier = magnitude;
            entity.saveComponent(itemUseSpeed);
        }

        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this, ""));
        long modifiedDuration = 0;

        if (!effectModifyEvent.isConsumed()) {
            float modifiedMagnitude = effectModifyEvent.getMagnitudeResultValue();
            modifiedDuration = effectModifyEvent.getShortestDuration();

            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                itemUseSpeed.multiplier = modifiedMagnitude;
            }
        }

        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.ITEM_USE_SPEED + "|" + effectID, modifiedDuration);
        } else if (duration != AlterationEffects.DURATION_INDEFINITE) {
            entity.removeComponent(ItemUseSpeedComponent.class);
        }
        /*
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

        if (duration != AlterationEffects.DURATION_INDEFINITE) {
            delayManager.addDelayedAction(entity,AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.ITEM_USE_SPEED, duration);
        }
        */
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String effectID, String id, float magnitude, long duration) {

    }
}
