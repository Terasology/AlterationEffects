/*
 * Copyright 2014 MovingBlocks
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
 * This handles the application of the walk speed effect, which increases an entity's walk speed (based on the
 * magnitude) for a specified duration.
 */
public class WalkSpeedAlterationEffect extends ComponentBasedAlterationEffect<WalkSpeedComponent> {

    public WalkSpeedAlterationEffect(Context context) {
        super(context, WalkSpeedComponent.class, AlterationEffects.WALK_SPEED);
    }

    protected WalkSpeedComponent upsertComponent(Optional<WalkSpeedComponent> maybeComponent, float magnitude,
                                                 long duration) {
        WalkSpeedComponent walkSpeed = maybeComponent.orElse(new WalkSpeedComponent());
        walkSpeed.multiplier = magnitude;
        return walkSpeed;
    }

    protected WalkSpeedComponent updateComponent(OnEffectModifyEvent event, WalkSpeedComponent component) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
