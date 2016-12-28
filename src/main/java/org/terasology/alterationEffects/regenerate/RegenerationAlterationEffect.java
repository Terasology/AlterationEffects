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
import org.terasology.alterationEffects.OnEffectModifyEvent;
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

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        //applyEffect(instigator, entity, "", "", magnitude, duration);

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


        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this, ""));
        long modifiedDuration = 0;

        if (!effectModifyEvent.isConsumed()) {
            float modifiedMagnitude = effectModifyEvent.getMagnitudeResultValue();
            modifiedDuration = effectModifyEvent.getShortestDuration();

            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                regeneration.regenerationAmount = (int) modifiedMagnitude;
            }

            //delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.REGENERATION, modifiedDuration);
        }

        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            String effectID = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.REGENERATION + "|" + effectID, duration);
        }

    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String effectID, String id, float magnitude, long duration) {
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


        OnEffectModifyEvent effectModifyEvent = entity.send(new OnEffectModifyEvent(instigator, entity, 0, 0, this, ""));
        long modifiedDuration = 0;

        if (!effectModifyEvent.isConsumed()) {
            float modifiedMagnitude = effectModifyEvent.getMagnitudeResultValue();
            modifiedDuration = effectModifyEvent.getShortestDuration();

            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                regeneration.regenerationAmount = (int) modifiedMagnitude;
            }

            //delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.REGENERATION, modifiedDuration);
        }

        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.REGENERATION + "|" + effectID, duration);
        }
    }
}
