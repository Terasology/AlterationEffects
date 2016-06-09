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
import org.terasology.logic.characters.AffectJumpForceEvent;
import org.terasology.logic.characters.GetMaxSpeedEvent;
import org.terasology.logic.characters.MovementMode;

@RegisterSystem
public class SpeedAlterationSystem extends BaseComponentSystem {
    @ReceiveEvent
    public void modifySpeed(GetMaxSpeedEvent event, EntityRef entityRef) {
        if (event.getMovementMode() == MovementMode.WALKING && entityRef.hasComponent(WalkSpeedComponent.class)) {
            event.multiply(entityRef.getComponent(WalkSpeedComponent.class).multiplier);
        } else if (event.getMovementMode() == MovementMode.SWIMMING && entityRef.hasComponent(SwimSpeedComponent.class)) {
            event.multiply(entityRef.getComponent(SwimSpeedComponent.class).multiplier);
        }
    }

    @ReceiveEvent
    public void modifyJump(AffectJumpForceEvent event, EntityRef entityRef) {
        if (entityRef.hasComponent(JumpSpeedComponent.class)) {
            event.multiply(entityRef.getComponent(JumpSpeedComponent.class).multiplier);
        }
    }
}
