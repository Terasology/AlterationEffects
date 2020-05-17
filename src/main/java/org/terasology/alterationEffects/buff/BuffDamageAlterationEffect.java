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
package org.terasology.alterationEffects.buff;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.math.TeraMath;

/**
 * This handles the application of the buff damage effect, which allows an entity to buff certain damage types
 * (based on the magnitude) for a specified duration.
 */
public class BuffDamageAlterationEffect implements AlterationEffect {

    private final DelayManager delayManager;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context       The context which this effect will be executed on.
     */
    public BuffDamageAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * This will apply the buff damage effect on the given entity by calling the method
     * {@link #applyEffect(EntityRef, EntityRef, String, float, long)}.
     *
     * @param instigator    The entity who applied the buff damage effect.
     * @param entity        The entity that the buff damage effect is being applied on.
     * @param magnitude     The magnitude of the buff damage effect.
     * @param duration      The duration of the buff damage effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "Default", magnitude, duration);
    }

    /**
     * This will apply the buff damage effect on the given entity. This method will send out an event to the other
     * applicable effect systems so that they can contribute with their own buff damage effect related modifiers.
     *
     * @param instigator    The entity who applied the buff damage effect.
     * @param entity        The entity that the buff damage effect is being applied on.
     * @param id            The ID of this buff damage effect. This is used for determining what damage type to add
     *                      this resistance to.
     * @param magnitude     The magnitude of the buff damage effect.
     * @param duration      The duration of the buff damage effect.
     */
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        // First, determine if the entity already has a buff damage component attached. If not, create a new one and
        // attach it to the entity.
        BuffDamageComponent buffDamageComponent = entity.getComponent(BuffDamageComponent.class);
        if (buffDamageComponent == null) {
            buffDamageComponent = new BuffDamageComponent();
            entity.addComponent(buffDamageComponent);
        }

        // Create a new BuffDamageEffect instance and assign the damage type and amount based on the ID and
        // magnitude respectively.
        BuffDamageEffect buffDamageEffect = new BuffDamageEffect();
        buffDamageEffect.damageType = id;
        buffDamageEffect.buffAmount = TeraMath.floorToInt(magnitude);

        // If the current damage type doesn't already exist, add the buffDamageEffect into the map directly. Otherwise,
        // replace the older one.
        if (buffDamageComponent.bdes.get(id) == null) {
            buffDamageComponent.bdes.put(id, buffDamageEffect);
        } else {
            buffDamageComponent.bdes.replace(id, buffDamageEffect);
        }

        // Send out this event to collect all the duration and magnitude modifiers and multipliers that can affect this
        // buff damage effect. The ID is also sent to distinguish it from other possible buff damage effects.
        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this, id));
        long modifiedDuration = 0;
        boolean modifiersFound = false;

        // If the effect modify event is consumed, don't apply this buff damage effect.
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

            // If there's at least one duration and magnitude modifier, set the effect's magnitude and the
            // modifiersFound flag.
            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                buffDamageEffect.buffAmount = (int) modifiedMagnitude;
                modifiersFound = true;
            }
        }

        // Save the component so the latest changes to it don't get lost when the game's exited.
        entity.saveComponent(buffDamageComponent);

        // If the modified duration is between the accepted values (0 and Long.MAX_VALUE), and the base duration is not infinite,
        // add a delayed action to the DelayManager using the new system.
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.BUFF_DAMAGE
                    + ":" + id + "|" + effectID, modifiedDuration);
        }
        // Otherwise, if the duration is greater than 0, there are no modifiers found, and the effect modify event was not consumed,
        // add a delayed action to the DelayManager using the old system.
        else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.BUFF_DAMAGE
                    + ":" + id, duration);
        }
        // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event have infinite
        // duration, remove the buff effect from the buff damage component.
        else if (!modifiersFound || !effectModifyEvent.getHasInfDuration()) {
            buffDamageComponent.bdes.remove(id, buffDamageEffect);
        }
        // If this point is reached and none of the above if-clauses were met, that means there was at least one modifier
        // collected in the event which has infinite duration.
    }
}
