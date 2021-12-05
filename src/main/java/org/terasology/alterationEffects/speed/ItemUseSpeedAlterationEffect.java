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
 * This handles the application of the item use speed effect, which speeds up an entity's item use rate (based on the
 * magnitude) for a specified duration.
 */
public class ItemUseSpeedAlterationEffect extends ComponentBasedAlterationEffect<ItemUseSpeedComponent> {

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be used to get the
     * DelayManager.
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
