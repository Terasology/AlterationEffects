package org.terasology.alterationEffects.size;

/**
 * Created by Jing Quan on 6/12/2016.
 */
import org.terasology.alterationEffects.AlterationEffect;
import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.debug.MovementDebugCommands;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.delay.DelayManager;
import org.terasology.network.ClientComponent;

public class EnlargeAlterationEffect implements AlterationEffect {


    private DelayManager delayManager;
    private MovementDebugCommands movementDebugCommands;

    public EnlargeAlterationEffect(Context context) {
        this.delayManager = context.get(DelayManager.class);
    }

    public void applyEffect(EntityRef instigator, EntityRef entity, float magnitude, long duration) {

        EnlargeComponent enlarge = entity.getComponent(EnlargeComponent.class);
        movementDebugCommands = new MovementDebugCommands();

        ClientComponent cc = entity.getComponent(ClientComponent.class);
        CharacterMovementComponent cmc = cc.character.getComponent(CharacterMovementComponent.class);

        boolean add = false;
        if (enlarge == null) {
            add = true;
            enlarge = new EnlargeComponent();
        }
        EnlargeComponent.sizeMultiplier = magnitude;
        movementDebugCommands.playerHeight(entity, (float) cmc.height*EnlargeComponent.sizeMultiplier);

        if (add) {
            entity.addComponent(enlarge);
        } else {
            entity.saveComponent(enlarge);
        }

        delayManager.addDelayedAction(entity, AlterationEffects.EXPIRE_TRIGGER_PREFIX + AlterationEffects.ENLARGE, duration);
    }

    @Override
    public void applyEffect(EntityRef instigator, EntityRef entity, String id, float magnitude, long duration) {
        applyEffect(instigator, entity, magnitude, duration);
    }
}

