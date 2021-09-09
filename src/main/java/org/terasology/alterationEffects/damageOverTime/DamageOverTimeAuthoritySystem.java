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
package org.terasology.alterationEffects.damageOverTime;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.OnEffectRemoveEvent;
import org.terasology.engine.context.Context;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.Assets;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.health.components.HealthComponent;
import org.terasology.module.health.events.DoDamageEvent;

import java.util.regex.Pattern;

/**
 * This authority system manages all the damage over time (DOT) effects currently in-effect across all entities. By
 * that, it handles what course of action to take when one expires, and applies the DOT damage at regular intervals.
 */
@RegisterSystem(value = RegisterMode.AUTHORITY)
public class DamageOverTimeAuthoritySystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    /** Integer storing when to check each effect. */
    private static final int CHECK_INTERVAL = 100;

    /** Integer storing when to apply DOT damage */
    private static final int DAMAGE_TICK = 1000;

    /** Last time the list of DOT effects were checked. */
    private long lastUpdated;

    @In
    private Time time;
    @In
    private EntityManager entityManager;
    @In
    private Context context;

    /**
     * When one of this entity's DOT effects expire, remove it from the DOT effects map and recalculate the total
     * magnitude for this damage type.
     *
     * @param event         Event that indicates that the delayed action has expired.
     * @param entity        Entity that has the damage over time component.
     * @param component     Stores information of all the entity's current DOTs.
     */
    @ReceiveEvent
    public void expireDOTEffect(DelayedActionTriggeredEvent event, EntityRef entity, DamageOverTimeComponent component) {
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

            // If this DelayedActionTriggeredEvent corresponds to this particular DamageOverTime effect.
            if (split[2].equalsIgnoreCase(AlterationEffects.DAMAGE_OVER_TIME)) {
                // Remove the DamageOverTimeEffect from the ailments map.
                component.dots.remove(damageID);

                // Remove the corresponding effectID from the DOT effectIDMap. As this particular modifier is expiring,
                // we don't need to store it here anymore.
                if (component.effectIDMap.get(damageID) != null) {
                    component.effectIDMap.get(damageID).remove(effectID);
                }

                // Create a new DOT alteration effect using the current context. Then, send out an event alerting the
                // other effect-related systems that this particular resist damage effect has been removed.
                DamageOverTimeAlterationEffect dotAlterationEffect = new DamageOverTimeAlterationEffect(context);
                entity.send(new OnEffectRemoveEvent(entity, entity, dotAlterationEffect, effectID, damageID));

                // Re-apply the DOT effect of this particular effect type so that if there are any modifiers still in
                // effect, they'll be recalculated and reapplied to the entity correctly.
                dotAlterationEffect.applyEffect(entity, entity, damageID, 0, 0);

                // If the size of the ongoing damages map is zero and the DamageOverTime component doesn't exist
                // anymore, remove it from the entity.
                if (component.dots.size() == 0 && component != null) {
                    entity.removeComponent(DamageOverTimeComponent.class);
                }
            }
        }
    }

    /**
     * For every update, check to see if the time's been over the CHECK_INTERVAL. If so, verify if a DAMAGE_TICK has
     * passed for every DOT effect.
     *
     * @param delta The time (in seconds) since the last engine update.
     */
    @Override
    public void update(float delta) {
        final long currentTime = time.getGameTimeInMs();

        // If the current time passes the CHECK_INTERVAL threshold, continue.
        if (currentTime >= lastUpdated + CHECK_INTERVAL) {
            // Set the lastUpdated time to be the currentTime.
            lastUpdated = currentTime;

            // For every entity with the health and DOT components, check to see if they have passed a DAMAGE_TICK. If
            // so, apply a heal event to the applicable entities with the given damageAmount.
            for (EntityRef entity : entityManager.getEntitiesWith(DamageOverTimeComponent.class, HealthComponent.class)) {
                final DamageOverTimeComponent component = entity.getComponent(DamageOverTimeComponent.class);

                // Iterate through all of the DOT effects present on thie entity, and apply them to the entity.
                for (DamageOverTimeEffect dotEffect : component.dots.values()) {
                    if (currentTime >= dotEffect.lastDamageTime + DAMAGE_TICK) {
                        // Calculate this multiplier to account for time delays.
                        int multiplier = (int) (currentTime - dotEffect.lastDamageTime) / DAMAGE_TICK;
                        dotEffect.lastDamageTime = dotEffect.lastDamageTime + DAMAGE_TICK * multiplier;

                        // Save the DOT component so that the latest changes don't get lost during exit.
                        entity.saveComponent(component);

                        // Now send the damage event to this entity with the magnitude being the damage amount times
                        // the multiplier.
                        entity.send(new DoDamageEvent(dotEffect.damageAmount * multiplier,
                                Assets.getPrefab(dotEffect.damageType).get()));
                    }
                }
            }
        }
    }
}
