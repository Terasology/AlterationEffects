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
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.HealthComponent;
import org.terasology.registry.In;

/**
 * This authority system manages all the health boost effects currently in-effect across all entities.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class BoostAuthoritySystem extends BaseComponentSystem {
    @In
    private Time time;
    @In
    private EntityManager entityManager;

    /**
     * This will remove the health boost from an entity just before its health boost component is removed.
     *
     * @param event     Event that indicates that the HealthBoostComponent will be deactivated and removed.
     * @param entity    Entity that has the health boost component.
     * @param hBoost    The health boost component. Used as delimiter/filter as well as for its sole variable.
     */
    @ReceiveEvent
    public void removeHealthBoost(BeforeDeactivateComponent event, EntityRef entity, HealthBoostComponent hBoost) {
        // Get the HealthComponent from this entity, and set it's max health back to the original value.
        HealthComponent h = entity.getComponent(HealthComponent.class);
        h.maxHealth = Math.round(h.maxHealth / (1f + 0.01f*hBoost.boostAmount));

        // If the current health is greater than the new max health, set the current health value to be the max health.
        if (h.currentHealth > h.maxHealth) {
            h.currentHealth = h.maxHealth;
        }
    }
}
