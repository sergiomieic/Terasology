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
package org.terasology.persistence.typeHandling.coreTypes;

import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import org.terasology.persistence.typeHandling.DeserializationContext;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.PersistedDataArray;
import org.terasology.persistence.typeHandling.SerializationContext;
import org.terasology.persistence.typeHandling.TypeHandler;

import java.util.Collection;
import java.util.List;

/**
 */
public class BooleanTypeHandler implements TypeHandler<Boolean> {

    @Override
    public PersistedData serialize(Boolean value, SerializationContext context) {
        if (value != null) {
            return context.create(value);
        }
        return context.createNull();
    }

    @Override
    public Boolean deserialize(PersistedData data, DeserializationContext context) {
        if (data.isBoolean()) {
            return data.getAsBoolean();
        }
        return null;
    }

    @Override
    public PersistedData serializeCollection(Collection<Boolean> value, SerializationContext context) {
        return context.create(Booleans.toArray(value));
    }

    @Override
    public List<Boolean> deserializeCollection(PersistedData data, DeserializationContext context) {
        if (data.isArray()) {
            PersistedDataArray array = data.getAsArray();
            List<Boolean> result = Lists.newArrayListWithCapacity(array.size());
            for (PersistedData item : array) {
                if (item.isBoolean()) {
                    result.add(item.getAsBoolean());
                } else {
                    result.add(null);
                }
            }
            return result;
        }
        return Lists.newArrayList();
    }
}
