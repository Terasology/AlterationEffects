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
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.health.event.ActivateRegenEvent;
import org.terasology.math.TeraMath;

/**
 * This handles the application of the health regeneration effect, which heals an entity for the given magnitude per
 * second for a specified duration.
 */
public class RegenerationAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the current time and DelayManager.
     *
     * @param context       The context which this effect will be executed on.
     */
    public RegenerationAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * This will apply the regeneration effect on the given entity. This method will send out an event to the other
     * applicable effect systems so that they can contribute with their own regeneration effect related modifiers.
     *
     * @param instigator    The entity who applied the regen effect.
     * @param entity        The entity that the regen effect is being applied on.
     * @param magnitude     The magnitude of the regen effect.
     * @param duration      The duration of the regen effect.
     */
    @SuppressWarnings("CheckStyle")
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        // First, determine if the entity already has a regeneration component attached. If so, just replace the amount
        // and last regen time, and then save the component. Otherwise, create a new one and attach it to the entity.
        RegenerationComponent regeneration = entity.getComponent(RegenerationComponent.class);
        if (regeneration == null) {
            regeneration = new RegenerationComponent();
            regeneration.regenerationAmount = TeraMath.floorToInt(magnitude);
            regeneration.lastRegenerationTime = time.getGameTimeInMs();
            entity.addComponent(regeneration);
        } else {
            regeneration.regenerationAmount = TeraMath.floorToInt(magnitude);
            regeneration.lastRegenerationTime = time.getGameTimeInMs();
            entity.saveComponent(regeneration);
        }

        // Send out this event to collect all the duration and magnitude modifiers and multipliers that can affect this
        // regeneration effect.
        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this, ""));
        long modifiedDuration = 0;      // This will keep track of the current modified duration.
        boolean modifiersFound = false; // This flag will keep track if there were any modifiers collected in the event.

        // If the effect modify event is consumed, don't apply this health regeneration effect.
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
                regeneration.regenerationAmount = (int) modifiedMagnitude;
                modifiersFound = true;
            }
        }

        // Save the component so the latest changes to it don't get lost when the game's exited.
        entity.saveComponent(regeneration);

        // If the modified duration is between the accepted values (0 and Long.MAX_VALUE), and the base duration is not infinite,
        // add a delayed action to the DelayManager using the new system.
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE ) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.REGENERATION + "|" + effectID, modifiedDuration);
        }
        // Otherwise, if the duration is greater than 0, there are no modifiers found, and the effect modify event was not consumed,
        // add a delayed action to the DelayManager using the old system.
        else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.REGENERATION, duration);
        }
        // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event have infinite
        // duration, remove the component associated with this regeneration effect.
        else if (!modifiersFound || !effectModifyEvent.getHasInfDuration()) {
            entity.removeComponent(RegenerationComponent.class);
        }
        // If this point is reached and none of the above if-clauses were met, that means there was at least one modifier
        // collected in the event which has infinite duration.
    }

    /**
     * This will apply the regeneration effect on the given entity by calling the method
     * {@link #applyEffect(EntityRef, EntityRef, float, long)}.
     *
     * @param instigator    The entity who applied the regen effect.
     * @param entity        The entity that the regen effect is being applied on.
     * @param id            Id is used for unique identification in regen scheduler.
     * @param magnitude     The magnitude of the regen effect.
     * @param duration      The duration of the regen effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        //applyEffect(instigator, entity, magnitude, duration);
        entity.send(new ActivateRegenEvent(id, (int) magnitude, ((float) duration) / 1000));
    }
}
