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
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.math.TeraMath;

/**
 * This class defines the damage over time alteration effect, which deals damage to the entity over time.
 */
public class DamageOverTimeAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    /**
     * Parametrized constructor.
     *
     * @param context A Context object representing the state before the boost
     */
    public DamageOverTimeAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the default damage over time alteration effect to an entity.
     *
     * @param instigator The entity applying the effect
     * @param entity     The entity to which the effect is being applied
     * @param magnitude  The magnitude of the effect
     * @param duration   The duration of the effect
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "Default", magnitude, duration);
    }

    /**
     * Applies a damage over time alteration effect with a specified ID to an entity.
     *
     * @param instigator The instigator of the action
     * @param entity     The entity to which the effect is being applied
     * @param id         The ID of the effect
     * @param magnitude  The magnitude of the effect
     * @param duration   The duration of the effect
     */
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        DamageOverTimeComponent dot = entity.getComponent(DamageOverTimeComponent.class);
        if (dot == null) {
            dot = new DamageOverTimeComponent();
            dot.damageAmount = TeraMath.floorToInt(magnitude);
            dot.lastDamageTime = time.getGameTimeInMs();
            entity.addComponent(dot);
        } else {
            dot.damageAmount = TeraMath.floorToInt(magnitude);
            dot.lastDamageTime = time.getGameTimeInMs();
            entity.addComponent(dot);
        }

        DamageOverTimeEffect dotEffect = new DamageOverTimeEffect();
        dotEffect.damageAmount = TeraMath.floorToInt(magnitude);
        dotEffect.lastDamageTime = time.getGameTimeInMs();
        // TODO: Temporary until more damage prefabs are added.
        dotEffect.damageType = "AlterationEffects:PoisonDamage";

        if (dot.dots.get(id) == null) {
            dot.dots.put(id, dotEffect);
        } else {
            dot.dots.replace(id, dotEffect);
        }

        entity.saveComponent(dot);

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.DAMAGE_OVER_TIME + ":" + id, duration);
    }
}
