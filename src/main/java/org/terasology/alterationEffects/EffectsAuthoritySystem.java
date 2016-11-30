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

import org.terasology.alterationEffects.boost.HealthBoostComponent;
import org.terasology.alterationEffects.breath.WaterBreathingComponent;
import org.terasology.alterationEffects.damageOverTime.DamageOverTimeComponent;
import org.terasology.alterationEffects.regenerate.RegenerationComponent;
import org.terasology.alterationEffects.speed.ItemUseSpeedComponent;
import org.terasology.alterationEffects.speed.JumpSpeedComponent;
import org.terasology.alterationEffects.speed.MultiJumpComponent;
import org.terasology.alterationEffects.speed.SwimSpeedComponent;
import org.terasology.alterationEffects.speed.WalkSpeedComponent;
import org.terasology.alterationEffects.speed.StunComponent;
import org.terasology.alterationEffects.decover.DecoverComponent;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem
public class EffectsAuthoritySystem extends BaseComponentSystem {
    private Map<String, Class<? extends Component>> effectComponents = new HashMap<>();

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
    }

    @ReceiveEvent
    public void expireEffects(DelayedActionTriggeredEvent event, EntityRef entity) {
        final String actionId = event.getActionId();
        if (actionId.startsWith(AlterationEffects.EXPIRE_TRIGGER_PREFIX)) {
            String effectName = actionId.substring(AlterationEffects.EXPIRE_TRIGGER_PREFIX.length());
            final Class<? extends Component> component = effectComponents.get(effectName);
            if (component != null) {
                entity.removeComponent(component);
            }
        }
    }

}
