/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.alterationEffects.speed;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;

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
    protected GlueComponent upsertComponent(Optional<GlueComponent> maybeComponent, float magnitude, long duration) {
        GlueComponent glueComponent = maybeComponent.orElse(new GlueComponent());
        glueComponent.multiplier = magnitude;
        return glueComponent;
    }

    @Override
    protected GlueComponent updateComponent(OnEffectModifyEvent event, GlueComponent component) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
