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

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.health.DoHealEvent;
import org.terasology.logic.health.HealthComponent;
import org.terasology.registry.In;

/**
 * This authority system defines how the regeneration effect works.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class RegenerationAuthoritySystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final int CHECK_INTERVAL = 100;
    private static final int REGENERATION_TICK = 1000;

    private long lastUpdated;

    @In
    private Time time;
    @In
    private EntityManager entityManager;

    /**
     * Regenerates the entity's health over time during an engine update.
     *
     * @param delta The time (in seconds) since the last engine update
     */
    @Override
    public void update(float delta) {
        final long currentTime = time.getGameTimeInMs();
        if (currentTime >= lastUpdated + CHECK_INTERVAL) {
            lastUpdated = currentTime;

            for (EntityRef entity : entityManager.getEntitiesWith(RegenerationComponent.class, HealthComponent.class)) {
                final RegenerationComponent component = entity.getComponent(RegenerationComponent.class);
                if (currentTime >= component.lastRegenerationTime + REGENERATION_TICK) {
                    int multiplier = (int) (currentTime - component.lastRegenerationTime) / REGENERATION_TICK;
                    component.lastRegenerationTime = component.lastRegenerationTime + REGENERATION_TICK * multiplier;
                    entity.saveComponent(component);
                    entity.send(new DoHealEvent(component.regenerationAmount * multiplier));
                }
            }
        }
    }
}
