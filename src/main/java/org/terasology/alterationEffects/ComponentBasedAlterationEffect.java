// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.alterationEffects;

import org.terasology.context.Context;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

import java.util.Optional;

public abstract class ComponentBasedAlterationEffect<C extends Component> implements AlterationEffect {

    private final DelayManager delayManager;
    protected final Class<C> componentClass;
    private final String effectId;

    public ComponentBasedAlterationEffect(Context context, Class<C> componentClass, String effectIdentifier) {
        this.delayManager = context.get(DelayManager.class);
        this.componentClass = componentClass;
        this.effectId = effectIdentifier;
    }

    protected abstract C upsertComponent(Optional<C> maybeComponent, final EffectContext context);

    protected abstract C updateComponent(OnEffectModifyEvent event, C component, final EffectContext context);

    protected void removeComponent(final EffectContext context) {
        context.entity.removeComponent(componentClass);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        applyEffect(instigator, entity, "", magnitude, duration);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        final EffectContext context = new EffectContext(instigator, entity, id, magnitude, duration);
        //TODO: who should be responsible for setting base magnitude and duration?
        //      can we just pass it with OnEffectModifyEvent?
        entity.upsertComponent(componentClass, maybeComponent -> upsertComponent(maybeComponent, context));

        // 2. send OnEffectModifyEvent
        OnEffectModifyEvent effectModifyEvent = entity.send(
                new OnEffectModifyEvent(instigator, entity, 0, 0, this, id)
        );

        long modifiedDuration = 0;
        boolean modifiersFound = false;

        // If the effect modify event is consumed, don't apply this walk speed effect.meute
        if (!effectModifyEvent.isConsumed()) {
            modifiedDuration = effectModifyEvent.getShortestDuration();

            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                entity.updateComponent(componentClass, c -> updateComponent(effectModifyEvent, c, context));
                modifiersFound = true;
            }
        }

        // 3. update component from OnEffectModifyEvent
        //      (OnEffectModifyEvent, C) -> C
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {

            String effectIDWithShortestDuration = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity,
                    AlterationEffects.EXPIRE_TRIGGER_PREFIX + effectId + "|" + effectIDWithShortestDuration,
                    modifiedDuration);
        } else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            // Otherwise, if the duration is greater than 0, there are no modifiers found, and the effect modify
            // event was not consumed,
            // add a delayed action to the DelayManager using the old system.
            delayManager.addDelayedAction(entity,
                    AlterationEffects.EXPIRE_TRIGGER_PREFIX + effectId, duration);
        } else if (!modifiersFound || !effectModifyEvent.getHasInfDuration()) {
            // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event
            // have infinite
            // duration, remove the component associated with this walk speed effect.
            removeComponent(context);
        }
        // If this point is reached and none of the above if-clauses were met, that means there was at least one
        // modifier
        // collected in the event which has infinite duration.
    }
}
