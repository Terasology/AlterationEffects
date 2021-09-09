// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects;

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
import org.terasology.engine.context.Context;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.console.commandSystem.annotations.Command;
import org.terasology.engine.logic.console.commandSystem.annotations.CommandParam;
import org.terasology.engine.logic.console.commandSystem.annotations.Sender;
import org.terasology.engine.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.engine.logic.permission.PermissionManager;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * This authority system manages the expiration of the basic alteration effects. Other authority systems will handle the
 * more complex effects that require more than just simply removing the effect component from the entity and informing
 * other systems.
 */
@RegisterSystem
public class EffectsAuthoritySystem extends BaseComponentSystem {
    /**
     * This will store the mapping of the effect constants to the effect components.
     */
    private Map<String, Class<? extends Component>> effectComponents = new HashMap<>();

    /**
     * This will store the mapping of the effect constants to the alteration effects.
     */
    private Map<String, AlterationEffect> alterationEffects = new HashMap<>();

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
        effectComponents.put(AlterationEffects.WALK_SPEED, WalkSpeedComponent.class);
        effectComponents.put(AlterationEffects.SWIM_SPEED, SwimSpeedComponent.class);
        effectComponents.put(AlterationEffects.JUMP_SPEED, JumpSpeedComponent.class);
        effectComponents.put(AlterationEffects.WATER_BREATHING, WaterBreathingComponent.class);
        effectComponents.put(AlterationEffects.REGENERATION, RegenerationComponent.class);
        effectComponents.put(AlterationEffects.MULTI_JUMP, MultiJumpComponent.class);
        effectComponents.put(AlterationEffects.MAX_HEALTH_BOOST, HealthBoostComponent.class);
        effectComponents.put(AlterationEffects.ITEM_USE_SPEED, ItemUseSpeedComponent.class);
        effectComponents.put(AlterationEffects.STUN, StunComponent.class);
        effectComponents.put(AlterationEffects.DECOVER, DecoverComponent.class);
        effectComponents.put(AlterationEffects.GLUE, GlueComponent.class);

        alterationEffects.put(AlterationEffects.WALK_SPEED, new WalkSpeedAlterationEffect(context));
        alterationEffects.put(AlterationEffects.SWIM_SPEED, new SwimSpeedAlterationEffect(context));
        alterationEffects.put(AlterationEffects.JUMP_SPEED, new JumpSpeedAlterationEffect(context));
        alterationEffects.put(AlterationEffects.WATER_BREATHING, new WaterBreathingAlterationEffect(context));
        alterationEffects.put(AlterationEffects.REGENERATION, new RegenerationAlterationEffect(context));
        alterationEffects.put(AlterationEffects.MULTI_JUMP, new MultiJumpAlterationEffect(context));
        alterationEffects.put(AlterationEffects.MAX_HEALTH_BOOST, new HealthBoostAlterationEffect(context));
        alterationEffects.put(AlterationEffects.ITEM_USE_SPEED, new ItemUseSpeedAlterationEffect(context));
        alterationEffects.put(AlterationEffects.STUN, new StunAlterationEffect(context));
        alterationEffects.put(AlterationEffects.DECOVER, new DecoverAlterationEffect(context));
        alterationEffects.put(AlterationEffects.GLUE, new GlueAlterationEffect(context));
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
            // Remove the expire trigger prefix and store the resultant String into effectNamePlusID.
            String effectNamePlusID = actionId.substring(AlterationEffects.EXPIRE_TRIGGER_PREFIX.length());

            // Split the effectNamePlusID into two parts. The first part will contain the AlterationEffect's name, and
            // the second part will contain the effectID.
            String[] parts = effectNamePlusID.split(Pattern.quote("|"), 2);

            // If there are two items in the parts, set the effectID accordingly.
            String effectID = "";
            if (parts.length == 2) {
                effectID = parts[1];
            }

            // Set the effectName using the first String in the parts array.
            String effectName = parts[0];

            // If this DelayedActionTriggeredEvent corresponds to one of the basic alteration effects.
            final Class<? extends Component> component = effectComponents.get(effectName);
            if (component != null) {
                // Remove the component corresponding to this particular effect.
                entity.removeComponent(component);

                // Send out an event alerting the other effect-related systems that this effect has been removed.
                entity.send(new OnEffectRemoveEvent(entity, entity, alterationEffects.get(effectName), effectID, "",
                        true));

                // Re-apply this effect so that if there are any modifiers still in effect, they'll be recalculated and
                // reapplied to the entity correctly.
                alterationEffects.get(effectName).applyEffect(entity, entity, 0, 0);
            }
        }
    }

    @Command(value = "applyEffect", shortDescription = "apply an alteration effect to self", runOnServer = true,
            requiredPermission = PermissionManager.CHEAT_PERMISSION)
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
