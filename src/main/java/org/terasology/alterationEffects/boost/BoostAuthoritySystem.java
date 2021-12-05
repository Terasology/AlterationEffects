// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.boost;

import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.health.components.HealthComponent;

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
     * @param event Event that indicates that the HealthBoostComponent will be deactivated and removed.
     * @param entity Entity that has the health boost component.
     * @param hBoost The health boost component. Used as delimiter/filter as well as for its sole variable.
     */
    @ReceiveEvent
    public void removeHealthBoost(BeforeDeactivateComponent event, EntityRef entity, HealthBoostComponent hBoost) {
        HealthComponent h = entity.getComponent(HealthComponent.class);

        // Reverse the max health boosting effect by dividing the old boost amount.
        h.maxHealth = Math.round(h.maxHealth / (1f + 0.01f * hBoost.boostAmount));

        // If the current health is greater than the new max health, set the current health value to be the max health.
        if (h.currentHealth > h.maxHealth) {
            h.currentHealth = h.maxHealth;
        }
    }
}
