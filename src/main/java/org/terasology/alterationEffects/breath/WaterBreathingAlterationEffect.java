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
package org.terasology.alterationEffects.breath;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

public class WaterBreathingAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public WaterBreathingAlterationEffect(Context context) {
        delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Water Breathing effect on the entity - can stay underwater without taking damage
     *
     * @param instigator the entity which applied the Water Breathing effect
     * @param entity the entity the Water Breathing effect is applied on
     * @param magnitude inapplicable to the Water Breathing effect
     * @param duration the duration of the Water Breathing effect
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        final WaterBreathingComponent component = entity.getComponent(WaterBreathingComponent.class);
        if (component == null) {
            entity.saveComponent(new WaterBreathingComponent());
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.WATER_BREATHING, duration);
    }

    /**
     * Applies the Water Breathing effect on the entity - can stay underwater without taking damage
     *
     * @param instigator the entity which applied the Water Breathing effect
     * @param entity the entity the Water Breathing effect is applied on
     * @param id inapplicable to the Water Breathing effect
     * @param magnitude the percentage increase in maximum health due to the Water Breathing effect
     * @param duration the duration of the Water Breathing effect
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
