package Hytale.Modification.hydustrylib.Modifications;
 
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Vector3d;
import org.joml.Vector3i;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason; 
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem; 
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;  
import com.hypixel.hytale.server.core.universe.world.World; 
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;

import Hytale.Modification.hydustrylib.Components.ConsumerMachineComponent;
import Hytale.Modification.hydustrylib.Components.ConveyorTransporterComponent;  
import Hytale.Modification.hydustrylib.Lib.EntityHyDustryLib;
import Hytale.Modification.hydustrylib.Lib.EssentialsUtils;
import Hytale.Modification.hydustrylib.Lib.FillerBlocksHyDustryLib; 
import Hytale.Modification.hydustrylib.Lib.ItemContainerHyDustryLib; 
import Hytale.Modification.hydustrylib.Lib.RailsHyDustryLib; 

public class ItemComponentListening extends EntityTickingSystem<EntityStore> {
 
  private final ComponentType<EntityStore, ItemComponent> ItemComponentType;

  public ItemComponentListening(ComponentType<EntityStore, ItemComponent> ItemComponentType) {
    this.ItemComponentType = ItemComponentType;
  }

  @Override
  public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
      @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
 
    Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
    ItemComponent itemComponent = archetypeChunk.getComponent(index, ItemComponentType);    
    World world = store.getExternalData().getWorld();
    world.execute(() -> { 
        TransformComponent transform = (TransformComponent) store.getComponent(ref, TransformComponent.getComponentType());  
        Vector3d Position = transform.getPosition();  
        Vector3d StartPosition = new Vector3d(Math.floor(Position.x), Math.floor(Position.y), Math.floor(Position.z));
        Vector3d EndPosition = new Vector3d(Position.x, Position.y - 2, Position.z); 

        Vector3d direction = new Vector3d(EndPosition).sub(StartPosition);
        double distanciaMax = direction.length();
        direction.normalize(); 
 
        Vector3d RaycastResolver = TargetUtil.getTargetLocation(
            world, 
            (blockId) -> blockId != 0, 
            StartPosition.x, StartPosition.y, StartPosition.z, 
            direction.x, direction.y, direction.z, 
            distanciaMax
        ); 
        
        if (RaycastResolver == null) {
          return;
        }  

        Vector3i FixedPosition =FillerBlocksHyDustryLib.CalculateRealPositionUsingFillerBlocks(world, new Vector3i((int)RaycastResolver.x, (int)RaycastResolver.y, (int)RaycastResolver.z));  
        BlockType blockType = EssentialsUtils.getBlockType(world, FixedPosition); 
          
        if (blockType == null || blockType.getData() == null){  
          EntityHyDustryLib.ToggleBehaviourInteractionItems(transform, true, store, ref);  
          return;
        } 
        Map<String, String[]> RawTags = blockType.getData().getRawTags();
        if(!RawTags.containsKey("HyDustryUtility.Behaviour")){
          EntityHyDustryLib.ToggleBehaviourInteractionItems(transform, true, store, ref);
          return;
        }
           
        Store<ChunkStore> chunkStore = world.getChunkStore().getStore(); 
        Ref<ChunkStore> blockRef = EssentialsUtils.getBlockRef(world, FixedPosition);
                           
        ConveyorTransporterComponent ConveyorTransporter = (ConveyorTransporterComponent) chunkStore.getComponent(blockRef, ConveyorTransporterComponent.getComponentType());   
        if(ConveyorTransporter == null) { 
          EntityHyDustryLib.ToggleBehaviourInteractionItems(transform, true, store, ref);
          return;
        }

        EntityHyDustryLib.ToggleBehaviourInteractionItems(transform, false, store, ref);
        int RotationIndex = EssentialsUtils.getBlockRotation(world, FixedPosition);
        Vector3d PositionNormalized = new Vector3d(FixedPosition.x, FixedPosition.y, FixedPosition.z);      
        Vector3d[] Points = RailsHyDustryLib.TagsPathsConversion(RawTags);
        Vector3d[] Rails = RailsHyDustryLib.OrientRails(Points, RotationIndex); 
        Vector3d NextPosition = RailsHyDustryLib.RailsCalculateNextPosition(Rails, ConveyorTransporter.Progress, 0); 
        Vector3d Position_ = new Vector3d(PositionNormalized.x + NextPosition.x, PositionNormalized.y + NextPosition.y, PositionNormalized.z + NextPosition.z);
             
        Vector3d[] SaveItemsPosition = RailsHyDustryLib.ConvertingTagstoArrays(RawTags, "Items.Save.Positions");
        SaveItemsPosition = (SaveItemsPosition != null) ? SaveItemsPosition : new Vector3d[]{ new Vector3d(0, 0, 1) }; 
        for (Vector3d PositionSave : SaveItemsPosition) {
          Ref<ChunkStore> Reference = EssentialsUtils.getBlockRef(world, 
            EssentialsUtils.getValidRelativePosition(world, FixedPosition, PositionSave)
          );
          if(ConveyorTransporter.Progress >= 100f && Reference != null && itemComponent != null){ 
            SaveItemInPosition(world, Reference, store, itemComponent, ref);
          } 
        } 
        transform.setPosition(Position_);
        ConveyorTransporter.LookingBehaviour = RawTags.containsKey("HyDustryUtility.Behaviour.Loocking") ? true : false;
        ConveyorTransporter.ItemsDetected = true;  
    });
  }
  public void SaveItemInPosition(World world, Ref<ChunkStore> Reference, Store<EntityStore> store, ItemComponent ItemSave, Ref<EntityStore> Entity){
    if (ItemSave == null) return;  
    Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
    ItemContainerBlock InputItemsHyDustryComponentBlock = (ItemContainerBlock) chunkStore.getComponent(Reference, ItemContainerBlock.getComponentType());
    ItemContainerBlock itemContainerComponentBlock = (ItemContainerBlock) chunkStore.getComponent(Reference, ItemContainerBlock.getComponentType());
    ConsumerMachineComponent ConsumerMachineBlockComponent = (ConsumerMachineComponent) chunkStore.getComponent(Reference, ConsumerMachineComponent.getComponentType()); 

    if(InputItemsHyDustryComponentBlock != null || itemContainerComponentBlock != null || ConsumerMachineBlockComponent != null){    
      ItemStack itemStack = ItemSave.getItemStack(); 
      boolean itemWasConsumed = false;  
      
      if (InputItemsHyDustryComponentBlock != null) { 
          if (ItemContainerHyDustryLib.ValidationItemForAdding(InputItemsHyDustryComponentBlock, itemStack)) {
              InputItemsHyDustryComponentBlock.getItemContainer().addItemStack(itemStack); 
              itemWasConsumed = true;  
          }
      }
      if (!itemWasConsumed && itemContainerComponentBlock != null) {
          if (ItemContainerHyDustryLib.ValidationItemForAdding(itemContainerComponentBlock.getItemContainer(), itemStack)) {
              itemContainerComponentBlock.getItemContainer().addItemStack(itemStack); 
              itemWasConsumed = true;  
          }
      }
      if (!itemWasConsumed && ConsumerMachineBlockComponent != null) {
          if (ItemContainerHyDustryLib.ValidationItemForAdding(ConsumerMachineBlockComponent.GetInputItemsHyDustryComponent().getItemContainer(), itemStack)) {
              ConsumerMachineBlockComponent.GetInputItemsHyDustryComponent().getItemContainer().addItemStack(itemStack); 
              itemWasConsumed = true;  
          }
      }
      if (itemWasConsumed) { 
        if (Entity != null && Entity.isValid()) { 
          store.removeEntity(Entity, RemoveReason.REMOVE);
        } 
      }
    }
  }

  @Nullable
  @Override
  public SystemGroup<EntityStore> getGroup() {
    return DamageModule.get().getGatherDamageGroup();
  }

  @Nonnull
  @Override
  public Query<EntityStore> getQuery() {
    return Query.and(this.ItemComponentType);
  }
}  