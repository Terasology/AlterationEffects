// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.alterationEffects.speed;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.EffectsAuthoritySystem;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;
import org.terasology.logic.delay.DelayedActionComponent;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.moduletestingenvironment.MTEExtension;
import org.terasology.moduletestingenvironment.ModuleTestingHelper;
import org.terasology.moduletestingenvironment.TestEventReceiver;
import org.terasology.moduletestingenvironment.extension.Dependencies;
import org.terasology.registry.In;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MTEExtension.class)
@Dependencies({"engine", "AlterationEffects"})
class StunAlterationEffectTest {

    @In
    private ModuleTestingHelper helper;

    @In
    private DelayManager delayManager;

    @In
    private EffectsAuthoritySystem effectsAuthoritySystem;

    @Test
    void applyEffect() {
        Context context = helper.createClient();
        context.put(DelayManager.class, delayManager);
        EntityRef player = context.get(LocalPlayer.class).getCharacterEntity();
        StunAlterationEffect stunEffect = new StunAlterationEffect(context);
        stunEffect.applyEffect(player, player, 5, 1000);

        Assertions.assertTrue(player.hasComponent(StunComponent.class), "Stun component not found");
        Assertions.assertTrue(player.hasComponent(DelayedActionComponent.class), "Delayed action component not found");
        TestEventReceiver receiver = new TestEventReceiver<>(context, DelayedActionTriggeredEvent.class, (event, entity) -> {
           String actionId = event.getActionId();
           Assertions.assertEquals("AlterationEffects:Expire:STUN", actionId);
        });

        Assertions.assertFalse(receiver.getEvents().isEmpty(), "No events received");

        boolean timedOut = helper.runWhile(5000, ()-> true);
        Assertions.assertTrue(timedOut, "Didn't time out");
        Assertions.assertFalse(player.hasComponent(StunComponent.class), "Component not removed");
    }
}