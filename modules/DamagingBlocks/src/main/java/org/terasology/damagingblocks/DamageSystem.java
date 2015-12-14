/*
 * Copyright 2015 MovingBlocks
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
package org.terasology.damagingblocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.damagingblocks.component.DamagingBlockComponent;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.characters.CharacterMovementComponent;
import org.terasology.logic.characters.events.OnEnterBlockEvent;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;

/**
 * Created by Sérgio Domingues(skit) on 29/11/2015.
 */

@RegisterSystem(RegisterMode.AUTHORITY)
public class DamageSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(DamageSystem.class);

    @In
    private BlockEntityRegistry blockEntityProvider;

    @In
    private Time time;

    @In
    private EntityManager entityManager;

    @In
    private WorldProvider worldProvider;

    @Override
    public void update(float delta) {
       //logger.info("update damaging");

        for (EntityRef entity : entityManager.getEntitiesWith(DamagingBlockComponent.class, LocationComponent.class)) {
            DamagingBlockComponent damaging = entity.getComponent(DamagingBlockComponent.class);
            LocationComponent loc = entity.getComponent(LocationComponent.class);

            long gameTime = time.getGameTimeInMs();

            if (gameTime > damaging.nextDamageTime) {
                //damage the entity
                EntityRef lavaBlock = blockEntityProvider.getBlockEntityAt(loc.getWorldPosition());
                entity.send(new DoDamageEvent(damaging.blockDamage, EngineDamageTypes.PHYSICAL.get(), lavaBlock));
                // set the next damage time
                damaging.nextDamageTime = gameTime + damaging.timeBetweenDamage;
                entity.saveComponent(damaging);
            }
        }
    }

    @ReceiveEvent
    public void onEnterBlock(OnEnterBlockEvent event, EntityRef entity){

        //ignores "flying" lava
        //future rework will consider "flying" damage blocks
        if(isAtHeadLevel(event.getCharacterRelativePosition(), entity))
            return;

        if(blockIsDamaging(event.getNewBlock())){

            DamagingBlockComponent damaging = entity.getComponent(DamagingBlockComponent.class);

            if(damaging == null){
                damaging = new DamagingBlockComponent();
                damaging.nextDamageTime = time.getGameTimeInMs();
                entity.addComponent(damaging);
            }else{
                damaging.nextDamageTime = time.getGameTimeInMs() + damaging.timeBetweenDamage;
                entity.saveComponent(damaging);
            }
        }
        else{
            //check if it was damaged before and removes damaging component
            DamagingBlockComponent damagingOld = entity.getComponent(DamagingBlockComponent.class);

            if(damagingOld != null){
                //clean up damagingComponent
                entity.removeComponent(DamagingBlockComponent.class);
            }
        }
    }

    private boolean isAtHeadLevel(Vector3i relativePosition, EntityRef entity) {
        CharacterMovementComponent characterMovementComponent = entity.getComponent(CharacterMovementComponent.class);
        return (int) Math.ceil(characterMovementComponent.height) - 1 == relativePosition.y;
    }

    //TODO: change to block.isDamaging() (it's not implemented)
    //working only for lava locks atm
    private boolean blockIsDamaging(Block block) {
        return block.isLava();
    }

}
