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
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ConsumableEvent;

/**
 * This event is sent to an entity to collect all the potential magnitude and duration modifiers for a particular
 * alteration effect. The entire event may be consumed if needed. This is intended to be called when a new effect is
 * created or a new modifier is added.
 */
public class OnEffectModifyEvent implements ConsumableEvent {
    /** Flags whether this event been consumed or not. */
    private boolean consumed;

    /** The instigator entity that instigated this effect modification. */
    private EntityRef instigator;

    /** A reference to the entity being affected this by effect. */
    private EntityRef entity;

    /** Reference to the original alteration effect that spawned this effect. */
    private AlterationEffect alterationEffect;

    /** Used to distinguish different effects under the same parent type. Like DOT or Resist ones. */
    private String id;

    /** Base magnitude of this effect. */
    private float baseMagnitude = 0f;

    /** Base duration of this effect. */
    private long baseDuration = 0;

    /** The current shortest duration of all the effects of this same type that are currently running. */
    private long shortestDuration = Long.MAX_VALUE;

    /** ID of the item or entity that has the current shortest duration left for its effect being applied. */
    private String effectIDWithShortestDuration = "";

    /** Flag for checking if at least one modifier has infinite duration. */
    private boolean hasInfDuration = false;

    /** String that stores the effectID with infinite duration. */
    private String effectIDWithInfiniteDuration = "";

    /* Flag for checking if at least one modifier has zero duration. */
    private boolean hasZeroDuration = false;

    /** String that stores the effectID with zero duration. */
    private String effectIDWithZeroDuration = "";

    /** A list of all the multipliers for this alteration effect's magnitude. */
    private TFloatList magnitudeMultipliers = new TFloatArrayList();

    /** A list of all the multipliers for this alteration effect's duration. */
    private TFloatList durationMultipliers = new TFloatArrayList();

    /** A list of all the modifiers for this alteration effect's magnitude. */
    private TFloatList magnitudeModifiers = new TFloatArrayList();

    /** A list of all the modifiers for this alteration effect's duration. */
    private TDoubleList durationModifiers = new TDoubleArrayList();

    /**
     * Create an instance of this event with the minimum number of required parameters. Only use this for debugging.
     *
     * @param instigator        The entity that caused this effect to be applied or modified.
     * @param entity            The entity that the effect is currently on.
     * @param baseMagnitude     The base magnitude of this effect.
     * @param baseDuration      The base duration of this effect.
     */
    public OnEffectModifyEvent(EntityRef instigator, EntityRef entity, float baseMagnitude, long baseDuration) {
        this.instigator = instigator;
        this.entity = entity;
        this.baseMagnitude = baseMagnitude;
        this.baseDuration = baseDuration;
    }

    /**
     * Create an instance of this event with the given base values, alteration effect, and ID (if needed). Use this one
     * over the other method if you wish to use the updated effects system.
     *
     * @param instigator        The entity that caused this effect to be applied or modified.
     * @param entity            The entity that the effect is currently on.
     * @param baseMagnitude     The base magnitude of this effect.
     * @param baseDuration      The base duration of this effect.
     * @param alterationEffect  The original alteration effect that created this effect.
     * @param id                The optional ID of this effect. Only used for effects that support sub-types (like DOT
     *                          for example).
     */
    public OnEffectModifyEvent(EntityRef instigator, EntityRef entity, float baseMagnitude, long baseDuration, AlterationEffect alterationEffect, String id) {
        this.instigator = instigator;
        this.entity = entity;
        this.baseMagnitude = baseMagnitude;
        this.baseDuration = baseDuration;
        this.alterationEffect = alterationEffect;
        this.id = id;
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

    /**
     * Get a reference to the alteration effect that created the original effect.
     *
     * @return  A reference to the original alteration effect.
     */
    public AlterationEffect getAlterationEffect() {
        return alterationEffect;
    }

    /**
     * Get the ID of this effect.
     *
     * @return  The ID of the effect.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the shortest duration of all modifiers collected in this list. If one or more had zero duration, zero will
     * be returned. Otherwise, it'll be the shortest duration counted.
     *
     * @return  The shortest duration counted.
     */
    public long getShortestDuration() {
        if (hasZeroDuration) {
            return 0;
        } else {
            return shortestDuration;
        }
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
     * Get the effectID of the effect modifier with the shortest duration. If one or more had zero duration, the last
     * effectID counted that had zero duration will be returned. Otherwise, the effectID associated with the modifier
     * with the shortest duration will be returned.
     *
     * @return  The effectID associated to the modifier with the shortest counted duration.
     */
    public String getEffectIDWithShortestDuration() {
        if (hasZeroDuration) {
            return effectIDWithZeroDuration;
        } else {
            return effectIDWithShortestDuration;
        }
    }

    /**
     * Get the effectID associated with the modifier with infinite duration.
     *
     * @return  The effectID of the modifier with infinite duration.
     */
    public String getEffectIDWithInfiniteDuration() {
        return effectIDWithInfiniteDuration;
    }

    /**
     * Get whether one or more of the modifiers collected has infinite duration.
     *
     * @return  The value of the hasInfDuration flag.
     */
    public boolean getHasInfDuration() {
        return hasInfDuration;
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
     * Add a (pre)modifier to the duration (pre)modifiers list.
     *
     * @param amount    The value of the modifier to add to the list.
     */
    public void addDuration(double amount, String effectID) {
        if (amount < shortestDuration) {
            if (amount < 0) {
                hasInfDuration = true;
                effectIDWithInfiniteDuration = effectID;
            } else if (amount == 0) {
                hasZeroDuration = true;
                effectIDWithZeroDuration = effectID;
            } else {
                shortestDuration = (long) amount;
                effectIDWithShortestDuration = effectID;
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
        // For now, add all modifiers and multiply by all multipliers.

        // First add the (pre)modifiers.
        float result = baseMagnitude;
        TFloatIterator modifierIter = magnitudeModifiers.iterator();
        while (modifierIter.hasNext()) {
            result += modifierIter.next();
        }

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

    @Override
    public boolean isConsumed() {
        return consumed;
    }

    @Override
    public void consume() {
        consumed = true;
    }
}
