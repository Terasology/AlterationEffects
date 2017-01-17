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
 * This is the component added to entities with the health boost effect.
 */
public class HealthBoostComponent implements Component {
    /** The amount the max health should be boosted by. 1 is equivalent to +1% max health on the applied entity. */
    public int boostAmount;

    /**
     * The last time this health boost was applied on the entity. This is also modified when the effect itself is
     * modified.
     */
    public long lastUseTime;
}
