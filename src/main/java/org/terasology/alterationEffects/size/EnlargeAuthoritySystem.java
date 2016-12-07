package org.terasology.alterationEffects.size;

/**
 * Created by Jing Quan on 6/12/2016.
 */

import org.terasology.alterationEffects.AlterationEffects;
import org.terasology.alterationEffects.resist.ResistDamageComponent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.debug.MovementDebugCommands;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.network.ClientComponent;
import org.terasology.registry.In;

@RegisterSystem(value = RegisterMode.AUTHORITY)
public class EnlargeAuthoritySystem extends BaseComponentSystem {
    private static final int CHECK_INTERVAL = 100;
    private static final int DAMAGE_TICK = 1000;

    private long lastUpdated;

    @In
    private Time time;
    @In
    private EntityManager entityManager;

    private MovementDebugCommands movementDebugCommands;

    @ReceiveEvent
    public void expireEnlargeEffect(DelayedActionTriggeredEvent event, EntityRef entity, EnlargeComponent component) {
        final String actionId = event.getActionId();
        movementDebugCommands = new MovementDebugCommands();
        ClientComponent cc = entity.getComponent(ClientComponent.class);
        CharacterMovementComponent cmc = cc.character.getComponent(CharacterMovementComponent.class);
        if (actionId.startsWith(AlterationEffects.EXPIRE_TRIGGER_PREFIX)) {
            String effectName = actionId.substring(AlterationEffects.EXPIRE_TRIGGER_PREFIX.length());
            String[] split = actionId.split(":");

            if (split.length != 4) {
                return;
            }

            if (split[2].equalsIgnoreCase(AlterationEffects.ENLARGE)) {
                component.rdes.remove(split[3]);

                if (component.rdes.size() == 0 && component != null) {
                    entity.removeComponent(EnlargeComponent.class);
                    movementDebugCommands.playerHeight(entity, cmc.height * (float) 1.0 / (EnlargeComponent.sizeMultiplier));
                }
            }
        }
    }
}