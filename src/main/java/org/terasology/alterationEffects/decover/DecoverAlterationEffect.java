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
package org.terasology.alterationEffects.decover;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

/**
 * This handles the applying of the decover effect, which prevents an entity from healing for a specified duration
 */
public class DecoverAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public DecoverAlterationEffect(Context context) {
        delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Decover effect on the entity
     *
     * @param instigator the entity which applied the Decover effect
     * @param entity the entity the Decover effect is applied on
     * @param magnitude inapplicable to the Decover effect
     * @param duration the duration for which the Decover effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        DecoverComponent decover = entity.getComponent(DecoverComponent.class);
        if (decover == null) {
            decover = new DecoverComponent();
            entity.addComponent(decover);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.DECOVER, duration);
    }

    /**
     * Applies the Decover effect on the entity - entity cannot be healed by any source
     *
     * @param instigator the entity which applied the Decover effect
     * @param entity the entity the Decover effect is applied on
     * @param id inapplicable to the Decover effect
     * @param magnitude inapplicable to the Decover effect
     * @param duration the duration for which the Decover effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
