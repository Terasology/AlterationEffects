// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.alterationEffects;

import java.util.regex.Pattern;

public class EffectIdentifier {
    final String name;
    final String id;


    public EffectIdentifier(String name, String id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name + "|" + id;
    }

    public static EffectIdentifier fromString(String effect) {
        // Split the 'effect' string into two parts. The first part will contain the AlterationEffect's name, and
        // the second part will contain the effectID.
        String[] parts = effect.split(Pattern.quote("|"), 2);

        String effectName = parts[0];
        // If there are two items in the parts, set the effectID accordingly.
        String effectID = parts.length == 2 ? parts[1] : "";

        return new EffectIdentifier(effectName, effectID);
    }
}
