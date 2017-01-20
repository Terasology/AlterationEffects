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
 * This authority system manages all the regeneration effects currently in-effect across all entities. By that, it
 * handles what course of action to take when one expires, and applies the regen at regular intervals.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class RegenerationAuthoritySystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    /** Integer storing when to check each effect. */
    private static final int CHECK_INTERVAL = 100;

    /** Integer storing when to apply regen health */
    private static final int REGENERATION_TICK = 1000;

    /** Last time the list of regen effects were checked. */
    private long lastUpdated;

    @In
    private Time time;
    @In
    private EntityManager entityManager;

    /**
     * For every update, check to see if the time's been over the CHECK_INTERVAL. If so, verify if a REGENERATION_TICK
     * has passed for every regeneration effect.
     *
     * @param delta The time (in seconds) since the last engine update.
     */
    @Override
    public void update(float delta) {
        final long currentTime = time.getGameTimeInMs();

        // If the current time passes the CHECK_INTERVAL threshold, continue.
        if (currentTime >= lastUpdated + CHECK_INTERVAL) {
            // Set the lastUpdated time to be the currentTime.
            lastUpdated = currentTime;

            // For every entity with the health and regeneration components, check to see if they have passed a
            // REGENERATION_TICK. If so, apply a heal event to the applicable entities with the given
            // regenerationAmount.
            for (EntityRef entity : entityManager.getEntitiesWith(RegenerationComponent.class, HealthComponent.class)) {
                final RegenerationComponent component = entity.getComponent(RegenerationComponent.class);
                if (currentTime >= component.lastRegenerationTime + REGENERATION_TICK) {
                    // Calculate this multiplier to account for time delays.
                    int multiplier = (int) (currentTime - component.lastRegenerationTime) / REGENERATION_TICK;
                    component.lastRegenerationTime = component.lastRegenerationTime + REGENERATION_TICK * multiplier;

                    // Save the regen component so that the latest changes don't get lost during exit.
                    entity.saveComponent(component);

                    // Now send the heal event to this entity with the heal strength being the regen amount times the
                    // multiplier.
                    entity.send(new DoHealEvent(component.regenerationAmount * multiplier));
                }
            }
        }
    }
}
