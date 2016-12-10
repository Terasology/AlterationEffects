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
package org.terasology.alterationEffects.damageOverTime;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;

import java.util.HashMap;
import java.util.Map;

public class DamageOverTimeComponent implements Component {
    /**
     * the amount of damage dealt each tick
     */
    public int damageAmount;
    /**
     * the time the entity was last damaged by the Damage Over Time effect
     */
    public long lastDamageTime;
    public String source;
    public Map<String, DamageOverTimeEffect> dots = new HashMap<String, DamageOverTimeEffect>();
}
