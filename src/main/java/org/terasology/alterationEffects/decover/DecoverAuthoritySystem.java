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
package org.terasology.alterationEffects.decover;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.BeforeHealEvent;

/**
 * This handles events pertaining to the decover effect
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class DecoverAuthoritySystem extends BaseComponentSystem {
    /**
     * Prevents the entity from being healed
     *
     * @param event     the event called before an entity is healed
     * @param entity    the entity with the decover effect
     * @param component the decover component added to entities with the decover effect
     */
    @ReceiveEvent
    public void cancelHeal(BeforeHealEvent event, EntityRef entity, DecoverComponent component) {
        event.consume();
    }
}
