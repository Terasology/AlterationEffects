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

import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.HealthComponent;
import org.terasology.registry.In;
import org.terasology.utilities.Assets;

@RegisterSystem(value = RegisterMode.AUTHORITY)
public class BoostAuthoritySystem extends BaseComponentSystem {
    private static final int CHECK_INTERVAL = 250;
    private static final int DAMAGE_TICK = 1000;

    private long lastUpdated;

    @In
    private Time time;
    @In
    private EntityManager entityManager;

    /**
     * Removes the Health Boost effect from the entity and ensures that their current health does not exceed their maximum health
     *
     * @param event the event corresponding to the removal of the Health Boost effect
     * @param entity the entity affected by the HealthBoost effect
     * @param hBoost the HealthBoostComponent on the entity affected by the Health Boost effect
     */
    @ReceiveEvent
    public void removeHealthBoost(BeforeDeactivateComponent event, EntityRef entity, HealthBoostComponent hBoost) {
        HealthComponent h = entity.getComponent(HealthComponent.class);
        h.maxHealth = Math.round(h.maxHealth / (1f + 0.01f*hBoost.boostAmount));

        if (h.currentHealth > h.maxHealth) {
            h.currentHealth = h.maxHealth;
        }
    }
}