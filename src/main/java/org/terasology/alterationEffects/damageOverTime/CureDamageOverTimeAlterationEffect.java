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
import org.terasology.alterationEffects.OnEffectRemoveEvent;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

import java.util.Map;

/**
 * This handles the application of the cure damage over time (DOT) effect, which cures a DOT effect or ailment of a
 * specified type being applied to an entity. This WILL only remove DOT modifiers that have a finite duration.
 */
public class CureDamageOverTimeAlterationEffect implements AlterationEffect {

    private final DelayManager delayManager;
    private Context context;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context       The context which this effect will be executed on.
     */
    public CureDamageOverTimeAlterationEffect(Context context) {
        this.context = context;
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * This will apply the cure DOT effect on the given entity by calling the method
     * {@link #applyEffect(EntityRef, EntityRef, String, float, long)} with the ID being set to "Default".
     *
     * @param instigator    The entity who applied the cure DOT effect.
     * @param entity        The entity that the cure DOT effect is being applied on.
     * @param magnitude     Inapplicable to the cure DOT effect.
     * @param duration      Inapplicable to the cure DOT effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "Default", magnitude, duration);
    }

    /**
     * This will apply the cure DOT effect on the given entity. This method will send out an event to the other
     * applicable effect systems informing them that all temporary modifiers that contribute to this particular DOT
     * type will need to be removed. Then, this method will reapply the DOT of that same damage type so that the
     * permanent same type DOT modifiers will be recalculated properly.
     *
     * @param instigator    The entity who applied the cure DOT effect.
     * @param entity        The entity that the cure DOT effect is being applied on.
     * @param id            The ID of this cure DOT effect. This is used for determining what damage type to add this
     *                      resistance to.
     * @param magnitude     Inapplicable to the cure DOT effect.
     * @param duration      Inapplicable to the cure DOT effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        // First, determine if the entity already has a damage over time component attached. If not, return as there's
        // nothing to be cured.
        DamageOverTimeComponent dot = entity.getComponent(DamageOverTimeComponent.class);
        if (dot == null) {
            return;
        }

        // Check the DOT effects map to see if this particular type of DOT (indicated by the ID) is in effect. If so,
        // remove it.
        if (dot.dots.get(id) != null) {
            dot.dots.remove(id);

            // Cure all sources of this type of DOT.
            for (Map.Entry<String, Boolean> entry : dot.effectIDMap.get(id).entrySet()) {
                delayManager.cancelDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX +
                        AlterationEffects.DAMAGE_OVER_TIME + ":" + id + "|" + entry.getKey());
            }
            dot.effectIDMap.remove(id);

            // Send an event to remove the temporary source effects that caused this DOT effect. This is intended to
            // remove temporary effects like from potions, but not from equipment or more permanent sources.
            DamageOverTimeAlterationEffect dotAlterationEffect = new DamageOverTimeAlterationEffect(context);
            entity.send(new OnEffectRemoveEvent(entity, entity, dotAlterationEffect, AlterationEffects.CONSUMABLE_ITEM, id));

            // After removing the temporary DOT effects of this type, call the DOT alteration effect to calculate the
            // new magnitude and duration. This will tally up the non-removable modifiers (if any).
            dotAlterationEffect.applyEffect(entity, entity, id, 0, 0);

            // If there are no DOT effects in place, remove the DamageOverTime component from this entity.
            if (dot.dots.isEmpty()) {
                entity.removeComponent(DamageOverTimeComponent.class);
            }
        }
    }
}
