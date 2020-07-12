// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.buff;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
import org.terasology.alterationEffects.EffectContext;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;
import org.terasology.math.TeraMath;

import java.util.Optional;

/**
 * This handles the application of the buff damage effect, which allows an entity to buff certain damage types (based on
 * the magnitude) for a specified duration.
 */
public class BuffDamageAlterationEffect extends ComponentBasedAlterationEffect<BuffDamageComponent> implements AlterationEffect {

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public BuffDamageAlterationEffect(Context context) {
        super(context, BuffDamageComponent.class, AlterationEffects.BUFF_DAMAGE);
    }

    @Override
    protected BuffDamageComponent upsertComponent(Optional<BuffDamageComponent> maybeComponent, EffectContext context) {
        BuffDamageComponent buffDamageComponent = maybeComponent.orElse(new BuffDamageComponent());
        BuffDamageEffect buffDamageEffect = new BuffDamageEffect();
        buffDamageEffect.damageType = context.id;
        buffDamageEffect.buffAmount = TeraMath.floorToInt(context.magnitude);

        buffDamageComponent.bdes.put(context.id, buffDamageEffect);
        return buffDamageComponent;
    }

    @Override
    protected BuffDamageComponent updateComponent(OnEffectModifyEvent event, BuffDamageComponent component,
                                                  final EffectContext context) {
        BuffDamageEffect buffDamageEffect = component.bdes.get(context.id);
        buffDamageEffect.buffAmount = (int) event.getMagnitudeResultValue();
        return component;
    }

    @Override
    protected void removeComponent(EffectContext context) {
        BuffDamageComponent buffDamageComponent = context.entity.getComponent(componentClass);
        buffDamageComponent.bdes.remove(context.id);
    }
}
