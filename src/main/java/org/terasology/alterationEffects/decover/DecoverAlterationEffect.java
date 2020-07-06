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
package org.terasology.alterationEffects.decover;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
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
    protected DecoverComponent upsertComponent(Optional<DecoverComponent> maybeComponent, float magnitude,
                                               long duration) {
        return maybeComponent.orElse(new DecoverComponent());
    }

    @Override
    protected DecoverComponent updateComponent(OnEffectModifyEvent event, DecoverComponent component) {
        return component;
    }
}
