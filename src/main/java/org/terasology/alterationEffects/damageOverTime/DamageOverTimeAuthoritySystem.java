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

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.HealthComponent;
import org.terasology.registry.In;
import org.terasology.utilities.Assets;

/**
 * This authority system defines how the damage over time alteration effect works.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class DamageOverTimeAuthoritySystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final int CHECK_INTERVAL = 100;
    private static final int DAMAGE_TICK = 1000;

    private long lastUpdated;

    @In
    private Time time;
    @In
    private EntityManager entityManager;

    /**
     * Removes a damage over time effect from a given entity.
     *
     * @param event     The event corresponding to the triggering of the expiry of the effect
     * @param entity    The entity from which the effect is to be removed
     * @param component The damage over time component associated with the effect to be removed
     */
    @ReceiveEvent
    public void expireDOTEffect(DelayedActionTriggeredEvent event, EntityRef entity, DamageOverTimeComponent component) {
        final String actionId = event.getActionId();
        if (actionId.startsWith(AlterationEffects.EXPIRE_TRIGGER_PREFIX)) {
            String effectName = actionId.substring(AlterationEffects.EXPIRE_TRIGGER_PREFIX.length());
            String[] split = actionId.split(":");

            if (split.length != 4) {
                return;
            }

            if (split[2].equalsIgnoreCase(AlterationEffects.DAMAGE_OVER_TIME)) {
                component.dots.remove(split[3]);

                if (component.dots.size() == 0 && component != null) {
                    entity.removeComponent(DamageOverTimeComponent.class);
                }
            }
        }
    }

    /**
     * Deals damage to the entity (on which the effect is applied) during an engine update.
     *
     * @param delta The time (in seconds) since the last engine update
     */
    @Override
    public void update(float delta) {
        final long currentTime = time.getGameTimeInMs();
        if (currentTime >= lastUpdated + CHECK_INTERVAL) {
            lastUpdated = currentTime;


            /*
            for (EntityRef entity : entityManager.getEntitiesWith(DamageOverTimeComponent.class, HealthComponent.class)) {
                final DamageOverTimeComponent component = entity.getComponent(DamageOverTimeComponent.class);
                if (currentTime >= component.lastDamageTime + DAMAGE_TICK) {
                    int multiplier = (int) (currentTime - component.lastDamageTime) / DAMAGE_TICK;
                    component.lastDamageTime = component.lastDamageTime + DAMAGE_TICK * multiplier;
                    entity.saveComponent(component);
                    entity.send(new DoDamageEvent(component.damageAmount * multiplier,
                            Assets.getPrefab("AlterationEffects:damageOverTimeDamage").get()));
                }
            }
            */

            for (EntityRef entity : entityManager.getEntitiesWith(DamageOverTimeComponent.class, HealthComponent.class)) {
                final DamageOverTimeComponent component = entity.getComponent(DamageOverTimeComponent.class);

                for (DamageOverTimeEffect dotEffect : component.dots.values()) {
                    if (currentTime >= dotEffect.lastDamageTime + DAMAGE_TICK) {
                        int multiplier = (int) (currentTime - dotEffect.lastDamageTime) / DAMAGE_TICK;
                        dotEffect.lastDamageTime = dotEffect.lastDamageTime + DAMAGE_TICK * multiplier;
                        entity.saveComponent(component);
                        entity.send(new DoDamageEvent(dotEffect.damageAmount * multiplier,
                                Assets.getPrefab(dotEffect.damageType).get()));
                    }
                }
            }
        }
    }
}
