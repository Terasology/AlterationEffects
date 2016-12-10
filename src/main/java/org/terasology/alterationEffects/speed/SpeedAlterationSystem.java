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
 * This system defines how various speed modifications work.
 */
@RegisterSystem
public class SpeedAlterationSystem extends BaseComponentSystem {

    /**
     * Modifies the walk/swim speed of an entity.
     *
     * @param event     The event sent when the max speed of the entity is being accessed
     * @param entityRef The entity whose max speed is to be modified
     */
    @ReceiveEvent
    public void modifySpeed(GetMaxSpeedEvent event, EntityRef entityRef) {
        if (entityRef.hasComponent(StunComponent.class)) {
            event.multiply(0);
        } else if (event.getMovementMode() == MovementMode.WALKING && entityRef.hasComponent(WalkSpeedComponent.class)) {
            event.multiply(entityRef.getComponent(WalkSpeedComponent.class).multiplier);
        } else if (event.getMovementMode() == MovementMode.SWIMMING && entityRef.hasComponent(SwimSpeedComponent.class)) {
            event.multiply(entityRef.getComponent(SwimSpeedComponent.class).multiplier);
        }
        
        if (entityRef.hasComponent(GlueComponent.class)) {
            event.multiply(0.9f);
        }
    }

    /**
     * Modifies the jump speed of an entity.
     *
     * @param event     The event sent when the jump force of the entity is being accessed
     * @param entityRef The entity whose jump speed is to be modified
     */
    @ReceiveEvent
    public void modifyJumpSpeed(AffectJumpForceEvent event, EntityRef entityRef) {
        if (entityRef.hasComponent(StunComponent.class)) {
            event.multiply(0);
        } else if (entityRef.hasComponent(JumpSpeedComponent.class)) {
            event.multiply(entityRef.getComponent(JumpSpeedComponent.class).multiplier);
        }
        
        if (entityRef.hasComponent(GlueComponent.class)) {
            event.multiply(0);
        }
    }

    /**
     * Modifies the multi-jump attribute of an entity.
     *
     * @param event     The event sent when the multi-jump attribute is being accessed
     * @param entityRef The entity whose multi-jump attribute is to be changed
     */
    @ReceiveEvent
    public void modifyMultiJump(AffectMultiJumpEvent event, EntityRef entityRef) {
        if (entityRef.hasComponent(MultiJumpComponent.class)) {
            event.multiply(entityRef.getComponent(MultiJumpComponent.class).multiplier);
        }
    }

    /**
     * Modifies the item use speed of an entity.
     *
     * @param event     The event sent when the item use cooldown is being accessed
     * @param entityRef The entity whose item use speed is to be changed.
     */
    @ReceiveEvent
    public void modifyItemUseSpeed(AffectItemUseCooldownTimeEvent event, EntityRef entityRef) {
        if (entityRef.hasComponent(ItemUseSpeedComponent.class)) {
            ItemUseSpeedComponent itemUseSpeed = entityRef.getComponent(ItemUseSpeedComponent.class);
            if (itemUseSpeed.multiplier > 0) {
                event.multiply(itemUseSpeed.multiplier);
            }
        }
    }
}
