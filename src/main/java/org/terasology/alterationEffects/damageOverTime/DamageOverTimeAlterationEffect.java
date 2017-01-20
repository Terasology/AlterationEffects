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
package org.terasology.alterationEffects.damageOverTime;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.alterationEffects.speed.WalkSpeedComponent;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.math.TeraMath;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This handles the application of the damage over time (or DOT) effect, which deals damage to an entity for the given
 * magnitude per second for a specified duration.
 */
public class DamageOverTimeAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager and current time.
     *
     * @param context       The context which this effect will be executed on.
     */
    public DamageOverTimeAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * This will apply the damage over time (DOT) effect on the given entity by calling the method
     * {@link #applyEffect(EntityRef, EntityRef, String, float, long)} with the ID being set to "Default".
     *
     * @param instigator    The entity who applied the damage over time effect.
     * @param entity        The entity that the damage over time effect is being applied on.
     * @param magnitude     The magnitude of the damage over time effect.
     * @param duration      The duration of the damage over time effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "Default", magnitude, duration);
    }

    /**
     * This will apply the damage over time (DOT) effect on the given entity. This method will send out an event to the
     * other applicable effect systems so that they can contribute with their own resist damage effect related
     * modifiers.
     *
     * @param instigator    The entity who applied the damage over time effect.
     * @param entity        The entity that the damage over time effect. is being applied on.
     * @param id            The ID of this damage over time effect. This is used for determining what damage type to
     *                      use or inflict on the user. For example, fire, poison, etc.
     * @param magnitude     The magnitude of the damage over time effect.
     * @param duration      The duration of the damage over time effect.
     */
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        // First, determine if the entity already has a DOT component attached. If so, just replace the damage amount
        // and last damage time, then save the component. Otherwise, create a new one and attach it to the entity.
        DamageOverTimeComponent dot = entity.getComponent(DamageOverTimeComponent.class);
        if (dot == null) {
            dot = new DamageOverTimeComponent();
            //dot.damageAmount = TeraMath.floorToInt(magnitude);
            //dot.lastDamageTime = time.getGameTimeInMs();
            entity.addComponent(dot);
        } else {
            //dot.damageAmount = TeraMath.floorToInt(magnitude);
            //dot.lastDamageTime = time.getGameTimeInMs();
            entity.saveComponent(dot);
        }

        // Create a new DOT effect instance and assign the damage amount, type, and last damage time based on the
        // magnitude, ID, and current time respectively.
        DamageOverTimeEffect dotEffect = new DamageOverTimeEffect();
        dotEffect.damageAmount = TeraMath.floorToInt(magnitude);
        dotEffect.lastDamageTime = time.getGameTimeInMs();
        // TODO: Temporary until more damage prefabs are added.
        dotEffect.damageType = "AlterationEffects:PoisonDamage";

        // If the current DOT type doesn't already exist, add the dotEffect into the map directly. Otherwise, replace
        // the older one.
        if (dot.dots.get(id) == null) {
            dot.dots.put(id, dotEffect);
        } else {
            dot.dots.replace(id, dotEffect);
        }

        // Send out this event to collect all the duration and magnitude modifiers and multipliers that can affect this
        // DOT effect. The ID is also sent to distinguish it from other possible DOT effects.
        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this, id));
        long modifiedDuration = 0;
        boolean modifiersFound = false;

        // If the effect modify event is consumed, don't apply this DOT effect.
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
                dotEffect.damageAmount = (int) modifiedMagnitude;
                modifiersFound = true;
            }
        }

        // Save the component so the latest changes to it don't get lost when the game's exited.
        entity.saveComponent(dot);

        // If the modified duration is between the accepted values (0 and Long.MAX_VALUE), and the base duration is not infinite,
        // add a delayed action to the DelayManager using the new system.
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();

            // If the DOT effects effectIDMap doesn't have a submap for this type of DOT effect, create it and add the
            // effectID into it.
            if (dot.effectIDMap.get(id) == null) {
                dot.effectIDMap.put(id, new HashMap<String, Boolean>());
            }
            dot.effectIDMap.get(id).put(effectID, true);

            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.DAMAGE_OVER_TIME
                    + ":" + id + "|" + effectID, modifiedDuration);
        }
        // Otherwise, if the duration is greater than 0, there are no modifiers found, and the effect modify event was not consumed,
        // add a delayed action to the DelayManager using the old system.
        else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.DAMAGE_OVER_TIME
                    + ":" + id, duration);
        }
        // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event have infinite
        // duration, remove the resist effect from the DOT component.
        else if (!modifiersFound || !effectModifyEvent.getHasInfDuration()) {
            dot.dots.remove(id, dotEffect);
        }
        // If this point is reached and none of the above if-clauses were met, that means there was at least one modifier
        // collected in the event which has infinite duration.
    }
}
