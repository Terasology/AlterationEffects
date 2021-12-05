// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.characters.AffectItemUseCooldownTimeEvent;
import org.terasology.engine.logic.characters.AffectJumpForceEvent;
import org.terasology.engine.logic.characters.AffectMultiJumpEvent;
import org.terasology.engine.logic.characters.GetMaxSpeedEvent;
import org.terasology.engine.logic.characters.MovementMode;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

/**
 * This system manages the effects of the various speed or movement-related alteration effects on entities.
 */
@RegisterSystem
public class SpeedAlterationSystem extends BaseComponentSystem {
    /**
     * When an entity tries to move in any direction, modify the speed based on what effects are being applied to the entity.
     *
     * @param event Stores information on what the max speed currently is and collects potential speed modifiers.
     * @param entityRef The entity trying to move.
     */
    @ReceiveEvent
    public void modifySpeed(GetMaxSpeedEvent event, EntityRef entityRef) {
        // If the entity's stunned, prevent it from moving.
        if (entityRef.hasComponent(StunComponent.class)) {
            event.multiply(0);
        } else if (event.getMovementMode() == MovementMode.WALKING && entityRef.hasComponent(WalkSpeedComponent.class)) {
            // If the entity is walking and they have a walk speed effect, boost their walking speed by the multiplier.
            event.multiply(entityRef.getComponent(WalkSpeedComponent.class).multiplier);
        } else if (event.getMovementMode() == MovementMode.SWIMMING && entityRef.hasComponent(SwimSpeedComponent.class)) {
            // If the entity is swimming and they have a swim speed effect, boost their swimming speed by the multiplier.
            event.multiply(entityRef.getComponent(SwimSpeedComponent.class).multiplier);
        }

        // If the entity has a glue effect on them, set the movement speed to be 90% of what it was.
        if (entityRef.hasComponent(GlueComponent.class)) {
            event.multiply(0.9f);
        }
    }

    /**
     * When an entity tries to jump, modify the jump speed based on what effects are being applied to the entity.
     *
     * @param event Stores information on the jump force and allows other systems to modify it.
     * @param entityRef The entity trying to jump.
     */
    @ReceiveEvent
    public void modifyJumpSpeed(AffectJumpForceEvent event, EntityRef entityRef) {
        // If the entity's stunned, prevent it from moving.
        if (entityRef.hasComponent(StunComponent.class)) {
            event.multiply(0);
        } else if (entityRef.hasComponent(JumpSpeedComponent.class)) {
            // If the entity is beginning to jump and they have a jump speed effect, boost their jumping speed by the
            // multiplier.
            event.multiply(entityRef.getComponent(JumpSpeedComponent.class).multiplier);
        }

        // If the entity has a glue effect on them, prevent them from jumping at all.
        if (entityRef.hasComponent(GlueComponent.class)) {
            event.multiply(0);
        }
    }

    /**
     * When an entity tries to jump, modify the max number jumps (before hitting the ground) based on what effects are being applied to the
     * entity.
     *
     * @param event Stores information on the number of jumps and allows other systems to modify it.
     * @param entityRef The entity trying to jump.
     */
    @ReceiveEvent
    public void modifyMultiJump(AffectMultiJumpEvent event, EntityRef entityRef) {
        // If the entity has a multi jump effect, multiply the max number of jumps by the multiplier.
        if (entityRef.hasComponent(MultiJumpComponent.class)) {
            event.multiply(entityRef.getComponent(MultiJumpComponent.class).multiplier);
        }
    }

    /**
     * When an entity tries to use an item, modify the cooldown time based on what effects are being applied to the entity.
     *
     * @param event Stores information on the item's cooldown and allows other systems to modify it.
     * @param entityRef The entity trying to use an item.
     */
    @ReceiveEvent
    public void modifyItemUseSpeed(AffectItemUseCooldownTimeEvent event, EntityRef entityRef) {
        // If the entity has a item use speed effect, boost the item use speed by the multiplier. However, only do this
        // if the multiplier is greater than 0, so that zero or negative use rates are noy possible.
        if (entityRef.hasComponent(ItemUseSpeedComponent.class)) {
            ItemUseSpeedComponent itemUseSpeed = entityRef.getComponent(ItemUseSpeedComponent.class);
            if (itemUseSpeed.multiplier > 0) {
                event.multiply(itemUseSpeed.multiplier);
            }
        }
    }
}
