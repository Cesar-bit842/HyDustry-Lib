package Hytale.Modification.hydustrylib.Lib;

import java.time.Instant;
import org.joml.Math; 
import org.joml.Vector3i;
import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType; 
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store; 
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World; 
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockComponentSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import Hytale.Modification.hydustrylib.Components.HarvestingEngineComponent;
import Hytale.Modification.hydustrylib.Lib.HyDustryAPI.Insecure;
import Hytale.Modification.hydustrylib.Lib.HyDustryAPI.safe;

public class FarmingHyDustryLib {  
    @safe 
    public static void ProccesingCoopsInRange(@Nonnull CommandBuffer<ChunkStore> commandBuffer, HarvestingEngineComponent CoreBlock, Vector3i Position, int range, int Fase) {  
        World world = commandBuffer.getExternalData().getWorld();     
        Ref<ChunkStore> StorangeService = EssentialsUtils.getBlockRef(world, Position);
        
        ItemContainerBlock AlmacenLocal = (ItemContainerBlock) commandBuffer.getComponent(StorangeService, ItemContainerBlock.getComponentType());   
 
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    try { 
                        int targetX = Position.x + dx;
                        int targetY = Position.y + dy;   
                        int targetZ = Position.z + dz;    
                        BlockType blockType = EssentialsUtils.getBlockType(world, new Vector3i(targetX, targetY, targetZ)); 
                        Ref<ChunkStore> blockRef = EssentialsUtils.getBlockRef(world, new Vector3i(targetX, targetY, targetZ)); 
                        BlockSection blocksection = EssentialsUtils.getBlockSection(world, new Vector3i(targetX, targetY, targetZ));
                        Vector3i blockPosition = new Vector3i(targetX, targetY, targetZ);
                        
                        if(blockType == null || blockType.getId() == "Empty"){ continue; }

                        if (blockType != null && blockRef != null && blockRef.isValid()) {  
                            if(!blocksection.isTicking(targetX, targetY, targetZ)){
                                blocksection.setTicking(targetX, targetY, targetZ, true);
                            }    
                            
                            if(CoreBlock.Water){ 
                                FarmingHyDustryLib.WateringInBlock(commandBuffer, blockRef, world, CoreBlock.DecayWatering); 
                            }
                            if(CoreBlock.Fertilize){    
                                FarmingHyDustryLib.FetilizedInBlock(commandBuffer, blockRef);
                            }
                            if(CoreBlock.Accelerate){     
                                FarmingHyDustryLib.AccelerateGrowthInBlock(commandBuffer, blockRef, world, blockType); 
                                FarmingHyDustryLib.UpdateGrowthInBlock(commandBuffer, blockRef, world);  
                            }  
                        }  
                        if(AlmacenLocal != null &&CoreBlock.Harvest){  
                            Vector3i UnderBlock = new Vector3i(targetX, targetY - 1, targetZ);
                            Ref<ChunkStore> UnderBlockRef = EssentialsUtils.getBlockRef(world, UnderBlock);
                            FarmingBlock Underfarmingcomponent = (FarmingBlock) commandBuffer.getComponent(UnderBlockRef, FarmingBlock.getComponentType());    

                            if((AlmacenLocal.getItemContainer().toProtocolMap().size() - 1) == ((int)AlmacenLocal.getItemContainer().getCapacity() - 1)){
                                if(!HarvestingHyDustryLib.HarvestValidationGatheringInContainer(blockPosition, world, AlmacenLocal)){ 
                                        continue; 
                                }  
                            }
                            if(HarvestingHyDustryLib.HarvestBlockValidation(UnderBlock, world) || Underfarmingcomponent != null){ 
                                continue;
                            } 
                            HarvestingHyDustryLib.HarvestInSimpleContainer(blockPosition, world, AlmacenLocal);  
                        } 
                    } catch (Exception e) {} 
                }
            }
       } 
    } 

    @safe 
    public static void AccelerateGrowthInBlock(@Nonnull CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> blockRef, World world, BlockType ProccesingBlockType){
        FarmingBlock farmingBlock = (FarmingBlock) commandBuffer.getComponent(blockRef, FarmingBlock.getComponentType());    
        
        if (farmingBlock != null) {   
            Instant now = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();
            float currentProgress = farmingBlock.getGrowthProgress();
            int currentStageIndex = (int) currentProgress;
            float stageProgress = currentProgress - currentStageIndex;
             
            if (stageProgress < 0.9f) {
                float newProgress = currentStageIndex + 0.9999f;
                farmingBlock.setGrowthProgress(newProgress);
                farmingBlock.setLastTickGameTime(now); 
            }

        }
    }

    @safe 
    public static void WateringAndFetilizedInBlock(@Nonnull CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> blockRef, World world, float WateringTime){
        int AmountTimeWatering = (int) Math.ceil((600 / 10) * WateringTime);
        TilledSoilBlock soil = (TilledSoilBlock) commandBuffer.getComponent(blockRef, TilledSoilBlock.getComponentType());  
        if (soil != null) { 
            Instant now = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();
            Instant nextWatering = now.plusSeconds(AmountTimeWatering); 
            soil.setWateredUntil(nextWatering); 
            soil.setDecayTime(now.plusSeconds(365000 * 86400)); 
            soil.setFertilized(true);
        }
    }
    
    @safe 
    public static void WateringInBlock(@Nonnull CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> blockRef, World world, float WateringTime){
        int AmountTimeWatering = (int) Math.ceil((600 / 10) * WateringTime);
        TilledSoilBlock soil = (TilledSoilBlock) commandBuffer.getComponent(blockRef, TilledSoilBlock.getComponentType());  
        if (soil != null) { 
            Instant now = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();
            Instant nextWatering = now.plusSeconds(AmountTimeWatering); 
            soil.setWateredUntil(nextWatering); 
            soil.setDecayTime(now.plusSeconds(365000 * 86400));  
        }
    }
     
    @safe 
    public static void FetilizedInBlock(@Nonnull CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> blockRef){ 
        TilledSoilBlock soil = (TilledSoilBlock) commandBuffer.getComponent(blockRef, TilledSoilBlock.getComponentType());  
        if (soil != null) {  
            soil.setFertilized(true);
        }
    }
     
    @safe 
    public static void UpdateGrowthInBlock(@Nonnull CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> blockRef, World world){
        FarmingBlock farmingBlock = (FarmingBlock) commandBuffer.getComponent(blockRef, FarmingBlock.getComponentType());  
        BlockSection blockSection = (BlockSection) commandBuffer.getComponent(blockRef, BlockSection.getComponentType());
        if (farmingBlock != null){    
            Instant now = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();   
            blockSection.scheduleTick(blockRef.getIndex(), now.plusSeconds(1));  
        }
    } 
      
    @Insecure 
    public static void AccelerateCropGrowth(@Nonnull Vector3i blockPosition, @Nonnull World world){
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore(); 
        Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
        BlockComponentSection blockComponentChunk = (BlockComponentSection)chunkStore.getComponent(chunkRef, BlockComponentSection.getComponentType());
        int BlockIndexColumn = ChunkUtil.indexBlock(blockPosition.x, blockPosition.y, blockPosition.z); 
        Ref<ChunkStore> blockRef = blockComponentChunk.getBlockReference(BlockIndexColumn); 
                           
        FarmingBlock farmingBlock = (FarmingBlock) chunkStore.getComponent(blockRef, FarmingBlock.getComponentType());    
        
        if (farmingBlock != null) {   
            Instant now = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();
            float currentProgress = farmingBlock.getGrowthProgress();
            int currentStageIndex = (int) currentProgress;
            float stageProgress = currentProgress - currentStageIndex;
             
            if (stageProgress < 0.9f) {
                float newProgress = currentStageIndex + 0.9999f;
                farmingBlock.setGrowthProgress(newProgress);
                farmingBlock.setLastTickGameTime(now); 
            }

        }
    }
 
    @Insecure 
    public static void ScheduleInstantUpdate(@Nonnull CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> blockRef, World world){ 
        BlockSection blockSection = (BlockSection) commandBuffer.getComponent(blockRef, BlockSection.getComponentType()); 
        Instant now = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime();   
        blockSection.scheduleTick(blockRef.getIndex(), now.plusSeconds(1));   
    }
}
