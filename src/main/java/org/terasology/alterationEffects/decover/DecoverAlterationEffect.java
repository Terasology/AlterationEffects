// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.decover;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
import org.terasology.alterationEffects.EffectContext;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;

import java.util.Optional;

/**
 * This handles the application of the decover effect, which prevents an entity from healing for a specified duration.
 */
public class DecoverAlterationEffect extends ComponentBasedAlterationEffect<DecoverComponent> {

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public DecoverAlterationEffect(Context context) {
        super(context, DecoverComponent.class, AlterationEffects.DECOVER);
    }

    @Override
    protected DecoverComponent upsertComponent(Optional<DecoverComponent> maybeComponent, final EffectContext context) {
        return maybeComponent.orElse(new DecoverComponent());
    }

    @Override
    protected DecoverComponent updateComponent(OnEffectModifyEvent event, DecoverComponent component,
                                               final EffectContext context) {
        return component;
    }
}
