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
 * This handles the application of the stun effect, which prevents an entity from walking or jumping for a specified
 * duration.
 */
public class StunAlterationEffect extends ComponentBasedAlterationEffect<StunComponent> {
    public StunAlterationEffect(Context context) {
        super(context, StunComponent.class, AlterationEffects.STUN);
    }

    @Override
    protected StunComponent upsertComponent(Optional<StunComponent> maybeComponent, final EffectContext context) {
        return maybeComponent.orElse(new StunComponent());
    }

    @Override
    protected StunComponent updateComponent(OnEffectModifyEvent event, StunComponent component,
                                            final EffectContext context) {
        return component;
    }
}
