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
 * This class contains a list of constants (effect IDs) used for alteration effect-related type checks. Most are
 * self-explanatory as to what they mean.
 */
public final class AlterationEffects {
    /** This constant is used to denote effects with infinite durations. */
    public static final int DURATION_INDEFINITE = -1;

    /** This constant is used to indicate that an effect from this module is expiring. */
    public static final String EXPIRE_TRIGGER_PREFIX = "AlterationEffects:Expire:";

    public static final String WALK_SPEED = "WalkSpeed";
    public static final String SWIM_SPEED = "SwimSpeed";
    public static final String JUMP_SPEED = "JumpSpeed";
    public static final String ITEM_USE_SPEED = "ItemUseSpeed";
    public static final String WATER_BREATHING = "WaterBreathing";
    public static final String REGENERATION = "Regeneration";
    public static final String MULTI_JUMP = "MultiJump";
    public static final String DAMAGE_OVER_TIME = "DamageOverTime";
    public static final String BUFF_DAMAGE = "BuffDamage";
    public static final String RESIST_DAMAGE = "ResistDamage";
    public static final String STUN = "Stun";
    public static final String DECOVER = "Decover";
    public static final String GLUE = "Glue";

    public static final String MAX_HEALTH_BOOST = "MaxHealthBoost";

    public static final String CONSUMABLE_ITEM = "ConsumableItem";

    private AlterationEffects() {
    }
}
