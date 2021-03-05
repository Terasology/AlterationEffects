// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.breath;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.event.BeforeDamagedEvent;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockUri;

/**
 * This authority system manages all the water breathing effects currently in-effect across all entities.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class WaterBreathingAuthoritySystem extends BaseComponentSystem {
    /**
     * Cancel any potential oncoming water or drowning damage when the water breathing effect is currently in-effect.
     *
     * @param event         Event with information of the incoming damage.
     * @param entity        Entity that the damage was going to be dealt to.
     * @param component     Reference to the entity's water breathing component. Used as a delimiter/filter.
     */
    @ReceiveEvent
    public void cancelDamageFromWater(BeforeDamagedEvent event, EntityRef entity, WaterBreathingComponent component) {
        final BlockComponent block = event.getInstigator().getComponent(BlockComponent.class);
        if (block != null && block.getBlock().getURI().equals(new BlockUri("CoreAssets:Water"))) {
            event.consume();
        }
    }
}
