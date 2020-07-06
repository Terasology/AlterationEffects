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
 * This handles the application of the multi jump effect, which allows an entity to jump multiple times before hitting
 * solid ground for a specified duration.
 */
public class MultiJumpAlterationEffect extends ComponentBasedAlterationEffect<MultiJumpComponent> {

    public MultiJumpAlterationEffect(Context context) {
        super(context, MultiJumpComponent.class, AlterationEffects.MULTI_JUMP);
    }

    @Override
    protected MultiJumpComponent upsertComponent(Optional<MultiJumpComponent> maybeComponent, float magnitude,
                                                 long duration) {
        MultiJumpComponent multiJump = maybeComponent.orElse(new MultiJumpComponent());
        multiJump.multiplier = magnitude;
        return multiJump;
    }

    @Override
    protected MultiJumpComponent updateComponent(OnEffectModifyEvent event, MultiJumpComponent component) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
