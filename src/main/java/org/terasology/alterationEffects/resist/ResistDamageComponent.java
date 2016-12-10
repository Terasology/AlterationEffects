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

import org.terasology.entitySystem.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * A component containing information about resist damage effects on an entity.
 */
public class ResistDamageComponent implements Component {
    /** The amount of damage that can be resisted due to the effects. */
    public int resistAmount;

    /** The source of the effect. */
    public String source;

    /** A map between IDs and resist damage effects on an entity. */
    public Map<String, ResistDamageEffect> rdes = new HashMap<String, ResistDamageEffect>();
}
