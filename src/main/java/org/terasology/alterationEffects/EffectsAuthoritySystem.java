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
package org.terasology.alterationEffects;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.alterationEffects.boost.HealthBoostAlterationEffect;
import org.terasology.alterationEffects.boost.HealthBoostComponent;
import org.terasology.alterationEffects.breath.WaterBreathingAlterationEffect;
import org.terasology.alterationEffects.breath.WaterBreathingComponent;
import org.terasology.alterationEffects.decover.DecoverAlterationEffect;
import org.terasology.alterationEffects.decover.DecoverComponent;
import org.terasology.alterationEffects.regenerate.RegenerationAlterationEffect;
import org.terasology.alterationEffects.regenerate.RegenerationComponent;
import org.terasology.alterationEffects.speed.GlueAlterationEffect;
import org.terasology.alterationEffects.speed.GlueComponent;
import org.terasology.alterationEffects.speed.ItemUseSpeedAlterationEffect;
import org.terasology.alterationEffects.speed.ItemUseSpeedComponent;
import org.terasology.alterationEffects.speed.JumpSpeedAlterationEffect;
import org.terasology.alterationEffects.speed.JumpSpeedComponent;
import org.terasology.alterationEffects.speed.MultiJumpAlterationEffect;
import org.terasology.alterationEffects.speed.MultiJumpComponent;
import org.terasology.alterationEffects.speed.StunAlterationEffect;
import org.terasology.alterationEffects.speed.StunComponent;
import org.terasology.alterationEffects.speed.SwimSpeedAlterationEffect;
import org.terasology.alterationEffects.speed.SwimSpeedComponent;
import org.terasology.alterationEffects.speed.WalkSpeedAlterationEffect;
import org.terasology.alterationEffects.speed.WalkSpeedComponent;
import org.terasology.context.Context;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.console.commandSystem.annotations.Command;
import org.terasology.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.logic.console.commandSystem.annotations.Sender;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This authority system manages the expiration of the basic alteration effects. Other authority systems will handle the
 * more complex effects that require more than just simply removing the effect component from the entity and informing
 * other systems.
 */
@RegisterSystem
@Share(EffectsAuthoritySystem.class)
public class EffectsAuthoritySystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(EffectsAuthoritySystem.class);
    /**
     * This will store the mapping of the effect constants to the effect components.
     */
    private Map<String, Class<? extends Component>> effectComponents = new HashMap<>();

    /**
     * This will store the mapping of the effect constants to the alteration effects.
     */
    public Map<String, AlterationEffect> alterationEffects = new HashMap<>();

    /**
     * This Context is necessary for all the AlterationEffects due to timing and use of the DelayManager.
     */
    @In
    private Context context;

    /**
     * Initialize all elements of the two maps.
     */
    @Override
    public void initialise() {
        registerEffect(AlterationEffects.WALK_SPEED, WalkSpeedComponent.class, new WalkSpeedAlterationEffect(context));
        registerEffect(AlterationEffects.SWIM_SPEED, SwimSpeedComponent.class, new SwimSpeedAlterationEffect(context));
        registerEffect(AlterationEffects.JUMP_SPEED, JumpSpeedComponent.class, new JumpSpeedAlterationEffect(context));
        registerEffect(AlterationEffects.WATER_BREATHING, WaterBreathingComponent.class,
                new WaterBreathingAlterationEffect(context));
        registerEffect(AlterationEffects.REGENERATION, RegenerationComponent.class,
                new RegenerationAlterationEffect(context));
        registerEffect(AlterationEffects.MULTI_JUMP, MultiJumpComponent.class, new MultiJumpAlterationEffect(context));
        registerEffect(AlterationEffects.MAX_HEALTH_BOOST, HealthBoostComponent.class,
                new HealthBoostAlterationEffect(context));
        registerEffect(AlterationEffects.ITEM_USE_SPEED, ItemUseSpeedComponent.class,
                new ItemUseSpeedAlterationEffect(context));
        registerEffect(AlterationEffects.STUN, StunComponent.class, new StunAlterationEffect(context));
        registerEffect(AlterationEffects.DECOVER, DecoverComponent.class, new DecoverAlterationEffect(context));
        registerEffect(AlterationEffects.GLUE, GlueComponent.class, new GlueAlterationEffect(context));
    }

    public <T extends AlterationEffect> void registerEffect(final String id,
                                                            final Class<? extends Component> componentType,
                                                            final T instance) {
        effectComponents.put(id, componentType);
        alterationEffects.put(id, instance);
    }

    public AlterationEffect getEffect(final String effectId) {
        Preconditions.checkArgument(alterationEffects.containsKey(effectId), "Unknown effect ID.");
        return alterationEffects.get(effectId);
    }

    /**
     * Once a basic effect's duration has expired, remove the effect from the entity that had it, send a removal event,
     * informing the other effect systems, and then re-apply the associated alteration effect.
     *
     * @param event Event with information of what particular effect expired.
     * @param entity The entity that had the expired effect.
     */
    @ReceiveEvent
    public void expireEffects(DelayedActionTriggeredEvent event, EntityRef entity) {
        final String actionId = event.getActionId();

        // First, make sure this expired event is actually part of the AlterationEffects module.
        if (actionId.startsWith(AlterationEffects.EXPIRE_TRIGGER_PREFIX)) {
            // Remove the expire trigger prefix
            EffectIdentifier effect =
                    EffectIdentifier.fromString(actionId.substring(AlterationEffects.EXPIRE_TRIGGER_PREFIX.length()));

            // If this DelayedActionTriggeredEvent corresponds to one of the basic alteration effects.
            final Class<? extends Component> component = effectComponents.get(effect.name);
            if (component != null) {
                // Remove the component corresponding to this particular effect.
                entity.removeComponent(component);

                // Send out an event alerting the other effect-related systems that this effect has been removed.
                //TODO: parse the individual effect id from the action id
                entity.send(new OnEffectRemoveEvent(entity, entity, alterationEffects.get(effect.name), effect.name,
                        effect.id, true));

                // Re-apply this effect so that if there are any modifiers still in effect, they'll be recalculated and
                // reapplied to the entity correctly.
                entity.send(new RecalculateEffect(effect));
            } else {
                logger.warn("Unknown alteration effect '{}' expired! Unable to remove effect.", effect);
            }
        }
    }

    @ReceiveEvent
    public void recalculateEffects(RecalculateEffect event, EntityRef entity) {
        for (EffectIdentifier effect : event.getEffects()) {
            alterationEffects.get(effect.name).applyEffect(entity, entity);
        }
    }

    @Command(value = "applyEffect", shortDescription = "apply an alteration effect to self", runOnServer = true)
    public String effectCommand(@Sender EntityRef client,
                                @CommandParam(value = "the effect to apply") String effectId,
                                @CommandParam(value = "the effect magnitude (default: 3)", required = false) Integer magnitude,
                                @CommandParam(value = "the duration in ms (default: 5000)", required = false) Long duration) {

        if (!alterationEffects.containsKey(effectId)) {
            return "ERROR: Unknown effect id!";
        }

        EntityRef player = client.getComponent(ClientComponent.class).character;
        AlterationEffect effect = alterationEffects.get(effectId);

        int m = Optional.ofNullable(magnitude).orElse(3);
        long d = Optional.ofNullable(duration).orElse(5000L);

        effect.applyEffect(player, player, m, d);
        return "Applied effect '" + effectId + "' with a duration of " + d / 1000f + " seconds";
    }
}
