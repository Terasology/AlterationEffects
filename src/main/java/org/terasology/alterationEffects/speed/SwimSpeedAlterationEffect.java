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
 * This handles the application of the jump speed effect, which increases an entity's swim speed (based on the
 * magnitude) for a specified duration.
 */
public class SwimSpeedAlterationEffect extends ComponentBasedAlterationEffect<SwimSpeedComponent> {

    public SwimSpeedAlterationEffect(Context context) {
        super(context, SwimSpeedComponent.class, AlterationEffects.SWIM_SPEED);
    }

    protected SwimSpeedComponent upsertComponent(Optional<SwimSpeedComponent> maybeComponent,
                                                 final EffectContext context) {
        SwimSpeedComponent swimSpeed = maybeComponent.orElse(new SwimSpeedComponent());
        swimSpeed.multiplier = context.magnitude;
        return swimSpeed;
    }

    protected SwimSpeedComponent updateComponent(OnEffectModifyEvent event, SwimSpeedComponent component,
                                                 final EffectContext context) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
