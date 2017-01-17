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
package org.terasology.alterationEffects.speed;
import org.terasology.entitySystem.Component;

/**
 * This is the component added to entities with the multi jump effect.
 */
public class MultiJumpComponent implements Component {
    /** This will affect how much the base number of multi jumps is added by. */
    public float modifier; // TODO: Won't work until AlterationEffects has something else in addition to magnitude.

    /** This affects how much the base number of max jumps is multiplied by. */
    public float multiplier;
}

