// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.alterationEffects.boost;

import org.terasology.engine.entitySystem.Component;

/**
 * This is the component added to entities with the health boost effect.
 */
public class HealthBoostComponent implements Component {
    /**
     * The amount the max health should be boosted by. 1 is equivalent to +1% max health on the applied entity.
     */
    public int boostAmount;

    /**
     * The last time this health boost was applied on the entity. This is also modified when the effect itself is
     * modified.
     */
    public long lastUseTime;
}
