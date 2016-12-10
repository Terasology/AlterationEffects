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

public class CureDamageOverTimeAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    public CureDamageOverTimeAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }
    
    /**
     * Applies the Cure Damage Over Time effect on the entity - entity cannot be healed by any source
     *
     * @param instigator the entity which applied the Cure Damage Over Time effect
     * @param entity the entity the Cure Damage Over Time effect is applied on
     * @param magnitude inapplicable to the Cure Damage Over Time effect
     * @param duration the duration for which the Cure Damage Over Time effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "Default", magnitude, duration);
    }

    /**
     * Applies the Cure Damage Over Time effect on the entity - negates damage done by Damage Over Time effects
     *
     * @param instigator the entity which applied the Cure Damage Over Time effect
     * @param entity the entity the Cure Damage Over Time effect is applied on
     * @param id inapplicable to the Cure Damage Over Time effect
     * @param magnitude inapplicable to the Cure Damage Over Time effect
     * @param duration the duration for which the Cure Damage Over Time effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        DamageOverTimeComponent dot = entity.getComponent(DamageOverTimeComponent.class);
        if (dot == null) {
            return;
        }

        if (dot.dots.get(id) != null) {
            dot.dots.remove(id);
            delayManager.cancelDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX +
                    AlterationEffects.DAMAGE_OVER_TIME + ":" + id);

            if (dot.dots.isEmpty()) {
                entity.removeComponent(DamageOverTimeComponent.class);
            }
        }
    }
}
