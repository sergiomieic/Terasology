/*
 * Copyright 2013 Moving Blocks
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
package org.terasology.persistence.internal;

import com.google.common.collect.Maps;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.EngineEntityManager;
import org.terasology.entitySystem.EntityRef;
import org.terasology.entitySystem.OwnershipHelper;
import org.terasology.entitySystem.metadata.ClassMetadata;
import org.terasology.entitySystem.metadata.extension.EntityRefTypeHandler;
import org.terasology.persistence.serializers.EntitySerializer;
import org.terasology.persistence.serializers.FieldSerializeCheck;
import org.terasology.protobuf.EntityData;

import java.util.Map;

/**
 * @author Immortius
 */
class EntityStorer implements EntityRefTypeHandler.EntityRefInterceptor {

    private final EngineEntityManager entityManager;
    private final EntitySerializer serializer;
    private final EntityData.EntityStore.Builder entityStoreBuilder;
    private final OwnershipHelper helper;
    private TIntSet externalReferences = new TIntHashSet();
    private TIntSet storedEntityIds = new TIntHashSet();

    public EntityStorer(EngineEntityManager entityManager) {
        this.entityManager = entityManager;
        this.entityStoreBuilder = EntityData.EntityStore.newBuilder();
        this.serializer = new EntitySerializer(entityManager);
        this.helper = new OwnershipHelper(entityManager.getComponentLibrary());

        Map<Class<? extends Component>, Integer> componentIds = Maps.newHashMap();
        for (ClassMetadata<? extends Component> componentMetadata : entityManager.getComponentLibrary()) {
            entityStoreBuilder.addComponentClass(componentMetadata.getName());
            componentIds.put(componentMetadata.getType(), componentIds.size());
        }
        serializer.setComponentIdMapping(componentIds);
    }

    public void store(EntityRef entity) {
        store(entity, "");
    }

    public void store(EntityRef entity, String name) {
        if (entity.isActive()) {
            for (EntityRef ownedEntity : helper.listOwnedEntities(entity)) {
                store(ownedEntity);
            }
            EntityRefTypeHandler.setReferenceInterceptor(this);
            EntityData.Entity entityData = serializer.serialize(entity, true, FieldSerializeCheck.NullCheck.<Component>newInstance());
            EntityRefTypeHandler.setReferenceInterceptor(null);
            entityStoreBuilder.addEntity(entityData);
            entityStoreBuilder.addEntityName(name);
            storedEntityIds.add(entityData.getId());
            externalReferences.remove(entityData.getId());
            entityManager.deactivateForStorage(entity);
        }
    }

    public EntityData.EntityStore finaliseStore() {
        return entityStoreBuilder.build();
    }

    public TIntSet getExternalReferences() {
        return externalReferences;
    }

    @Override
    public boolean loadingRef(int id) {
        return true;
    }

    @Override
    public void savingRef(int id) {
        if (!storedEntityIds.contains(id)) {
            externalReferences.add(id);
        }
    }
}