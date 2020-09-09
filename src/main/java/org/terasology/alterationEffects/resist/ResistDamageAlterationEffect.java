// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.resist;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.math.TeraMath;

/**
 * This handles the application of the resist damage effect, which allows an entity to resist certain damage types
 * (based on the magnitude) for a specified duration.
 */
public class ResistDamageAlterationEffect implements AlterationEffect {

    private final DelayManager delayManager;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public ResistDamageAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * This will apply the resist damage effect on the given entity by calling the method {@link #applyEffect(EntityRef,
     * EntityRef, String, float, long)}.
     *
     * @param instigator The entity who applied the resist damage effect.
     * @param entity The entity that the resist damage effect is being applied on.
     * @param magnitude The magnitude of the resist damage effect.
     * @param duration The duration of the resist damage effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "Default", magnitude, duration);
    }

    /**
     * This will apply the resist damage effect on the given entity. This method will send out an event to the other
     * applicable effect systems so that they can contribute with their own resist damage effect related modifiers.
     *
     * @param instigator The entity who applied the resist damage effect.
     * @param entity The entity that the resist damage effect is being applied on.
     * @param id The ID of this resist damage effect. This is used for determining what damage type to add this
     *         resistance to.
     * @param magnitude The magnitude of the resist damage effect.
     * @param duration The duration of the resist damage effect.
     */
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        // First, determine if the entity already has a resist damage component attached. If not, create a new one and
        // attach it to the entity.
        ResistDamageComponent resDamageComponent = entity.getComponent(ResistDamageComponent.class);
        if (resDamageComponent == null) {
            resDamageComponent = new ResistDamageComponent();
            entity.addComponent(resDamageComponent);
        }

        // Create a new ResistDamageEffect instance and assign the resistance type and amount based on the ID and
        // magnitude respectively.
        ResistDamageEffect resEffect = new ResistDamageEffect();
        resEffect.resistType = id;
        resEffect.resistAmount = TeraMath.floorToInt(magnitude);

        // If the current resist type doesn't already exist, add the resEffect into the map directly. Otherwise,
        // replace the older one.
        if (resDamageComponent.rdes.get(id) == null) {
            resDamageComponent.rdes.put(id, resEffect);
        } else {
            resDamageComponent.rdes.replace(id, resEffect);
        }

        // Send out this event to collect all the duration and magnitude modifiers and multipliers that can affect this
        // resist damage effect. The ID is also sent to distinguish it from other possible resist damage effects.
        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this,
                id));
        long modifiedDuration = 0;
        boolean modifiersFound = false;

        // If the effect modify event is consumed, don't apply this resist damage effect.
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
                resEffect.resistAmount = (int) modifiedMagnitude;
                modifiersFound = true;
            }
        }

        // Save the component so the latest changes to it don't get lost when the game's exited.
        entity.saveComponent(resDamageComponent);

        // If the modified duration is between the accepted values (0 and Long.MAX_VALUE), and the base duration is 
        // not infinite,
        // add a delayed action to the DelayManager using the new system.
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity,
                    AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.RESIST_DAMAGE
                    + ":" + id + "|" + effectID, modifiedDuration);
        }
        // Otherwise, if the duration is greater than 0, there are no modifiers found, and the effect modify event 
        // was not consumed,
        // add a delayed action to the DelayManager using the old system.
        else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            delayManager.addDelayedAction(entity,
                    AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.RESIST_DAMAGE
                    + ":" + id, duration);
        }
        // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event have 
        // infinite
        // duration, remove the resist effect from the resist damage component.
        else if (!modifiersFound || !effectModifyEvent.getHasInfDuration()) {
            resDamageComponent.rdes.remove(id, resEffect);
        }
        // If this point is reached and none of the above if-clauses were met, that means there was at least one 
        // modifier
        // collected in the event which has infinite duration.
    }
}
