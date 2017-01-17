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

/**
 * This handles the application of the item use speed effect, which speeds up an entity's item use rate (based on the
 * magnitude) for a specified duration.
 */
public class ItemUseSpeedAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context       The context which this effect will be executed on.
     */
    public ItemUseSpeedAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * This will apply the item use speed effect on the given entity. This method will send out an event to the other
     * applicable effect systems so that they can contribute with their own item use speed effect related modifiers.
     *
     * @param instigator    The entity who applied the item use speed effect.
     * @param entity        The entity that the item use speed effect is being applied on.
     * @param magnitude     The magnitude of the item use speed effect.
     * @param duration      The duration of the item use speed effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        // First, determine if the entity already has a item use speed component attached. If so, just replace the
        // speed multiplier, and then save the component. Otherwise, create a new one and attach it to the entity.
        ItemUseSpeedComponent itemUseSpeed = entity.getComponent(ItemUseSpeedComponent.class);
        if (itemUseSpeed == null) {
            itemUseSpeed = new ItemUseSpeedComponent();
            itemUseSpeed.multiplier = magnitude;
            entity.addComponent(itemUseSpeed);
        } else {
            itemUseSpeed.multiplier = magnitude;
            entity.saveComponent(itemUseSpeed);
        }

        // Send out this event to collect all the duration and magnitude modifiers and multipliers that can affect this
        // item use speed effect.
        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this, ""));
        long modifiedDuration = 0;
        boolean modifiersFound = false;

        // If the effect modify event is consumed, don't apply this item use speed effect.
        if (!effectModifyEvent.isConsumed()) {
            /*
            Get the magnitude result value and the shortest duration, and assign them to the modifiedMagnitude and
            modifiedDuration respectively.

            The shortest duration is used as the effect modifier associated with that will expire in the shortest
            amount of time, meaning that this effect's total magnitude and next remaining duration will have to be
            recalculated.
            */
            float modifiedMagnitude = effectModifyEvent.getMagnitudeResultValue();
            modifiedDuration = effectModifyEvent.getShortestDuration();

            // If there's at least one duration and magnitude modifier, set the effect's magnitude and the modifiersFound flag.
            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                itemUseSpeed.multiplier = modifiedMagnitude;
                modifiersFound = true;
            }
        }

        // If the modified duration is between the accepted values (0 and Long.MAX_VALUE), and the base duration is not infinite,
        // add a delayed action to the DelayManager using the new system.
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.ITEM_USE_SPEED + "|" + effectID, modifiedDuration);
        }
        // Otherwise, if the duration is greater than 0, there are no modifiers found, and the effect modify event was not consumed,
        // add a delayed action to the DelayManager using the old system.
        else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.ITEM_USE_SPEED, duration);
        }
        // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event have infinite
        // duration, remove the component associated with this item use speed effect.
        else if (!modifiersFound || !effectModifyEvent.getHasInfDuration()) {
            entity.removeComponent(ItemUseSpeedComponent.class);
        }
        // If this point is reached and none of the above if-clauses were met, that means there was at least one modifier
        // collected in the event which has infinite duration.
    }

    /**
     * This will apply the item use speed effect on the given entity by calling the method
     * {@link #applyEffect(EntityRef, EntityRef, float, long)}.
     *
     * @param instigator    The entity who applied the item use speed effect.
     * @param entity        The entity that the item use speed effect is being applied on.
     * @param id            Inapplicable to the item use speed effect.
     * @param magnitude     The magnitude of the item use speed effect.
     * @param duration      The duration of the item use speed effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String effectID, String id, float magnitude, long duration) {

    }
}
