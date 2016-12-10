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
package org.terasology.alterationEffects.resist;

import org.terasology.alterationEffects.damageOverTime.DamageOverTimeEffect;
import org.terasology.entitySystem.Component;

import java.util.HashMap;
import java.util.Map;

public class ResistDamageComponent implements Component {
    /**
     * the amount that damage is reduced each time
     */
    public int resistAmount;
    public String source;
    /**
     * maps the type of damage being reduced to its ResistDamageEffect object
     */
    public Map<String, ResistDamageEffect> rdes = new HashMap<String, ResistDamageEffect>();
}
