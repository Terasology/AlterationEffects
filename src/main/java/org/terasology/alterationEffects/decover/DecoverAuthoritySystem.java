// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.decover;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.health.events.BeforeRestoreEvent;

/**
 * This authority system handles events for entities with the decover effect.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class DecoverAuthoritySystem extends BaseComponentSystem {
    /**
     * This prevents the entity from being healed.
     *
     * @param event     The event called before an entity is healed.
     * @param entity    The entity with the decover effect.
     * @param component The decover component added to entities with the decover effect.
     */
    @ReceiveEvent
    public void cancelHeal(BeforeRestoreEvent event, EntityRef entity, DecoverComponent component) {
        event.consume();
    }
}
