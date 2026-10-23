package Hytale.Modification.hydustrylib.Listenings;
  
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Math;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Rotation3f; 
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World; 
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockComponentSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import Hytale.Modification.hydustrylib.Components.ConsumerMachineComponent;
import Hytale.Modification.hydustrylib.Components.ConveyorTransporterComponent;  
import Hytale.Modification.hydustrylib.Lib.EssentialsUtils;
import Hytale.Modification.hydustrylib.Lib.ItemContainerHyDustryLib;
import Hytale.Modification.hydustrylib.Lib.RailsHyDustryLib; 

public class ConveyorTransporterListening extends EntityTickingSystem { 
    public void tick(float dt, int index, @Nonnull ArchetypeChunk archetypeChunk, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) { 
        BlockSection blockSection = (BlockSection)archetypeChunk.getComponent(index, BlockSection.getComponentType());
        if (blockSection != null) {
            if (blockSection.getTickingBlocksCountCopy() != 0) {
                ChunkSection chunkSection = (ChunkSection)archetypeChunk.getComponent(index, ChunkSection.getComponentType());
                if (chunkSection != null) {
                    Ref<ChunkStore> chunkColumnRef = chunkSection.getChunkColumnReference();
                    if (chunkColumnRef != null && chunkColumnRef.isValid()) {
                        BlockComponentSection Section_ = (BlockComponentSection)archetypeChunk.getComponent(index, BlockComponentSection.getComponentType());
                        if (Section_ != null) { 
                            blockSection.forEachTicking(Section_, commandBuffer, chunkSection.getY(), this::BlockTicking);
                        } 
                    }
                } 
            }
        } 
 
    }
    
    private BlockTickStrategy BlockTicking(BlockComponentSection bcSection, CommandBuffer<ChunkStore> commandBuffer, int localX, int localY, int localZ, int blockIndex) {
      Ref<ChunkStore> blockRef = bcSection.getBlockReference(ChunkUtil.indexBlock(localX, localY, localZ));
      if (blockRef != null) { 
        ConveyorTransporterComponent ConveyorTransporter = (ConveyorTransporterComponent) commandBuffer.getComponent(blockRef, ConveyorTransporterComponent.getComponentType());  
        if (ConveyorTransporter != null) { 
            World world = commandBuffer.getExternalData().getWorld();  
            BlockModule.BlockStateInfo blockStateInfo = (BlockModule.BlockStateInfo)commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
            Vector3i LocalPosition = new Vector3i(localX, localY, localZ);
            Vector3ic globalCoord = EssentialsUtils.getGlobalPosition(LocalPosition, blockStateInfo, commandBuffer);
            Vector3i Position = new Vector3i(globalCoord.x(), globalCoord.y(), globalCoord.z());
            BlockType Blocktype = EssentialsUtils.getBlockType(world, Position);   
            if (blockStateInfo != null) { 
                world.execute(() -> { 
                    boolean ItemsCollins = ConveyorTransporter.ItemsDetected;
                    ConveyorTransporter.ItemsDetected = false; 

                    if(ItemsCollins || ConveyorTransporter.LookingBehaviour){
                        if(ConveyorTransporter.Progress < 100f){
                            ConveyorTransporter.Progress = Math.min(ConveyorTransporter.Progress + ConveyorTransporter.Velocity, 100f);
                        }
                        else{
                            ConveyorTransporter.Progress = 0f;
                            ConveyorTransporter.LookingBehaviour = false;
                        }
                    } 
                }); 
                world.execute(() -> { 
                    if(ConveyorTransporter.Progress == 0f){
                        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();  
                        if (Blocktype == null || Blocktype.getData() == null) return; 
                        Map<String, String[]> RawTags = Blocktype.getData().getRawTags(); 
                        Vector3d[] LoadItemsPosition = RailsHyDustryLib.ConvertingTagstoArrays(RawTags, "Items.Load.Positions");
                        LoadItemsPosition = (LoadItemsPosition != null) ? LoadItemsPosition : new Vector3d[]{ 
                            new Vector3d(0, 0, -1),
                            new Vector3d(1, 0, 0),
                            new Vector3d(-1, 0, 0)
                        }; 
                        for (Vector3d ActualPosition : LoadItemsPosition) { 
                            Ref<ChunkStore> Reference = EssentialsUtils.getBlockRef(world, 
                                EssentialsUtils.getValidRelativePosition(world, Position, ActualPosition)
                            ); 

                            if(Reference == null){ continue; }
                            ItemContainerBlock OutputItemsHyDustryComponentBlock = (ItemContainerBlock) chunkStore.getComponent(Reference, ItemContainerBlock.getComponentType());
                            ItemContainerBlock itemContainerComponentBlock = (ItemContainerBlock) chunkStore.getComponent(Reference, ItemContainerBlock.getComponentType());
                            ConsumerMachineComponent ConsumerMachineComponentBlock = (ConsumerMachineComponent) chunkStore.getComponent(Reference, ConsumerMachineComponent.getComponentType()); 
                                
                            if(OutputItemsHyDustryComponentBlock != null || itemContainerComponentBlock != null || ConsumerMachineComponentBlock != null){
                                if (OutputItemsHyDustryComponentBlock != null && OutputItemsHyDustryComponentBlock.getItemContainer() != null){
                                    GetContainerItem(OutputItemsHyDustryComponentBlock.getItemContainer(), world, Position);
                                }
                                if (itemContainerComponentBlock != null && itemContainerComponentBlock.getItemContainer() != null){
                                    GetContainerItem(itemContainerComponentBlock.getItemContainer(), world, Position); 
                                }
                                if (ConsumerMachineComponentBlock != null && 
                                    ConsumerMachineComponentBlock.GetOutputItemsHyDustryComponent() != null &&
                                    ConsumerMachineComponentBlock.GetOutputItemsHyDustryComponent().getItemContainer() != null){ 
                                    GetContainerItem(ConsumerMachineComponentBlock.GetOutputItemsHyDustryComponent().getItemContainer(), world, Position); 
                                }
                            }
                        } 
                        
                    }
                }); 
                return BlockTickStrategy.CONTINUE; 
            }
            else{
                return BlockTickStrategy.IGNORED;
            } 
        } 
        else {
            return BlockTickStrategy.IGNORED;
        } 
      } 
      else {
        return BlockTickStrategy.IGNORED;
      }
    }
    public void GetContainerItem(@Nonnull SimpleItemContainer Container, World world, Vector3i Position){ 
        ItemStack ItemRecovery = ItemContainerHyDustryLib.getLastItem(Container, 1);
        if(ItemRecovery != null){ ItemRecovery.setOverrideDroppedItemAnimation(true); } 
        if(ItemRecovery != null && !ItemRecovery.isEmpty() && ItemRecovery.isValid()){ 
            Store<EntityStore> SafetyStore = world.getEntityStore().getStore();
            Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(
                SafetyStore, 
                ItemRecovery, 
                new Vector3d(Position.x + 0.5, Position.y + 0.5, Position.z + 0.5), 
                Rotation3f.IDENTITY, 
                0.0F, 0.0F, 0.0F
            ); 
            if (itemEntityHolder == null) {
                return;
            } 

            EntityScaleComponent ModifierItemComponent = (EntityScaleComponent)itemEntityHolder.getComponent(EntityScaleComponent.getComponentType());
            if(ModifierItemComponent != null){
                ModifierItemComponent.setScale(0.25f);
            } 

            SafetyStore.addEntity(itemEntityHolder, AddReason.SPAWN);
            ItemContainerHyDustryLib.removeLastItem(Container, 1);
        }  
    }
    @Nullable
    public Query getQuery() {
         return Query.and(BlockSection.getComponentType(), BlockComponentSection.getComponentType());
    }
}
