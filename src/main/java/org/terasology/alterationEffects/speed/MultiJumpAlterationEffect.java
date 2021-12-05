// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
import org.terasology.alterationEffects.EffectContext;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.engine.context.Context;

import java.util.Optional;

/**
 * This handles the application of the multi jump effect, which allows an entity to jump multiple times before hitting
 * solid ground for a specified duration.
 */
public class MultiJumpAlterationEffect extends ComponentBasedAlterationEffect<MultiJumpComponent> {

    public MultiJumpAlterationEffect(Context context) {
        super(context, MultiJumpComponent.class, AlterationEffects.MULTI_JUMP);
    }

    @Override
    protected MultiJumpComponent upsertComponent(Optional<MultiJumpComponent> maybeComponent,
                                                 final EffectContext context) {
        MultiJumpComponent multiJump = maybeComponent.orElse(new MultiJumpComponent());
        multiJump.multiplier = context.magnitude;
        return multiJump;
    }

    @Override
    protected MultiJumpComponent updateComponent(OnEffectModifyEvent event, MultiJumpComponent component,
                                                 final EffectContext context) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
