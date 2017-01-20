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
package org.terasology.alterationEffects.speed;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.AffectItemUseCooldownTimeEvent;
import org.terasology.logic.characters.AffectJumpForceEvent;
import org.terasology.logic.characters.AffectMultiJumpEvent;
import org.terasology.logic.characters.GetMaxSpeedEvent;
import org.terasology.logic.characters.MovementMode;

/**
 * This system manages the effects of the various speed or movement-related alteration effects on entities.
 */
@RegisterSystem
public class SpeedAlterationSystem extends BaseComponentSystem {
    /**
     * When an entity tries to move in any direction, modify the speed based on what effects are being applied to the
     * entity.
     *
     * @param event         Stores information on what the max speed currently is and collects potential speed
     *                      modifiers.
     * @param entityRef     The entity trying to move.
     */
    @ReceiveEvent
    public void modifySpeed(GetMaxSpeedEvent event, EntityRef entityRef) {
        // If the entity's stunned, prevent it from moving.
        if (entityRef.hasComponent(StunComponent.class)) {
            event.multiply(0);
        }
        // If the entity is walking and they have a walk speed effect, boost their walking speed by the multiplier.
        else if (event.getMovementMode() == MovementMode.WALKING && entityRef.hasComponent(WalkSpeedComponent.class)) {
            event.multiply(entityRef.getComponent(WalkSpeedComponent.class).multiplier);
        }
        // If the entity is swimming and they have a swim speed effect, boost their swimming speed by the multiplier.
        else if (event.getMovementMode() == MovementMode.SWIMMING && entityRef.hasComponent(SwimSpeedComponent.class)) {
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
     * @param event         Stores information on the jump force and allows other systems to modify it.
     * @param entityRef     The entity trying to jump.
     */
    @ReceiveEvent
    public void modifyJumpSpeed(AffectJumpForceEvent event, EntityRef entityRef) {
        // If the entity's stunned, prevent it from moving.
        if (entityRef.hasComponent(StunComponent.class)) {
            event.multiply(0);
        }
        // If the entity is beginning to jump and they have a jump speed effect, boost their jumping speed by the
        // multiplier.
        else if (entityRef.hasComponent(JumpSpeedComponent.class)) {
            event.multiply(entityRef.getComponent(JumpSpeedComponent.class).multiplier);
        }

        // If the entity has a glue effect on them, prevent them from jumping at all.
        if (entityRef.hasComponent(GlueComponent.class)) {
            event.multiply(0);
        }
    }

    /**
     * When an entity tries to jump, modify the max number jumps (before hitting the ground) based on what effects are
     * being applied to the entity.
     *
     * @param event         Stores information on the number of jumps and allows other systems to modify it.
     * @param entityRef     The entity trying to jump.
     */
    @ReceiveEvent
    public void modifyMultiJump(AffectMultiJumpEvent event, EntityRef entityRef) {
        // If the entity has a multi jump effect, multiply the max number of jumps by the multiplier.
        if (entityRef.hasComponent(MultiJumpComponent.class)) {
            event.multiply(entityRef.getComponent(MultiJumpComponent.class).multiplier);
        }
    }

    /**
     * When an entity tries to use an item, modify the cooldown time based on what effects are being applied to the
     * entity.
     *
     * @param event         Stores information on the item's cooldown and allows other systems to modify it.
     * @param entityRef     The entity trying to use an item.
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
