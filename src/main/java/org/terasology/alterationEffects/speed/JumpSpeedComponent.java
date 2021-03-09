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
import org.terasology.engine.entitySystem.Component;

/**
 * This is the component added to entities with the jump speed effect.
 */
public class JumpSpeedComponent implements Component {
    /** This will affect how much the base jump speed is added by. */
    public float modifier; // TODO: Won't work until AlterationEffects has something else in addition to magnitude.

    /**
     * This affects how much the base jump speed is multiplied by. 1 is normal speed, 0 is immobility, and 2 is double
     * speed.
     */
    public float multiplier;
}

