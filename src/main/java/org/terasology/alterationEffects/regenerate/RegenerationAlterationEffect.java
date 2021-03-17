// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.regenerate;

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.ComponentBasedAlterationEffect;
import org.terasology.alterationEffects.EffectContext;
import org.terasology.alterationEffects.OnEffectModifyEvent;
import org.terasology.engine.context.Context;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.logic.health.event.ActivateRegenEvent;
import org.terasology.math.TeraMath;

import java.util.Optional;

/**
 * This handles the application of the health regeneration effect, which heals an entity for the given magnitude per
 * second for a specified duration.
 */
public class RegenerationAlterationEffect extends ComponentBasedAlterationEffect<RegenerationComponent> {

    private final Time time;

    /**
     * Constructor. Instantiate an instance of this alteration effect using the provided context. This context will be
     * used to get the current time and DelayManager.
     *
     * @param context The context which this effect will be executed on.
     */
    public RegenerationAlterationEffect(Context context) {
        super(context, RegenerationComponent.class, AlterationEffects.REGENERATION);
        this.time = context.get(Time.class);
    }

    @Override
    protected RegenerationComponent upsertComponent(Optional<RegenerationComponent> maybeComponent,
                                                    final EffectContext context) {
        RegenerationComponent component = maybeComponent.orElse(new RegenerationComponent());
        component.regenerationAmount = TeraMath.floorToInt(context.magnitude);
        component.lastRegenerationTime = time.getGameTimeInMs();
        return component;
    }

    @Override
    protected RegenerationComponent updateComponent(OnEffectModifyEvent event, RegenerationComponent component,
                                                    final EffectContext context) {
        component.regenerationAmount = (int) event.getMagnitudeResultValue();
        return component;
    }

    /**
     * This will apply the regeneration effect on the given entity by calling the method {@link #applyEffect(EntityRef,
     * EntityRef, float, long)}.
     *
     * @param instigator The entity who applied the regen effect.
     * @param entity The entity that the regen effect is being applied on.
     * @param id Id is used for unique identification in regen scheduler.
     * @param magnitude The magnitude of the regen effect.
     * @param duration The duration of the regen effect.
     */
    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        //TODO: this is supposed to be the "new" implementation with the approach above being deprecated
        //applyEffect(instigator, entity, magnitude, duration);
        if (magnitude != 0) {
            //FIXME(skaldarnar): "duration" may be -1 to indicated DURATION_INDEFINITE - how does that translate to the
            //                   regen event we are sending here?
            //                   Actually, -1 has a special meaning for the regen effect, but it is different from the
            //                   semantic in alteration effects: heal until the entity has restored full health as
            //                   opposed to heal indefinitely
            entity.send(new ActivateRegenEvent(id, magnitude, ((float) duration) / 1000));
        }
    }
}
