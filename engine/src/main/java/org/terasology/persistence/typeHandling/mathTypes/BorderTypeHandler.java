/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.persistence.typeHandling.mathTypes;

import com.google.common.collect.Maps;
import org.terasology.math.Border;
import org.terasology.persistence.typeHandling.DeserializationContext;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.PersistedDataMap;
import org.terasology.persistence.typeHandling.SerializationContext;
import org.terasology.persistence.typeHandling.SimpleTypeHandler;

import java.util.Map;

/**
 */
public class BorderTypeHandler extends SimpleTypeHandler<Border> {
    private static final String LEFT_FIELD = "left";
    private static final String RIGHT_FIELD = "right";
    private static final String TOP_FIELD = "top";
    private static final String BOTTOM_FIELD = "bottom";

    @Override
    public PersistedData serialize(Border value, SerializationContext context) {
        if (value != null) {
            Map<String, PersistedData> map = Maps.newLinkedHashMap();
            map.put(LEFT_FIELD, context.create(value.getLeft()));
            map.put(RIGHT_FIELD, context.create(value.getRight()));
            map.put(TOP_FIELD, context.create(value.getTop()));
            map.put(BOTTOM_FIELD, context.create(value.getBottom()));
            return context.create(map);
        }

        return context.createNull();
    }

    @Override
    public Border deserialize(PersistedData data, DeserializationContext context) {
        if (data.isValueMap()) {
            PersistedDataMap map = data.getAsValueMap();
            return new Border(map.getAsInteger(LEFT_FIELD), map.getAsInteger(RIGHT_FIELD), map.getAsInteger(TOP_FIELD), map.getAsInteger(BOTTOM_FIELD));
        }
        return null;
    }
}
