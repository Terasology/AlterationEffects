// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.alterationEffects.speed;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.EffectsAuthoritySystem;
import org.terasology.context.Context;
import org.terasology.engine.modes.StateIngame;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.logic.delay.DelayedActionComponent;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.math.geom.Vector3i;
import org.terasology.moduletestingenvironment.MTEExtension;
import org.terasology.moduletestingenvironment.ModuleTestingHelper;
import org.terasology.moduletestingenvironment.TestEventReceiver;
import org.terasology.moduletestingenvironment.extension.Dependencies;
import org.terasology.registry.In;

@ExtendWith(MTEExtension.class)
@Dependencies({"engine", "AlterationEffects"})
class StunAlterationEffectTest {

    @In
    private ModuleTestingHelper helper;

    @In
    private EffectsAuthoritySystem effectsAuthoritySystem;

    @Test
    void applyEffect() {
        helper.forceAndWaitForGeneration(new Vector3i());
        Context context = helper.createClient();
        TestEventReceiver<OnActivatedComponent> receiveOnActivatedComponent = new TestEventReceiver<>(context,
                OnActivatedComponent.class, (event, entity) -> {
        }, DelayedActionComponent.class);
        TestEventReceiver<DelayedActionTriggeredEvent> receiveDelayedActionTriggered =
                new TestEventReceiver<>(context, DelayedActionTriggeredEvent.class, (event, entity) -> {
                    String actionId = event.getActionId();
                    Assertions.assertEquals("AlterationEffects:Expire:STUN", actionId);
                });

        Assertions.assertFalse(
                helper.runUntil(1000,
                        () -> helper.getEngines().stream().allMatch(engine -> engine.getState().getClass().equals(StateIngame.class))),
                "Engine(s) not in state 'Ingame'!");

        EntityRef player = context.get(LocalPlayer.class).getCharacterEntity();
        AlterationEffect stunEffect = effectsAuthoritySystem.getEffect(AlterationEffects.STUN);
        stunEffect.applyEffect(player, player, 5, 1000);

        Assertions.assertTrue(player.hasComponent(StunComponent.class), "Stun component not found");
        Assertions.assertTrue(player.hasComponent(DelayedActionComponent.class), "Delayed action component not found");
        Assertions.assertFalse(receiveOnActivatedComponent.getEvents().isEmpty(), "Delayed action component not " +
                "activated");
        Assertions.assertFalse(receiveDelayedActionTriggered.getEvents().isEmpty(), "No events received");

        boolean timedOut = helper.runWhile(5000, () -> true);
        Assertions.assertTrue(timedOut, "Didn't time out");
        Assertions.assertFalse(player.hasComponent(StunComponent.class), "Component not removed");
    }
}