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
package org.terasology.alterationEffects.boost;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.engine.context.Context;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.math.TeraMath;

/**
 * This handles the application of the health boost effect, which boosts an entity's maximum health (based on the
 * magnitude) for a specified duration.
 */
public class HealthBoostAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the time and DelayManager.
     *
     * @param context       The context which this effect will be executed on.
     */
    public HealthBoostAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Removes the effect of the given health boost from the entity.
     *
     * @param entity        The entity which has the health boost effect.
     * @param hBoost        The health boost component.
     */
    private void removeBoost(EntityRef entity, HealthBoostComponent hBoost) {
        HealthComponent h = entity.getComponent(HealthComponent.class);

        // Reverse the max health boosting effect by dividing the old boost amount.
        h.maxHealth = Math.round(h.maxHealth / (1f + 0.01f*hBoost.boostAmount));

        // If the current health is greater than the new max health, set the current health value to be the max health.
        if (h.currentHealth > h.maxHealth) {
            h.currentHealth = h.maxHealth;
        }
    }

    /**
     * This will apply the health boost effect on the given entity. This method will send out an event to the other
     * applicable effect systems so that they can contribute with their own health boost effect related modifiers.
     *
     * @param instigator    The entity who applied the health boost effect.
     * @param entity        The entity that the health boost effect is being applied on.
     * @param magnitude     The magnitude of the health boost effect.
     * @param duration      The duration of the health boost effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        // First, determine if the entity already has a health boost component attached. If so, just update the boost
        // amount. Otherwise, create a new one and attach it to the entity.
        HealthBoostComponent hbot = entity.getComponent(HealthBoostComponent.class);
        if (hbot == null) {
            hbot = new HealthBoostComponent();
            hbot.boostAmount = TeraMath.floorToInt(magnitude);
            entity.addComponent(hbot);
        } else {
            // Remove the current health boost in place.
            removeBoost(entity, hbot);
            hbot.boostAmount = TeraMath.floorToInt(magnitude);
        }

        // Send out this event to collect all the duration and magnitude modifiers and multipliers that can affect this
        // health boost effect.
        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this, ""));
        long modifiedDuration = 0;
        boolean modifiersFound = false;

        // If the effect modify event is consumed, don't apply this health boost effect.
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

            // If there's at least one duration and magnitude modifier, set the effect's boost amount and the
            // modifiersFound flag.
            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                hbot.boostAmount = (int) modifiedMagnitude;
                modifiersFound = true;
            }
        }

        hbot.lastUseTime = time.getGameTimeInMs();

        // Get the health component of this entity, and increase its max health using the health boost multiplier.
        HealthComponent h = entity.getComponent(HealthComponent.class);
        h.maxHealth = Math.round(h.maxHealth * (1 + 0.01f * hbot.boostAmount));

        // Save the component so the latest changes to it don't get lost when the game's exited.
        entity.saveComponent(hbot);

        // If the modified duration is between the accepted values (0 and Long.MAX_VALUE), and the base duration is not infinite,
        // add a delayed action to the DelayManager using the new system.
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.MAX_HEALTH_BOOST + "|" + effectID, modifiedDuration);
        }
        // Otherwise, if the duration is greater than 0, there are no modifiers found, and the effect modify event was not consumed,
        // add a delayed action to the DelayManager using the old system.
        else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.MAX_HEALTH_BOOST, duration);
        }
        // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event have infinite
        // duration, remove the component associated with this health boost effect.
        else if (!modifiersFound || !effectModifyEvent.getHasInfDuration()) {
            entity.removeComponent(HealthBoostComponent.class);
        }
        // If this point is reached and none of the above if-clauses were met, that means there was at least one modifier
        // collected in the event which has infinite duration.
    }

    /**
     * This will apply the health boost effect on the given entity by calling the method
     * {@link #applyEffect(EntityRef, EntityRef, float, long)}.
     *
     * @param instigator    The entity who applied the health boost effect.
     * @param entity        The entity that the health boost effect is being applied on.
     * @param id            Inapplicable to the health boost effect.
     * @param magnitude     The magnitude of the health boost effect.
     * @param duration      The duration of the health boost effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
