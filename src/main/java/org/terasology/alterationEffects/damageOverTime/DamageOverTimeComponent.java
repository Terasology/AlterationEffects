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

import java.util.HashMap;
import java.util.Map;

/**
 * A component that contains information about damage over time effects on an entity.
 */
public class DamageOverTimeComponent implements Component {
    /** The damage amount. */
    public int damageAmount;

    /** The last time the damage was dealt by the effect. */
    public long lastDamageTime;

    /** The source of the effect/ */
    public String source;

    /** A map between effect IDs and damage over time effects on an entity. */
    public Map<String, DamageOverTimeEffect> dots = new HashMap<String, DamageOverTimeEffect>();
}
