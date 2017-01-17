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

import org.terasology.reflection.MappedContainer;

/**
 * This class is used for storing a damage type resistance.
 */
@MappedContainer
public class ResistDamageEffect {
    /** The damage type that will be resisted. */
    public String resistType;

    /** The amount of damage that'll be resisted (i.e. subtracted). */
    public int resistAmount;
}
