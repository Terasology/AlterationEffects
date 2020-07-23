// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.alterationEffects;

import org.terasology.context.Context;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

import java.util.Optional;

/**
 * A skeleton implementation for alteration effects backed by a single component.
 * <p>
 * This base class offers hook methods for affecting the life cycle of the backing component. These life cycle hooks are
 * called from the common logic handling the initial application of an effect, sending out {@link OnEffectModifyEvent},
 * and expiring the effect after some duration.
 * <p>
 * Identifiers for common alteration effects can be found in {@link AlterationEffects}.
 * <p>
 * This skeleton implementation applies an alteration affect as follows:
 * <ol>
 *     <li><strong>upsert</strong> the backing component and send {@link OnEffectModifyEvent} to notify systems</li>
 *     <li><strong>update</strong> the backing component if the effect was modified</li>
 *     <li>schedule effect expiration or <strong>remove</strong> the backing component</li>
 * </ol>
 * <p>
 * An effect based on this class must be accompanied by the corresponding <emph>backing component class</emph>. In
 * addition, a <emph>backing system</emph> (or corresponding system) should be implemented to react on the effect
 * component and handle subsequent {@link OnEffectModifyEvent}s.
 *
 * <strong>Note:</strong> I believe this to be only a temporary solution for simplifying alteration effects.
 *
 * @param <C> the component class this effect is backed by
 */
public abstract class ComponentBasedAlterationEffect<C extends Component> implements AlterationEffect {

    /**
     * The component class of the backing component.
     */
    protected final Class<C> componentClass;

    private final DelayManager delayManager;
    private final String effectId;

    /**
     * Initialize a component-backed alteration effect for the given component class and identifier.
     *
     * @param context the game context to retrieve objects without dependency injection
     * @param componentClass the class of the backing component
     * @param effectIdentifier the effect identifier used with the delayed expiration event
     */
    public ComponentBasedAlterationEffect(Context context, Class<C> componentClass, String effectIdentifier) {
        this.delayManager = context.get(DelayManager.class);
        this.componentClass = componentClass;
        this.effectId = effectIdentifier;
    }

    /**
     * Hook method called whenever this effect is applied.
     * <p>
     * Upsert denotes the operation to either insert (i.e., create) or update the backing component. If the
     * component is already present on the entity the optional value will contain the component. Otherwise, it will be
     * empty.
     * <p>
     * The effect context holds all information passed to {@link AlterationEffect#applyEffect(EntityRef, EntityRef,
     * String, float, long)}. See {@link EffectContext} for more details.
     * <p>
     * Implementations of this method should return the initialized (or updated) component.
     * <p>
     * <strong>Note:</strong> Ideally, the corresponding system should act upon an {@link OnEffectModifyEvent} for this
     * alteration effect. If that is the case, the upsert operation only needs to handle component creation, and setting
     * the correct state is handled by {@link #updateComponent(OnEffectModifyEvent, Component, EffectContext)}.
     * <p>
     * <strong>Example: </strong> A common implementation pattern is to first retrieve the component from the optional
     * value or create a new instance, update any fields as necessary, and then return the component:
     * <pre>
     * {@code
     * MyComponent upsertComponent(Optional<MyComponent> maybeComponent, final EffectContext context) {
     *      MyComponent component = maybeComponent.orElse(new MyComponent());
     *      // set fields of 'component'  as necessary
     *      return component;
     * }
     * }
     * </pre>
     *
     * @param maybeComponent the value of the component if it is already present, or empty otherwise
     * @param context the contextual information this effect is applied in
     * @return the initialized or updated component
     */
    protected abstract C upsertComponent(Optional<C> maybeComponent, EffectContext context);

    /**
     * Hook method called if the backing component should be updated based on the initial {@link OnEffectModifyEvent}.
     * <p>
     * This hook is only called when the initial {@link OnEffectModifyEvent} is not consumed and both duration and
     * magnitude modifiers are set. The event is sent out with a base magnitude of 0 and a base duration of 0. Any
     * system can modify the event to change these values.
     * <p>
     * <strong>Note:</strong> Ideally, the corresponding system should act upon the {@link OnEffectModifyEvent} if the
     * backing component is present on an entity. This should be the main method to update the entity's state. If the
     * corresponding system does react on the event, the initial {@link #upsertComponent(Optional, EffectContext)} can
     * be limited to creating the component.
     *
     * @param event the event holding modifications to duration or magnitude by other systems
     * @param component the component to update
     * @param context the contextual information this effect is applied in
     * @return the updated component
     */
    protected abstract C updateComponent(OnEffectModifyEvent event, C component, EffectContext context);

    /**
     * Hook method to be called if the backing component should be removed without applying the effect.
     * <p>
     * Called for
     * <ul>
     *     <li>indefinite effects without modifiers</li>
     * </ul>
     * and
     * <ul>
     *     <li>definite effects with
     *         <ul>
     *             <li>invalid modifiers</li>
     *             <li>no modifiers and zero duration</li>
     *             <li>no modifiers and invalid duration</li>
     *         </ul>
     *     </li>
     * </ul>
     * <p>
     * The default implementation simply removes the backing component.
     * <pre>
     * {@code context.entity.removeComponent(componentClass);}
     * </pre>
     *
     * @param context the contextual information this effect is applied in
     */
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
        // -- 1. Upsert component and send modification event ----------------------------------------------------------
        //TODO: who should be responsible for setting base magnitude and duration?
        //      can we just pass it with OnEffectModifyEvent?
        entity.upsertComponent(componentClass, maybeComponent -> upsertComponent(maybeComponent, context));

        OnEffectModifyEvent effectModifyEvent = entity.send(
                new OnEffectModifyEvent(instigator, entity, 0, 0, this, id)
        );

        long modifiedDuration = 0;
        boolean modifiersFound = false;

        // -- 2. Update component if event was modified ----------------------------------------------------------------
        // If the effect modify event is consumed, don't apply this walk speed effect
        if (!effectModifyEvent.isConsumed()) {
            modifiedDuration = effectModifyEvent.getShortestDuration();

            if (!effectModifyEvent.getDurationModifiers().isEmpty() && !effectModifyEvent.getMagnitudeModifiers().isEmpty()) {
                entity.updateComponent(componentClass, c -> updateComponent(effectModifyEvent, c, context));
                modifiersFound = true;
            }
        }

        // -- 3. Schedule effect expiration or remove component --------------------------------------------------------
        if (modifiedDuration < Long.MAX_VALUE && modifiedDuration > 0 && duration != AlterationEffects.DURATION_INDEFINITE) {
            // valid modifiers (only checks modified duration though) and definite

            String effectIDWithShortestDuration = effectModifyEvent.getEffectIDWithShortestDuration();
            delayManager.addDelayedAction(entity,
                    AlterationEffects.EXPIRE_TRIGGER_PREFIX + effectId + "|" + effectIDWithShortestDuration,
                    modifiedDuration);
        } else if (duration > 0 && !modifiersFound && !effectModifyEvent.isConsumed()) {
            // no modifiers but positive duration and definite (and not consumed)
            // add a delayed action to the DelayManager using the old system.
            delayManager.addDelayedAction(entity,
                    AlterationEffects.EXPIRE_TRIGGER_PREFIX + effectId, duration);
        } else if (!modifiersFound || !effectModifyEvent.getHasInfDuration()) {
            // Otherwise, if there are either no modifiers found, or none of the modifiers collected in the event
            // have infinite duration, remove the component associated with this effect.
            removeComponent(context);
        } else {
            // If this point is reached and none of the above if-clauses were met, that means there was at least one
            // modifier collected in the event which has infinite duration.
        }
    }
}
