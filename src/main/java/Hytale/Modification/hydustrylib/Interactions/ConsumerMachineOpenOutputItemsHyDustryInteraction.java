package Hytale.Modification.hydustrylib.Interactions; 

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.ContainerBlockWindow;
import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler; 
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockOperations;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import Hytale.Modification.hydustrylib.Components.ConsumerMachineComponent; 

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3i;

public class ConsumerMachineOpenOutputItemsHyDustryInteraction extends SimpleBlockInteraction { 
   public static final BuilderCodec<ConsumerMachineOpenOutputItemsHyDustryInteraction> CODEC = BuilderCodec.builder(
            ConsumerMachineOpenOutputItemsHyDustryInteraction.class, ConsumerMachineOpenOutputItemsHyDustryInteraction::new, SimpleBlockInteraction.CODEC
   ).build();
   public static final String OPEN_WINDOW = "OpenWindow";
   public static final String CLOSE_WINDOW = "CloseWindow";

   public ConsumerMachineOpenOutputItemsHyDustryInteraction() {
   }
   protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i pos, @Nonnull CooldownHandler cooldownHandler) {
      Ref<EntityStore> ref = context.getEntity();
      Store<EntityStore> store = ref.getStore();
      Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
      if (playerComponent != null) {
         PlayerRef playerRefComponent = (PlayerRef)commandBuffer.getComponent(ref, PlayerRef.getComponentType());
         if (playerRefComponent != null) {
            ChunkStore chunkStore = world.getChunkStore();
            Ref<ChunkStore> sectionRef = chunkStore.getChunkSectionReferenceAtBlock(pos.x, pos.y, pos.z);
            if (sectionRef != null && sectionRef.isValid()) {
               Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
               Ref<ChunkStore> blockRef = BlockModule.getBlockEntity(chunkComponentStore, sectionRef, pos.x, pos.y, pos.z);
               if (blockRef != null) {
                  ItemContainerBlock itemContainerBlock = ((ConsumerMachineComponent)chunkComponentStore.getComponent(blockRef, ConsumerMachineComponent.getComponentType())).GetOutputItemsHyDustryComponent();
                  if (itemContainerBlock == null) {
                     playerRefComponent.sendMessage(Message.translation("server.interactions.invalidBlockState").param("interaction", this.getClass().getSimpleName()).param("blockState", chunkComponentStore.getArchetype(blockRef).toString()));
                  } else {
                     BlockSection section = (BlockSection)chunkComponentStore.getComponent(sectionRef, BlockSection.getComponentType());
                     if (section != null) {
                        int blockId = section.get(pos.x, pos.y, pos.z);
                        BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockId);
                        if (blockType != null) {
                           int rotationIndex = section.getRotationIndex(pos.x, pos.y, pos.z);
                           ContainerBlockWindow window = new ContainerBlockWindow(pos.x, pos.y, pos.z, rotationIndex, blockType, itemContainerBlock.getItemContainer());
                           Map<UUID, ContainerBlockWindow> windows = itemContainerBlock.getWindows();
                           UUIDComponent uuidComponent = (UUIDComponent)commandBuffer.getComponent(ref, UUIDComponent.getComponentType());

                           assert uuidComponent != null;

                           UUID uuid = uuidComponent.getUuid();
                           if (windows.putIfAbsent(uuid, window) == null) {
                              if (playerComponent.getPageManager().setPageWithWindows(ref, store, Page.Bench, true, new Window[]{window})) {
                                 window.registerCloseEvent((var8) -> onWindowClose(world, ref, uuid, pos, blockType, itemContainerBlock, window, commandBuffer));
                                 if (windows.size() == 1) {
                                    BlockOperations.setBlockInteractionState(chunkStore, sectionRef, pos.x, pos.y, pos.z, blockType, "OpenWindow", false);
                                 }

                                 BlockType interactionState = blockType.getBlockForState("OpenWindow");
                                 if (interactionState == null) {
                                    return;
                                 }

                                 int soundEventIndex = interactionState.getInteractionSoundEventIndex();
                                 if (soundEventIndex == 0) {
                                    return;
                                 }

                                 Vector3d soundPos = new Vector3d();
                                 blockType.getBlockCenter(rotationIndex, soundPos);
                                 soundPos.add((double)pos.x, (double)pos.y, (double)pos.z);
                                 SoundUtil.playSoundEvent3d(ref, soundEventIndex, soundPos, commandBuffer);
                              } else {
                                 windows.remove(uuid, window);
                              }
                           }

                        }
                     }
                  }
               }
            }
         }
      }
   }
   private static void onWindowClose(@Nonnull World world, @Nonnull Ref<EntityStore> ref, @Nonnull UUID uuid, @Nonnull Vector3i pos, @Nonnull BlockType blockType, @Nonnull ItemContainerBlock itemContainerBlock, @Nonnull ContainerBlockWindow window, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
      Map<UUID, ContainerBlockWindow> windows = itemContainerBlock.getWindows();
      windows.remove(uuid, window);
      ChunkStore chunkStore = world.getChunkStore();
      Ref<ChunkStore> sectionRef = chunkStore.getChunkSectionReferenceAtBlock(pos.x, pos.y, pos.z);
      if (sectionRef != null && sectionRef.isValid()) {
         Store<ChunkStore> chunkComponentStore = chunkStore.getStore();
         BlockSection section = (BlockSection)chunkComponentStore.getComponent(sectionRef, BlockSection.getComponentType());
         if (section != null) {
            BlockType currentBlockType = (BlockType)BlockType.getAssetMap().getAsset(section.get(pos.x, pos.y, pos.z));
            if (currentBlockType != null) {
               if (windows.isEmpty()) {
                  if (itemContainerBlock.isStayOpenWhenEmpty() && itemContainerBlock.getItemContainer().isEmpty()) {
                     return;
                  }

                  String defBase = (String)Objects.requireNonNullElse(blockType.getDefaultStateKey(), blockType.getId());
                  String currentBase = (String)Objects.requireNonNullElse(currentBlockType.getDefaultStateKey(), currentBlockType.getId());
                  if (Objects.equals(currentBase, defBase)) {
                     BlockOperations.setBlockInteractionState(chunkStore, sectionRef, pos.x, pos.y, pos.z, currentBlockType, "CloseWindow", false);
                  }
               }

               BlockType interactionState = currentBlockType.getBlockForState("CloseWindow");
               if (interactionState != null) {
                  int soundEventIndex = interactionState.getInteractionSoundEventIndex();
                  if (soundEventIndex != 0) {
                     int rotationIndex = section.getRotationIndex(pos.x, pos.y, pos.z);
                     Vector3d soundPos = new Vector3d();
                     blockType.getBlockCenter(rotationIndex, soundPos);
                     soundPos.add((double)pos.x, (double)pos.y, (double)pos.z);
                     SoundUtil.playSoundEvent3d(ref, soundEventIndex, soundPos, commandBuffer);
                  }
               }
            }
         }
      }
   }
   
   protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
   
    } 
}
