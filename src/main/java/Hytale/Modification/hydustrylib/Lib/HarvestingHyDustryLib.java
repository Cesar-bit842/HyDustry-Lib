package Hytale.Modification.hydustrylib.Lib;

import java.time.Instant;
import java.util.Map;
import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

import javax.annotation.Nonnull;
 
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.component.AddReason;  
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref; 
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;  
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.HarvestingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingData;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingStageData;  
import com.hypixel.hytale.server.core.inventory.ItemStack; 
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.BlockModule.BlockStateInfo;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World; 
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockOperations;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection; 
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore; 

public class HarvestingHyDustryLib { 
   
   public static boolean HarvestInSimpleContainer(@Nonnull Vector3i blockPosition, World world, ItemContainerBlock container) {
       
      BlockType blockType = EssentialsUtils.getBlockType(world, blockPosition); 
      if (blockType == null) return false;

      
      BlockGathering gathering = blockType.getGathering();
      if (gathering == null) return false;

      HarvestingDropType harvestingDropType = gathering.getHarvest(); 
      if (harvestingDropType == null) return false; 

      
      int rotationIndex = EssentialsUtils.getBlockRotation(world, blockPosition);
      Vector3d centerPosition = new Vector3d();
      blockType.getBlockCenter(rotationIndex, centerPosition);
      centerPosition.add(blockPosition.x, blockPosition.y, blockPosition.z);

      FarmingData farmingConfig = blockType.getFarming();
      boolean isFarmable = farmingConfig != null && farmingConfig.getStages() != null; 

      Ref<ChunkStore> sectionRef = world.getChunkStore().getChunkSectionReferenceAtBlock(blockPosition.x, blockPosition.y, blockPosition.z);
      if (sectionRef == null || !sectionRef.isValid()) {
         return false;
      }

      Store<ChunkStore> chunkStore = world.getChunkStore().getStore();

      if (isFarmable && farmingConfig.getStageSetAfterHarvest() != null) {
         
         SaveItemsInSimpleContainer(harvestingDropType, blockType, container); 

         Map<String, FarmingStageData[]> stageSets = farmingConfig.getStages();
         FarmingStageData[] stages = stageSets.get(farmingConfig.getStartingStageSet());
         if (stages == null) return false;
         
         int currentStageIndex = stages.length - 1;
         FarmingStageData previousStage = stages[currentStageIndex];
         String newStageSet = farmingConfig.getStageSetAfterHarvest();
         FarmingStageData[] newStages = stageSets.get(newStageSet);
         
         if (newStages != null && newStages.length != 0) {
               Instant now = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime(); 
               int blockIndex = ChunkUtil.indexBlock(blockPosition.x, blockPosition.y, blockPosition.z);
               Ref<ChunkStore> blockRef = BlockModule.getBlockEntity(chunkStore, sectionRef, blockPosition.x, blockPosition.y, blockPosition.z);
               
               FarmingBlock farmingBlock;
               if (blockRef == null) {
                  Holder<ChunkStore> blockEntity = ChunkStore.REGISTRY.newHolder();
                  blockEntity.putComponent(BlockStateInfo.getComponentType(), new BlockModule.BlockStateInfo(blockIndex, sectionRef));
                  farmingBlock = new FarmingBlock();
                  farmingBlock.setLastTickGameTime(now);
                  farmingBlock.setCurrentStageSet(newStageSet);
                  blockEntity.addComponent(FarmingBlock.getComponentType(), farmingBlock);
                  blockRef = chunkStore.addEntity(blockEntity, AddReason.SPAWN); 
               } else { 
                  farmingBlock = (FarmingBlock) chunkStore.ensureAndGetComponent(blockRef, FarmingBlock.getComponentType()); 
               }

               farmingBlock.setCurrentStageSet(newStageSet);
               farmingBlock.setGrowthProgress(0.0F);
               farmingBlock.setExecutions(0);
               farmingBlock.setGeneration(farmingBlock.getGeneration() + 1);
               farmingBlock.setLastTickGameTime(now);
               
               if (blockRef == null) {
                  return false;
               } else {
                  BlockSection section = (BlockSection) chunkStore.getComponent(sectionRef, BlockSection.getComponentType());
                  if (section != null) {
                     section.scheduleTick(blockIndex, now);
                  }
                  
                  newStages[0].apply(chunkStore, sectionRef, blockRef, ChunkUtil.localCoordinate(blockPosition.x), ChunkUtil.localCoordinate(blockPosition.y), ChunkUtil.localCoordinate(blockPosition.z), previousStage); 
                  return true;
               }
         } else {
               BlockOperations.setBlock(world.getChunkStore(), sectionRef, blockPosition.x, blockPosition.y, blockPosition.z, 0, BlockType.EMPTY, 0, 0, 0);
               return false;
         }
      } else {
         
         SaveItemsInSimpleContainer(harvestingDropType, blockType, container); 
         BlockOperations.setBlock(world.getChunkStore(), sectionRef, blockPosition.x, blockPosition.y, blockPosition.z, 0, BlockType.EMPTY, 0, 0, 0); 
         return true;
      }
   }
   public static boolean HarvestValidationGatheringInContainer(@Nonnull Vector3i blockPosition, World world, ItemContainerBlock container){
      
      BlockType blockType = EssentialsUtils.getBlockType(world, blockPosition); 

      
      

      
      BlockGathering Gathering = blockType.getGathering();
      
      if (Gathering != null) {  
         
         HarvestingDropType harvestingDropType = Gathering.getHarvest(); 
         
         if (harvestingDropType != null) { 
            
            Vector3d CenterPosition = new Vector3d();
            
            int IndexRotationBlock = EssentialsUtils.getBlockRotation(world, blockPosition);
            
            blockType.getBlockCenter(IndexRotationBlock, CenterPosition);
            
            CenterPosition.add(new Vector3d(blockPosition.x, blockPosition.y, blockPosition.z));
            
            FarmingData FarmingConfig = blockType.getFarming();
            
            boolean isFarmable = FarmingConfig != null && FarmingConfig.getStages() != null; 
             
            
            if (isFarmable && FarmingConfig.getStageSetAfterHarvest() != null){ 
               
               Map<String, FarmingStageData[]> StangesSet = FarmingConfig.getStages();
               
               FarmingStageData[] stages = (FarmingStageData[])StangesSet.get(FarmingConfig.getStartingStageSet());
                
               if (stages != null) { 
                  
                  int CurrentStageIndex = stages.length - 1;
                  
                  FarmingStageData previousStage = stages[CurrentStageIndex];
                  
                  String newStageSet = FarmingConfig.getStageSetAfterHarvest();
                  
                  FarmingStageData[] NewStages = (FarmingStageData[])StangesSet.get(newStageSet);
                   
                  if (NewStages != null && NewStages.length != 0){
                     
                     Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
                     
                     Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
                      
                     if (chunkRef != null){
                        
                        BlockComponentChunk blockComponentChunk = (BlockComponentChunk)chunkStore.getComponent(chunkRef, BlockComponentChunk.getComponentType());
                         
                        if (blockComponentChunk != null){ 
                           
                         return ValidationItemsInSimpleContainer(harvestingDropType, blockType, container);  
                        }else{return false;} 
                     }else{return false;} 
                  }else{ 
                     
                     return ValidationItemsInSimpleContainer(harvestingDropType, blockType, container); 
                  }
               }else{return false;} 
            }else{
               
               return ValidationItemsInSimpleContainer(harvestingDropType, blockType, container); 
            }
         }else{return false;}
      }else{return false;}
   }
   public static boolean HarvestBlockValidation(@Nonnull Vector3i blockPosition, World world){
      
      BlockType blockType = EssentialsUtils.getBlockType(world, blockPosition); 

      
      

      
      BlockGathering Gathering = blockType.getGathering();
      
      if (Gathering != null) {  
         
         HarvestingDropType harvestingDropType = Gathering.getHarvest(); 
         
         if (harvestingDropType != null) { 
            
            Vector3d CenterPosition = new Vector3d();
            
            int IndexRotationBlock = EssentialsUtils.getBlockRotation(world, blockPosition);
            
            blockType.getBlockCenter(IndexRotationBlock, CenterPosition);
            
            CenterPosition.add(new Vector3d(blockPosition.x, blockPosition.y, blockPosition.z));
            
            FarmingData FarmingConfig = blockType.getFarming();
            
            boolean isFarmable = FarmingConfig != null && FarmingConfig.getStages() != null; 
             
            
            if (isFarmable && FarmingConfig.getStageSetAfterHarvest() != null){ 
               
               Map<String, FarmingStageData[]> StangesSet = FarmingConfig.getStages();
               
               FarmingStageData[] stages = (FarmingStageData[])StangesSet.get(FarmingConfig.getStartingStageSet());
                
               if (stages != null) { 
                  
                  int CurrentStageIndex = stages.length - 1;
                  
                  FarmingStageData previousStage = stages[CurrentStageIndex];
                  
                  String newStageSet = FarmingConfig.getStageSetAfterHarvest();
                  
                  FarmingStageData[] NewStages = (FarmingStageData[])StangesSet.get(newStageSet);
                   
                  if (NewStages != null && NewStages.length != 0){
                     
                     Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
                     
                     Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
                      
                     if (chunkRef != null){
                        
                        BlockComponentChunk blockComponentChunk = (BlockComponentChunk)chunkStore.getComponent(chunkRef, BlockComponentChunk.getComponentType());
                         
                        if (blockComponentChunk != null){
                         return true;  
                        }else{return false;} 
                     }else{return false;} 
                  }else{return true;}
               }else{return false;} 
            }else{return true;}
         }else{return false;}
      }else{return false;}
   }
   
   
   protected static void SaveItemsInSimpleContainer(@Nonnull HarvestingDropType harvestingDropType, @Nonnull BlockType blockType, @Nonnull ItemContainerBlock container) {
      
      String itemId = harvestingDropType.getItemId();
      
      String dropListId = harvestingDropType.getDropListId(); 
      
      container.getItemContainer().addItemStacks(BlockHarvestUtils.getDrops(blockType, 1, itemId, dropListId));
   } 
   protected static boolean ValidationItemsInSimpleContainer(@Nonnull HarvestingDropType harvestingDropType, @Nonnull BlockType blockType, @Nonnull ItemContainerBlock container) {
      
      String itemId = harvestingDropType.getItemId();
      
      String dropListId = harvestingDropType.getDropListId(); 
      
      for (ItemStack Drop : BlockHarvestUtils.getDrops(blockType, 1, itemId, dropListId)) {
           
         if(ItemContainerHyDustryLib.ValidationItemForAdding(container.getItemContainer(), Drop)){ return true; } 
      } 
      
      return false;
   } 
} 




















































































































































