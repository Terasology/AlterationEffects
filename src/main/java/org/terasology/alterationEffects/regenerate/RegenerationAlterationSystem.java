// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.regenerate;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.health.events.BeforeRegenEvent;

import static org.terasology.alterationEffects.regenerate.RegenerationAlterationEffect.REGEN_EFFECT_ID;

@RegisterSystem
public class RegenerationAlterationSystem extends BaseComponentSystem {

    /**
     * Adds the regeneration alteration effect's regeneration amount to the base regeneration for an entity under the
     * effect. This only affects the base regeneration action. All other registered regeneration actions are ignored.
     *
     * @param event The collector event for regeneration actions, called before an entity's health is about to
     *         be regenerated.
     * @param entity The entity whose health is about to be regenerated.
     * @param regeneration The regeneration effect affecting the entity.
     */
    @ReceiveEvent
    public void beforeRegen(BeforeRegenEvent event, EntityRef entity, RegenerationComponent regeneration) {
        if (event.getId().equals(REGEN_EFFECT_ID)) {
            event.add(regeneration.regenerationAmount);
        }
    }
}
