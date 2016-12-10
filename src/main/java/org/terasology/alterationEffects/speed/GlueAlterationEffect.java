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

public class GlueAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public GlueAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    /**
     * Applies the Glue effect on the entity - they move 10% slower and are unable to jump
     *
     * @param instigator the entity which applied the Glue effect
     * @param entity the entity the Glue effect is applied on
     * @param magnitude inapplicable to the Glue effect
     * @param duration the duration for which the Glue effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        boolean add = false;
        GlueComponent glue = entity.getComponent(GlueComponent.class);
        if (glue == null) {
            add = true;
            glue = new GlueComponent();
        }
        glue.multiplier = magnitude;

        if (add) {
            entity.addComponent(glue);
        } else {
            entity.saveComponent(glue);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.GLUE, duration);
    }

    /**
     * Applies the Glue effect on the entity - they move 10% slower and are unable to jump
     *
     * @param instigator the entity which applied the Glue effect
     * @param entity the entity the Glue effect is applied on
     * @param id inapplicable to the Glue effect
     * @param magnitude inapplicable to the Glue effect
     * @param duration the duration for which the Glue effect lasts
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
