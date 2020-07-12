// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.breath;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
import org.terasology.alterationEffects.EffectContext;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;

import java.util.Optional;

/**
 * This handles the application of the water breathing effect, which allows an entity to breathe underwater for a
 * specified duration.
 */
public class WaterBreathingAlterationEffect extends ComponentBasedAlterationEffect<WaterBreathingComponent> {

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public WaterBreathingAlterationEffect(Context context) {
        super(context, WaterBreathingComponent.class, AlterationEffects.WATER_BREATHING);
    }

    @Override
    protected WaterBreathingComponent upsertComponent(Optional<WaterBreathingComponent> maybeComponent,
                                                      final EffectContext context) {
        return maybeComponent.orElse(new WaterBreathingComponent());
    }

    @Override
    protected WaterBreathingComponent updateComponent(OnEffectModifyEvent event, WaterBreathingComponent component,
                                                      final EffectContext context) {
        return component;
    }
}
