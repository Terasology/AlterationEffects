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
package org.terasology.alterationEffects.resist;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.logic.health.BeforeDamagedEvent;
import org.terasology.registry.In;

/**
 * This authority system defines how the resist damage effect works.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class ResistDamageAuthoritySystem extends BaseComponentSystem {
    private static final int CHECK_INTERVAL = 100;
    private static final int DAMAGE_TICK = 1000;

    private long lastUpdated;

    @In
    private Time time;
    @In
    private EntityManager entityManager;

    /**
     * Removes a resist damage effect from an entity.
     *
     * @param event     The event corresponding to the triggering of the expiry of the effect
     * @param entity    The entity from which the effect is to be removed
     * @param component The resist damage component associated with the effect to be removed
     */
    @ReceiveEvent
    public void expireResistDamageEffect(DelayedActionTriggeredEvent event, EntityRef entity, ResistDamageComponent component) {
        final String actionId = event.getActionId();
        if (actionId.startsWith(AlterationEffects.EXPIRE_TRIGGER_PREFIX)) {
            String effectName = actionId.substring(AlterationEffects.EXPIRE_TRIGGER_PREFIX.length());
            String[] split = actionId.split(":");

            if (split.length != 4) {
                return;
            }

            if (split[2].equalsIgnoreCase(AlterationEffects.RESIST_DAMAGE)) {
                component.rdes.remove(split[3]);

                if (component.rdes.size() == 0 && component != null) {
                    entity.removeComponent(ResistDamageComponent.class);
                }
            }
        }
    }

    /**
     * Resists damage of certain types (specified by the active resist damage effect).
     * @param event     The event containing the state before the damage was dealt
     * @param entity    The entity on which damage is being dealt
     * @param component THe resist damage component associated with the effect on the entity
     */
    @ReceiveEvent
    public void resistDamageOfType(BeforeDamagedEvent event, EntityRef entity, ResistDamageComponent component) {
        String damageType = event.getDamageType().getName();

        if (component.rdes.containsKey(damageType.split(":", 2)[1])) {
            ResistDamageEffect rdEffect = component.rdes.get(damageType.split(":", 2)[1]);

            if (rdEffect.resistAmount >= event.getResultValue()) {
                event.multiply(0);
            }
            else {
                event.add(-rdEffect.resistAmount);
            }
        }
    }
}