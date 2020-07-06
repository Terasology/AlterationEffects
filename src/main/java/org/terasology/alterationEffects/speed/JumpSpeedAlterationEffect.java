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
    protected JumpSpeedComponent upsertComponent(Optional<JumpSpeedComponent> maybeComponent, float magnitude,
                                                 long duration) {
        JumpSpeedComponent jumpSpeed = maybeComponent.orElse(new JumpSpeedComponent());
        jumpSpeed.multiplier = magnitude;
        return jumpSpeed;
    }

    @Override
    protected JumpSpeedComponent updateComponent(OnEffectModifyEvent event, JumpSpeedComponent component) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
