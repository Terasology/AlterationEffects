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
package org.terasology.alterationEffects.boost;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.health.HealthComponent;
import org.terasology.math.TeraMath;

public class HealthBoostAlterationEffect implements AlterationEffect {

    private final Time time;
    private final DelayManager delayManager;

    public HealthBoostAlterationEffect(Context context) {
        this.time = context.get(Time.class);
        this.delayManager = context.get(DelayManager.class);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        HealthBoostComponent hbot = entity.getComponent(HealthBoostComponent.class);
        if (hbot == null) {
            hbot = new HealthBoostComponent();
            hbot.boostAmount = TeraMath.floorToInt(magnitude);
            hbot.lastUseTime = time.getGameTimeInMs();

            HealthComponent h = entity.getComponent(HealthComponent.class);
            h.maxHealth = Math.round(h.maxHealth * (1 + 0.01f * hbot.boostAmount));

            entity.addComponent(hbot);
        } else {
            HealthComponent h = entity.getComponent(HealthComponent.class);

            delayManager.cancelDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.MAX_HEALTH_BOOST);
            entity.removeComponent(HealthBoostComponent.class);

            hbot.boostAmount = TeraMath.floorToInt(magnitude);
            hbot.lastUseTime = time.getGameTimeInMs();
            h.maxHealth = Math.round(h.maxHealth * (1 + 0.01f * hbot.boostAmount));

            entity.addComponent(hbot);
        }

        if (duration != AlterationEffects.DURATION_INDEFINITE) {
            delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.MAX_HEALTH_BOOST, duration);
        }
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
