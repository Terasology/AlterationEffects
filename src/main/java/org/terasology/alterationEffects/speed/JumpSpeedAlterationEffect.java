// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.speed;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
import org.terasology.alterationEffects.EffectContext;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.engine.context.Context;

import java.util.Optional;

/**
 * This handles the application of the jump speed effect, which increases an entity's jump speed and height (based on
 * the magnitude) for a specified duration.
 */
public class JumpSpeedAlterationEffect extends ComponentBasedAlterationEffect<JumpSpeedComponent> {

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public JumpSpeedAlterationEffect(Context context) {
        super(context, JumpSpeedComponent.class, AlterationEffects.JUMP_SPEED);
    }

    @Override
    protected JumpSpeedComponent upsertComponent(Optional<JumpSpeedComponent> maybeComponent,
                                                 final EffectContext context) {
        JumpSpeedComponent jumpSpeed = maybeComponent.orElse(new JumpSpeedComponent());
        jumpSpeed.multiplier = context.magnitude;
        return jumpSpeed;
    }

    @Override
    protected JumpSpeedComponent updateComponent(OnEffectModifyEvent event, JumpSpeedComponent component,
                                                 final EffectContext context) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
