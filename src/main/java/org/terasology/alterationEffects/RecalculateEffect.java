// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.alterationEffects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.terasology.entitySystem.event.Event;

import java.util.List;

public class RecalculateEffect implements Event {
    private final ImmutableList<EffectIdentifier> effects;

    public RecalculateEffect(List<EffectIdentifier> effects) {
        this.effects = ImmutableList.copyOf(effects);
    }

    public RecalculateEffect(EffectIdentifier... effects) {
        this(Lists.newArrayList(effects));
    }


    public List<EffectIdentifier> getEffects() {
        return effects;
    }
}
