/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.alterationEffects;

/**
 * A class containing the IDs for various alteration effects.
 */
public final class AlterationEffects {
    /** The prefix added to the event ID when the expiry of an effect is triggered. */
    public static final String EXPIRE_TRIGGER_PREFIX = "AlterationEffects:Expire:";

    /** The ID of the walk speed buff effect. */
    public static final String WALK_SPEED = "WalkSpeed";

    /** The ID of the swim speed buff effect. */
    public static final String SWIM_SPEED = "SwimSpeed";

    /** The ID of the jump speed buff effect. */
    public static final String JUMP_SPEED = "JumpSpeed";

    /** The ID of the item usage speed buff effect. */
    public static final String ITEM_USE_SPEED = "ItemUseSpeed";

    /** The ID of the water breathing effect. */
    public static final String WATER_BREATHING = "WaterBreathing";

    /** The ID of the health regeneration effect. */
    public static final String REGENERATION = "Regeneration";

    /** The ID of the multi-jump effect. */
    public static final String MULTI_JUMP = "MultiJump";

    /** The ID of the health decay effect. */
    public static final String DAMAGE_OVER_TIME = "DamageOverTime";

    /** The ID of the resist damage effect. */
    public static final String RESIST_DAMAGE = "ResistDamage";

    /** The ID of the stun effect. */
    public static final String STUN = "Stun";

    /** The ID of the decover effect. */
    public static final String DECOVER = "Decover";

    /** The ID of the glue effect. */
    public static final String GLUE = "Glue";

    /** The ID of the max health boost effect. */
    public static final String MAX_HEALTH_BOOST = "MaxHealthBoost";

    private AlterationEffects() {
    }
}
