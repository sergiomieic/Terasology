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
import com.google.common.primitives.Doubles;
import org.terasology.persistence.typeHandling.DeserializationContext;
import org.terasology.persistence.typeHandling.PersistedData;
import org.terasology.persistence.typeHandling.PersistedDataArray;
import org.terasology.persistence.typeHandling.SerializationContext;
import org.terasology.persistence.typeHandling.TypeHandler;

import java.util.Collection;
import java.util.List;

/**
 */
public class DoubleTypeHandler implements TypeHandler<Double> {

    @Override
    public PersistedData serialize(Double value, SerializationContext context) {
        if (value != null) {
            return context.create(value);
        }
        return context.createNull();
    }

    @Override
    public Double deserialize(PersistedData data, DeserializationContext context) {
        if (data.isNumber()) {
            return data.getAsDouble();
        }
        return null;
    }

    @Override
    public PersistedData serializeCollection(Collection<Double> value, SerializationContext context) {
        return context.create(Doubles.toArray(value));

    }

    @Override
    public List<Double> deserializeCollection(PersistedData data, DeserializationContext context) {
        if (data.isArray()) {
            PersistedDataArray array = data.getAsArray();
            List<Double> result = Lists.newArrayListWithCapacity(array.size());
            for (PersistedData item : array) {
                if (item.isNumber()) {
                    result.add(item.getAsDouble());
                } else {
                    result.add(null);
                }
            }
            return result;
        }
        return Lists.newArrayList();
    }
}
