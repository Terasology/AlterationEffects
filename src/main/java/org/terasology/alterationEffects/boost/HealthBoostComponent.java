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

public class HealthBoostComponent implements Component {
    /**
     * the percentage increase in health due to the Health Boost effect
     */
    public int boostAmount;
    /**
     * the time the Health Boost effect was last used on the entity
     */
    public long lastUseTime;
    public int oldBaseMax;
    public int newBaseMax;
    public String source;
}
