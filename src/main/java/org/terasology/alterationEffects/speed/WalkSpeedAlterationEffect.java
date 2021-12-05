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
 * This handles the application of the walk speed effect, which increases an entity's walk speed (based on the
 * magnitude) for a specified duration.
 */
public class WalkSpeedAlterationEffect extends ComponentBasedAlterationEffect<WalkSpeedComponent> {

    public WalkSpeedAlterationEffect(Context context) {
        super(context, WalkSpeedComponent.class, AlterationEffects.WALK_SPEED);
    }

    protected WalkSpeedComponent upsertComponent(Optional<WalkSpeedComponent> maybeComponent,
                                                 final EffectContext context) {
        WalkSpeedComponent walkSpeed = maybeComponent.orElse(new WalkSpeedComponent());
        walkSpeed.multiplier = context.magnitude;
        return walkSpeed;
    }

    protected WalkSpeedComponent updateComponent(OnEffectModifyEvent event, WalkSpeedComponent component,
                                                 final EffectContext context) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
