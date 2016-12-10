/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.alterationEffects.boost;

import org.terasology.entitySystem.Component;

/**
 * A component class containing information about a health boost effect.
 */
public class HealthBoostComponent implements Component {
    /** The amount by which the maximum health is boosted. */
    public int boostAmount;

    /** The time when the effect was last used. */
    public long lastUseTime;

    /** The old value of the maximum health of the entity to which the effect is applied. */
    public int oldBaseMax;

    /** The new value of the maximum health of the entity to which the effect is applied. */
    public int newBaseMax;

    /** The source of the effect. */
    public String source;
}
