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
 * This handles the applying of the decover effect, which prevents an entity from healing for a specified duration.
 */
public class DecoverAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public DecoverAlterationEffect(Context context) {
        delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the decover effect on an entity.
     *
     * @param instigator the entity who applied the decover effect
     * @param entity     the entity the decover effect is applied on
     * @param magnitude  inapplicable to the decover effect
     * @param duration   the duration for which the decover effect lasts
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
     * Applies the decover effect on an entity.
     *
     * @param instigator The instigator of the action
     * @param entity     The entity to which the effect is being applied
     * @param id         The ID of the effect
     * @param magnitude  The magnitude of the effect
     * @param duration   The duration of the effect
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
