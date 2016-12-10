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
package org.terasology.alterationEffects.breath;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.BeforeDamagedEvent;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockUri;

/**
 * This authority system defines hpw the water breathing effect works.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class WaterBreathingAuthoritySystem extends BaseComponentSystem {
    /**
     * This method makes sure that damage from water is cancelled when the effect is enabled on an entity.
     *
     * @param event     The event containing the state before the damage is received
     * @param entity    The entity on which the damage is to be negated
     * @param component The component corresponding to the water breathing effect on the entity
     */
    @ReceiveEvent
    public void cancelDamageFromWater(BeforeDamagedEvent event, EntityRef entity, WaterBreathingComponent component) {
        final BlockComponent block = event.getInstigator().getComponent(BlockComponent.class);
        if (block != null && block.getBlock().getURI().equals(new BlockUri("Core:Water"))) {
            event.consume();
        }
    }
}
