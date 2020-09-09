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
 * This handles the application of the glue effect, which slows down an entity's walk speed (based on the magnitude) and
 * prevents it from jumping for a specified duration.
 */
public class GlueAlterationEffect extends ComponentBasedAlterationEffect<GlueComponent> {

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public GlueAlterationEffect(Context context) {
        super(context, GlueComponent.class, AlterationEffects.GLUE);
    }

    @Override
    protected GlueComponent upsertComponent(Optional<GlueComponent> maybeComponent, final EffectContext context) {
        GlueComponent glueComponent = maybeComponent.orElse(new GlueComponent());
        glueComponent.multiplier = context.magnitude;
        return glueComponent;
    }

    @Override
    protected GlueComponent updateComponent(OnEffectModifyEvent event, GlueComponent component,
                                            final EffectContext context) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
