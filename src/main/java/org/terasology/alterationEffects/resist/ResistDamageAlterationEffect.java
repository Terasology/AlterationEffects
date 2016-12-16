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
package org.terasology.alterationEffects.resist;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.damageOverTime.DamageOverTimeComponent;
import org.terasology.alterationEffects.damageOverTime.DamageOverTimeEffect;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.math.TeraMath;

public class ResistDamageAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    public ResistDamageAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "Default", magnitude, duration);
    }

    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        ResistDamageComponent dot = entity.getComponent(ResistDamageComponent.class);
        if (dot == null) {
            dot = new ResistDamageComponent();
            dot.resistAmount = TeraMath.floorToInt(magnitude);
            entity.addComponent(dot);
        } else {
            dot.resistAmount = TeraMath.floorToInt(magnitude);
            entity.addComponent(dot);
        }

        ResistDamageEffect resEffect = new ResistDamageEffect();
        resEffect.resistAmount = TeraMath.floorToInt(magnitude);
        resEffect.resistType = id;

        if (dot.rdes.get(id) == null) {
            dot.rdes.put(id, resEffect);
        } else {
            dot.rdes.replace(id, resEffect);
        }

        entity.saveComponent(dot);

        if (duration != AlterationEffects.DURATION_INDEFINITE) {
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.RESIST_DAMAGE + ":" + id, duration);
        }
    }
}
