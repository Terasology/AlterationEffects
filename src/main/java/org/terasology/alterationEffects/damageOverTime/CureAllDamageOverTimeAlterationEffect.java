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
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.delay.DelayManager;

import java.util.Map;

/**
 * This handles the application of the cure all damage over time (DOT) effect, which cures all DOT effects or ailments
 * being applied to an entity. This WILL only remove DOT modifiers that have a finite duration.
 */
public class CureAllDamageOverTimeAlterationEffect implements AlterationEffect {

    private final DelayManager delayManager;
    private Context context;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context       The context which this effect will be executed on.
     */
    public CureAllDamageOverTimeAlterationEffect(Context context) {
        this.context = context;
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * This will apply the cure all DOT effect on the given entity. This method will send out an event to the other
     * applicable effect systems informing them that all temporary modifiers that contribute to all DOT types will need
     * to be removed. Then, this method will reapply all the DOT types so that the permanent DOT modifiers will be
     * recalculated properly.
     *
     * @param instigator    The entity who applied the cure all DOT effect.
     * @param entity        The entity that the cure all DOT effect is being applied on.
     * @param magnitude     Inapplicable to the cure all DOT effect.
     * @param duration      Inapplicable to the cure all DOT effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        DamageOverTimeComponent dot = entity.getComponent(DamageOverTimeComponent.class);
        if (dot != null) {
            // Cure each type of DOT effect.
            for (Map.Entry<String, Map<String, Boolean>> dotType : dot.effectIDMap.entrySet()) {
                // Cure all sources of this type of DOT effect.
                for (Map.Entry<String, Boolean> dotSource : dotType.getValue().entrySet()) {
                    delayManager.cancelDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX +
                            AlterationEffects.DAMAGE_OVER_TIME + ":" + dotType.getKey() + "|" + dotSource.getKey());
                }

                // Send an event to remove the temporary source effects that caused this DOT effect. This is intended to
                // remove temporary effects like from potions, but not from equipment or more permanent sources.
                DamageOverTimeAlterationEffect dotAlterationEffect = new DamageOverTimeAlterationEffect(context);
                entity.send(new OnEffectRemoveEvent(entity, entity, dotAlterationEffect, AlterationEffects.CONSUMABLE_ITEM, dotType.getKey()));

                // After removing the temporary DOT effects of this type, call the DOT alteration effect to calculate the new
                // magnitude and duration.
                dotAlterationEffect.applyEffect(entity, entity, dotType.getKey(), 0, 0);
            }

            // If there are no DOT effects in place, remove the DamageOverTime component from this entity.
            if (dot.dots.isEmpty()) {
                entity.removeComponent(DamageOverTimeComponent.class);
            }
        }
    }

    /**
     * This will apply the cure all damage over time (DOT) effect on the given entity by calling the method
     * {@link #applyEffect(EntityRef, EntityRef, float, long)}.
     *
     * @param instigator    The entity who applied the damage over time effect.
     * @param entity        The entity that the damage over time effect is being applied on.
     * @param magnitude     Inapplicable to the cure all DOT effect.
     * @param duration      Inapplicable to the cure all DOT effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
