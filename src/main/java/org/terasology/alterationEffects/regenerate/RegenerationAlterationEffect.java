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
package org.terasology.alterationEffects.regenerate;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.math.TeraMath;

public class RegenerationAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    public RegenerationAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Regeneration effect on the entity - they regain health at regular intervals
     *
     * @param instigator the entity which applied the Regeneration effect
     * @param entity the entity the Regeneration effect is applied on
     * @param magnitude the amount of health which the entity regains at each interval
     * @param duration the duration for which the Regeneration effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        RegenerationComponent regeneration = entity.getComponent(RegenerationComponent.class);
        if (regeneration == null) {
            regeneration = new RegenerationComponent();
            regeneration.regenerationAmount = TeraMath.floorToInt(magnitude);
            regeneration.lastRegenerationTime = time.getGameTimeInMs();
            entity.addComponent(regeneration);
        } else {
            regeneration.regenerationAmount = TeraMath.floorToInt(magnitude);
            regeneration.lastRegenerationTime = time.getGameTimeInMs();
            entity.addComponent(regeneration);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.REGENERATION, duration);
    }

    /**
     * Applies the Regeneration effect on the entity - they regain health at regular intervals
     *
     * @param instigator the entity which applied the Regeneration effect
     * @param entity the entity the Regeneration effect is applied on
     * @param id inapplicable to the Regeneration effect
     * @param magnitude the amount of health which the entity regains at each interval
     * @param duration the duration for which the Regeneration effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
