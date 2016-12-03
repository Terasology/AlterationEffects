package org.terasology.alterationEffects.speed;

import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.delay.DelayManager;

public class WalkSpeedAlterationEffect implements AlterationEffect {

    private DelayManager delayManager;

    public WalkSpeedAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {
        boolean add = false;
        GlueComponent glue = entity.getComponent(GlueComponent.class);
        if (glue == null) {
            add = true;
            glue = new WalkSpeedComponent();
        }
        glue.multiplier = magnitude;

        if (add) {
            entity.addComponent(glue);
        } else {
            entity.saveComponent(glue);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.GLUE, duration);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}
