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
import org.terasology.alterationEffects.boost.HealthBoostAlterationEffect;
import org.terasology.alterationEffects.boost.HealthBoostComponent;
import org.terasology.alterationEffects.breath.WaterBreathingAlterationEffect;
import org.terasology.alterationEffects.breath.WaterBreathingComponent;
import org.terasology.alterationEffects.decover.DecoverAlterationEffect;
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
import org.terasology.alterationEffects.speed.SwimSpeedAlterationEffect;
import org.terasology.alterationEffects.speed.SwimSpeedComponent;
import org.terasology.alterationEffects.speed.WalkSpeedAlterationEffect;
import org.terasology.alterationEffects.speed.WalkSpeedComponent;
import org.terasology.alterationEffects.speed.StunComponent;
import org.terasology.alterationEffects.decover.DecoverComponent;
import org.terasology.context.Context;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.registry.In;
import org.terasology.registry.Share;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This authority system manages the expiration of the basic alteration effects. Other authority systems will handle
 * the more complex effects that require more than just simply removing the effect component from the entity and
 * informing other systems.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
@Share(value = EffectsAuthoritySystem.class)
public class EffectsAuthoritySystem extends BaseComponentSystem {
    /** This will store the mapping of the effect constants to the effect components. */
    private Map<String, Class<? extends Component>> effectComponents = new HashMap<>();

    /** This will store the mapping of the effect constants to the alteration effects. */
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
     * @param event     Event with information of what particular effect expired.
     * @param entity    The entity that had the expired effect.
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
                entity.send(new OnEffectRemoveEvent(entity, entity, alterationEffects.get(effectName), effectID, "", true));

                // Re-apply this effect so that if there are any modifiers still in effect, they'll be recalculated and
                // reapplied to the entity correctly.
                alterationEffects.get(effectName).applyEffect(entity, entity, 0, 0);
            }
        }
    }

    public AlterationEffect getEffect(final String effectId) {
        Preconditions.checkArgument(alterationEffects.containsKey(effectId), "Unknown effect ID.");
        return alterationEffects.get(effectId);
    }
}
