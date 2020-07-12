// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.boost;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
import org.terasology.alterationEffects.EffectContext;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.context.Context;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.health.HealthComponent;
import org.terasology.math.TeraMath;

import java.util.Optional;

/**
 * This handles the application of the health boost effect, which boosts an entity's maximum health (based on the
 * magnitude) for a specified duration.
 */
public class HealthBoostAlterationEffect extends ComponentBasedAlterationEffect<HealthBoostComponent> implements AlterationEffect {

    private final Time time;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the time and DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public HealthBoostAlterationEffect(Context context) {
        super(context, HealthBoostComponent.class, AlterationEffects.MAX_HEALTH_BOOST);
        this.time = context.get(Time.class);
    }

    /**
     * Removes the effect of the given health boost from the entity.
     *
     * @param entity The entity which has the health boost effect.
     * @param boost The health boost component.
     */
    private void removeBoost(EntityRef entity, HealthBoostComponent boost) {
        entity.updateComponent(HealthComponent.class, health -> {
            // Reverse the max health boosting effect by dividing the old boost amount.
            health.maxHealth = Math.round(health.maxHealth / (1f + 0.01f * boost.boostAmount));
            if (health.currentHealth > health.maxHealth) {
                health.currentHealth = health.maxHealth;
            }
            return health;
        });
    }

    @Override
    protected HealthBoostComponent upsertComponent(Optional<HealthBoostComponent> maybeComponent,
                                                   EffectContext context) {
        maybeComponent.ifPresent(c -> removeBoost(context.entity, c));
        HealthBoostComponent component = maybeComponent.orElse(new HealthBoostComponent());
        component.boostAmount = TeraMath.floorToInt(context.magnitude);
        return component;
    }

    @Override
    protected HealthBoostComponent updateComponent(OnEffectModifyEvent event, HealthBoostComponent component,
                                                   EffectContext context) {
        component.boostAmount = TeraMath.floorToInt(event.getMagnitudeResultValue());
        component.lastUseTime = time.getGameTimeInMs();

        // Get the health component of this entity, and increase its max health using the health boost multiplier.
        context.entity.updateComponent(HealthComponent.class, h -> {
            h.maxHealth = Math.round(h.maxHealth * (1 + 0.01f * component.boostAmount));
            return h;
        });

        return component;
    }
}
