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
import org.terasology.alterationEffects.OnEffectRemoveEvent;
import org.terasology.engine.context.Context;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.module.health.events.BeforeDamagedEvent;
import org.terasology.engine.registry.In;

import java.util.regex.Pattern;

/**
 * This authority system manages all the resist damage effects currently in-effect across all entities. By that, it
 * handles what course of action to take when one expires, and interactions with incoming damage.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class ResistDamageAuthoritySystem extends BaseComponentSystem {
    @In
    private Time time;
    @In
    private EntityManager entityManager;
    @In
    private Context context;

    /**
     * When one of this entity's resist damage effects expire, remove it from the resist damage effects map and
     * recalculate the total magnitude for this damage type.
     *
     * @param event         Event that indicates that the delayed action has expired.
     * @param entity        Entity that has the resist damage component.
     * @param component     Stores information of all the entity's current damage resistances.
     */
    @ReceiveEvent
    public void expireResistDamageEffect(DelayedActionTriggeredEvent event, EntityRef entity, ResistDamageComponent component) {
        final String actionId = event.getActionId();

        // First, make sure this expired event is actually part of the AlterationEffects module.
        if (actionId.startsWith(AlterationEffects.EXPIRE_TRIGGER_PREFIX)) {
            // Make sure that this effect actionID actually has four parts to it (before the effectID). If not, return,
            // as this is the wrong format for this kind of AlterationEffect.
            String[] split = actionId.split(":");
            if (split.length != 4) {
                return;
            }

            // Remove the expire trigger prefix and store the resultant String into effectNamePlusID.
            String effectNamePlusID = actionId.substring(AlterationEffects.EXPIRE_TRIGGER_PREFIX.length());

            // Split the effectNamePlusID into two parts. The first part will contain the AlterationEffect's name and
            // the ID, and the second part will contain the effectID.
            String[] parts = effectNamePlusID.split(Pattern.quote("|"), 2);

            String effectID = "";
            String damageID = "";

            // If there are two items in the parts, set the damageID and effectID accordingly.
            if (parts.length == 2) {
                // Split parts[0] into two items using ':' as a delimiter. The first item will contain the
                // AlterationEffect name, and the second item will contain the damage type (or ID).
                damageID = parts[0].split(":")[1];
                effectID = parts[1];
            }

            // If this DelayedActionTriggeredEvent corresponds to this particular ResistDamage effect.
            if (split[2].equalsIgnoreCase(AlterationEffects.RESIST_DAMAGE)) {
                // Remove the ResistDamageEffect from the damage resistances map.
                component.rdes.remove(damageID);

                // Create a new resist damage alteration effect using the current context. Then, send out an event
                // alerting the other effect-related systems that this particular resist damage effect has been
                // removed.
                ResistDamageAlterationEffect resistDamageAlterationEffect = new ResistDamageAlterationEffect(context);
                entity.send(new OnEffectRemoveEvent(entity, entity, resistDamageAlterationEffect, effectID, damageID));

                // Re-apply the resist damage effect of this particular effect type so that if there are any modifiers
                // still in effect, they'll be recalculated and reapplied to the entity correctly.
                resistDamageAlterationEffect.applyEffect(entity, entity, damageID, 0, 0);

                // If the size of the damage resistances map is zero and the resist damage component doesn't exist
                // anymore, remove it from the entity.
                if (component.rdes.size() == 0 && component != null) {
                    entity.removeComponent(ResistDamageComponent.class);
                }
            }
        }
    }

    /**
     * Upon getting a damage event and the entity on the receiving end has a damage resistance, check to see if the
     * incoming damage type matches the resistance. If so, reduce the damage.
     *
     * @param event         Event with information of the incoming damage.
     * @param entity        Entity that the damage is going to be dealt to.
     * @param component     Stores information of all the entity's current damage resistances.
     */
    @ReceiveEvent
    public void resistDamageOfType(BeforeDamagedEvent event, EntityRef entity, ResistDamageComponent component) {
        String damageType = event.getDamageType().getName();

        // If the damage type matches one of the resistances.
        if (component.rdes.containsKey(damageType.split(":", 2)[1])) {
            // Get the details of the resistance.
            ResistDamageEffect rdEffect = component.rdes.get(damageType.split(":", 2)[1]);

            // If the resistance amount is greater than the total damage amount, nullify all of the damage. Otherwise,
            // subtract the resistance from the damage.
            if (rdEffect.resistAmount >= event.getResultValue()) {
                event.multiply(0);
            }
            else {
                event.add(-rdEffect.resistAmount);
            }
        }
    }
}
