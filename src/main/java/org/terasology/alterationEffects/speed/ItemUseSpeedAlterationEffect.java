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
import org.terasology.alterationEffects.EffectContext;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;
import org.terasology.logic.delay.DelayManager;

import java.util.Optional;

/**
 * This handles the application of the item use speed effect, which speeds up an entity's item use rate (based on the
 * magnitude) for a specified duration.
 */
public class ItemUseSpeedAlterationEffect extends ComponentBasedAlterationEffect<ItemUseSpeedComponent> {

    private DelayManager delayManager;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public ItemUseSpeedAlterationEffect(Context context) {
        super(context, ItemUseSpeedComponent.class, AlterationEffects.ITEM_USE_SPEED);
    }

    @Override
    protected ItemUseSpeedComponent upsertComponent(Optional<ItemUseSpeedComponent> maybeComponent,
                                                    final EffectContext context) {
        ItemUseSpeedComponent itemUseSpeed = maybeComponent.orElse(new ItemUseSpeedComponent());
        itemUseSpeed.multiplier = context.magnitude;
        return itemUseSpeed;
    }

    @Override
    protected ItemUseSpeedComponent updateComponent(OnEffectModifyEvent event, ItemUseSpeedComponent component,
                                                    final EffectContext context) {
        component.multiplier = event.getMagnitudeResultValue();
        return component;
    }
}
