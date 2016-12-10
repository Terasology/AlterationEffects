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
package org.terasology.alterationEffects.speed;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

public class StunAlterationEffect implements AlterationEffect {

    private final DelayManager delayManager;

    public StunAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Stun effect on the entity - prevents the entity from moving or jumping
     *
     * @param instigator the entity which applied the Stun effect
     * @param entity the entity the Stun effect is applied on
     * @param magnitude inapplicable to the Stun effect
     * @param duration the duration for which the Stun effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        StunComponent stun = entity.getComponent(StunComponent.class);
        if (stun == null) {
            stun = new StunComponent();
            entity.addComponent(stun);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.STUN, duration);
    }

    /**
     * Applies the Stun effect on the entity - prevents the entity from moving or jumping
     *
     * @param instigator the entity which applied the Stun effect
     * @param entity the entity the Stun effect is applied on
     * @param id inapplicable to the Stun effect
     * @param magnitude inapplicable to the Stun effect
     * @param duration the duration for which the Stun effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
