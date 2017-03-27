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
package org.terasology.alterationEffects.buff;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.OnEffectRemoveEvent;
import org.terasology.context.Context;
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

import java.util.regex.Pattern;

/**
 * This authority system manages all the buff damage effects currently in-effect across all entities. By that, it
 * handles what course of action to take when one expires, and interactions with outgoing damage.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class BuffDamageAuthoritySystem extends BaseComponentSystem {
    @In
    private Time time;
    @In
    private EntityManager entityManager;
    @In
    private Context context;

    /**
     * When one of this entity's buff damage effects expire, remove it from the damage buff effects map and
     * recalculate the total magnitude for this damage type.
     *
     * @param event         Event that indicates that the delayed action has expired.
     * @param entity        Entity that has the buff damage component.
     * @param component     Stores information of all the entity's current damage buffs.
     */
    @ReceiveEvent
    public void expireBuffDamageEffect(DelayedActionTriggeredEvent event, EntityRef entity, BuffDamageComponent component) {
        final String actionId = event.getActionId();

        // First, make sure this expired event is actually part of the AlterationEffects module.
        if (actionId.startsWith(AlterationEffects.EXPIRE_TRIGGER_PREFIX)) {
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

            // Make sure that this effect actionID actually has four parts to it (before the effectID). If not, return.
            String[] split = actionId.split(":");
            if (split.length != 4) {
                return;
            }

            // If this DelayedActionTriggeredEvent corresponds to this particular BuffDamage effect.
            if (split[2].equalsIgnoreCase(AlterationEffects.BUFF_DAMAGE)) {
                // Remove the BuffDamageEffect from the damage buffs map.
                component.bdes.remove(damageID);

                // Create a new resist damage alteration effect using the current context. Then, send out an event
                // alerting the other effect-related systems that this particular buff damage effect has been
                // removed.
                BuffDamageAlterationEffect buffDamageAlterationEffect = new BuffDamageAlterationEffect(context);
                entity.send(new OnEffectRemoveEvent(entity, entity, buffDamageAlterationEffect, effectID, damageID));

                // Re-apply the buff damage effect of this particular effect type so that if there are any modifiers
                // still in effect, they'll be recalculated and reapplied to the entity correctly.
                buffDamageAlterationEffect.applyEffect(entity, entity, damageID, 0, 0);

                // If the size of the damage buffs map is zero and the buff damage component doesn't exist
                // anymore, remove it from the entity.
                if (component.bdes.size() == 0 && component != null) {
                    entity.removeComponent(BuffDamageComponent.class);
                }
            }
        }
    }

    /**
     * Upon getting a damage event and the entity on the sending end has a damage buff, check to see if the incoming
     * damage type matches the buff's type. If so, increase the damage by the buff's amount.
     *
     * @param event         Event with information of the outgoing damage.
     * @param entity        Entity that's dealing the damage.
     * @param component     Stores information of all the entity's current damage buffs.
     */
    @ReceiveEvent
    public void buffDamageOfType(BeforeDamagedEvent event, EntityRef entity, BuffDamageComponent component) {
        String damageType = event.getDamageType().getName();

        // If the damage type matches one of the buffs.
        if (component.bdes.containsKey(damageType.split(":", 2)[1])) {
            // Get the details of the buff.
            BuffDamageEffect bdEffect = component.bdes.get(damageType.split(":", 2)[1]);

            // Add the buff amount to the damage.
            event.add(bdEffect.buffAmount);
        }
    }
}
