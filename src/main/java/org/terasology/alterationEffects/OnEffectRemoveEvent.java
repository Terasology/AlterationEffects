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
package org.terasology.alterationEffects;

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TFloatArrayList;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ConsumableEvent;

// USe BeforeApplyPotionEffectEvent as a base for OnEffectModifyEvent.
// Have a ref to the AlterationEffect.

public class OnEffectRemoveEvent implements ConsumableEvent {
    /** Flags whether this event been consumed or not. */
    private boolean consumed;

    /** Flags whether this effect has expired or not. */
    private boolean expired = false;

    /** The instigator entity that instigated this effect modification. */
    private EntityRef instigator;

    /** A reference to the entity being affected this by effect. */
    private EntityRef entity;

    /** Reference to the original alteration effect that spawned this effect. */
    private AlterationEffect alterationEffect;

    /** Used to distinguish different effects under the same parent type. Like DOT or Resist ones. */
    private String id = "";

    /** Used to identify individual effects. */
    private String effectID = "";

    /** Base magnitude of this effect. */
    private float baseMagnitude = 0f;

    /** Base duration of this effect. */
    private long baseDuration = 0;

    /** The current shortest duration of all the effects */
    private long shortestDuration = Long.MAX_VALUE;

    /** A list of all the multipliers for this effect's magnitude. */
    private TFloatList magnitudeMultipliers = new TFloatArrayList();

    /** A list of all the multipliers for this effect's duration. */
    private TFloatList durationMultipliers = new TFloatArrayList();

    /** A list of all the modifiers for this effect's magnitude. */
    private TFloatList magnitudeModifiers = new TFloatArrayList();

    /** A list of all the modifiers for this effect's duration. */
    private TDoubleList durationModifiers = new TDoubleArrayList();

    public OnEffectRemoveEvent(EntityRef instigator, EntityRef entity, AlterationEffect alterationEffect, String effectID, String id) {
        this.instigator = instigator;
        this.entity = entity;
        this.alterationEffect = alterationEffect;
        this.effectID = effectID;
        this.id = id;
    }

    public OnEffectRemoveEvent(EntityRef instigator, EntityRef entity, AlterationEffect alterationEffect, String effectID, String id, boolean expired) {
        this.instigator = instigator;
        this.entity = entity;
        this.alterationEffect = alterationEffect;
        this.effectID = effectID;
        this.id = id;
        this.expired = expired;
    }

    /**
     * Get the entity who instigated this modify event.
     *
     * @return  A reference to the instigator entity.
     */
    public EntityRef getInstigator() { return instigator; }

    /**
     * Get a reference to the entity being affected.
     *
     * @return  A reference to the affected entity.
     */
    public EntityRef getEntity() { return entity; }

    public AlterationEffect getAlterationEffect() {
        return alterationEffect;
    }

    public String getEffectId() {
        return effectID;
    }

    public String getId() {
        return id;
    }

    public long getShortestDuration() {
        return shortestDuration;
    }

    /**
     * Get a list of all the potion effect magnitude multipliers.
     *
     * @return  A TFloatList containing the magnitude modifiers.
     */
    public TFloatList getMagnitudeMultipliers() {
        return magnitudeMultipliers;
    }

    /**
     * Get a list of all the potion effect duration multipliers.
     *
     * @return  A TFloatList containing the duration modifiers.
     */
    public TFloatList getDurationMultipliers() {
        return durationMultipliers;
    }

    /**
     * Get a list of all the potion effect magnitude (pre)modifiers.
     *
     * @return  A TFloatList containing the magnitude (pre)modifiers.
     */
    public TFloatList getMagnitudeModifiers() {
        return magnitudeModifiers;
    }

    /**
     * Get a list of all the potion effect duration (pre)modifiers.
     *
     * @return  A TDoubleList containing the duration (pre)modifiers.
     */
    public TDoubleList getDurationModifiers() {
        return durationModifiers;
    }

    /**
     * Add a multiplier to the magnitude multipliers list.
     *
     * @param amount    The value of the multiplier to add to the list.
     */
    public void multiplyMagnitude(float amount) {
        magnitudeMultipliers.add(amount);
    }

    /**
     * Add a multiplier to the duration multipliers list.
     *
     * @param amount    The value of the multiplier to add to the list.
     */
    public void multiplyDuration(float amount) {
        durationMultipliers.add(amount);
    }

    /**
     * Add a (pre)modifier to the magnitude (pre)modifiers list.
     *
     * @param amount    The value of the modifier to add to the list.
     */
    public void addMagnitude(float amount) {
        magnitudeModifiers.add(amount);
    }

    /**
     * Add a (pre)modifier to the duration (pre)modifiers list.
     *
     * @param amount    The value of the modifier to add to the list.
     */
    public void addDuration(double amount) {
        if (amount < shortestDuration) {
            shortestDuration = (long) amount;

            if (shortestDuration < 0) {
                shortestDuration = 0;
            }
        }
        durationModifiers.add(amount);
    }

    /**
     * Add a negative (pre)modifier to the magnitude (pre)modifiers list.
     *
     * @param amount    The value of the modifier to add to the list.
     */
    public void subtractMagnitude(float amount) {
        magnitudeModifiers.add(-amount);
    }

    /**
     * Add a negative (pre)modifier to the duration (pre)modifiers list.
     *
     * @param amount    The value of the modifier to add to the list.
     */
    public void subtractDuration(long amount) {
        durationModifiers.add(-amount);
    }

    /**
     * Apply all of the magnitude modifiers and multipliers to get the net magnitude result value.
     *
     * @return  The result of the magnitude modifier and multiplier calculations.
     */
    public float getMagnitudeResultValue() {
        // For now, add all modifiers and multiply by all multipliers. Negative modifiers cap to zero, but negative
        // multipliers remain.

        // First add the (pre)modifiers.
        float result = baseMagnitude;
        TFloatIterator modifierIter = magnitudeModifiers.iterator();
        while (modifierIter.hasNext()) {
            result += modifierIter.next();
        }
        result = Math.max(0, result);

        // Then, multiply the multipliers.
        TFloatIterator multiplierIter = magnitudeMultipliers.iterator();
        while (multiplierIter.hasNext()) {
            result *= multiplierIter.next();
        }

        /*
        final TFloatIterator postModifierIter = postModifiers.iterator();
        while (postModifierIter.hasNext()) {
            result += postModifierIter.next();
        }
        */
        return result;
    }

    /**
     * Apply all of the duration modifiers and multipliers to get the net duration result value.
     *
     * @return  The result of the duration modifier and multiplier calculations.
     */
    public long getDurationResultValue() {
        // For now, add all modifiers and multiply by all multipliers. Negative modifiers cap to zero, but negative
        // multipliers remain.

        // First add the (pre)modifiers.
        double result = baseDuration;
        TDoubleIterator modifierIter = durationModifiers.iterator();
        while (modifierIter.hasNext()) {
            result += modifierIter.next();
        }
        result = Math.max(0, result);

        // Then, multiply the multipliers.
        TFloatIterator multiplierIter = magnitudeMultipliers.iterator();
        while (multiplierIter.hasNext()) {
            result *= multiplierIter.next();
        }

        /*
        final TFloatIterator postModifierIter = postModifiers.iterator();
        while (postModifierIter.hasNext()) {
            result += postModifierIter.next();
        }
        */
        return (long) result;
    }

    public void setBaseMagnitude(float base) {
        baseMagnitude = base;
    }

    public void setBaseDuration(long base) {
        baseDuration = base;
    }

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    @Override
    public void consume() {
        consumed = true;
    }

    /**
     * Get whether this effect has expired (time ran out) or not (was removed before time ran out).
     *
     * @return  The value of the expired flag.
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * Mark this effect as expired (time ran out).
     */
    public void expire() {
        expired = true;
    }
}
