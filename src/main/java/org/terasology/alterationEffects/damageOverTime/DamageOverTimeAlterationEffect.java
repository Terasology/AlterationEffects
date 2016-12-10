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

public class DamageOverTimeAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    public DamageOverTimeAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Damage Over Time effect on the entity - they take damage at regular intervals
     *
     * @param instigator the entity which applied the Damage Over Time effect
     * @param entity the entity the Damage Over Time effect is applied on
     * @param magnitude the damage taken by the entity regains at each interval
     * @param duration the duration for which the Damage Over Time effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "Default", magnitude, duration);
    }

    /**
     * Applies the Damage Over Time effect on the entity - they regain health at regular intervals
     *
     * @param instigator the entity which applied the Damage Over Time effect
     * @param entity the entity the Damage Over Time effect is applied on
     * @param id the type of damage taken by the entity
     * @param magnitude the damage taken by the entity regains at each interval
     * @param duration the duration for which the Damage Over Time effect lasts
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
