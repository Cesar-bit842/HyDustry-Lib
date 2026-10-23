package Hytale.Modification.hydustrylib;

import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit; 
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger; 
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path; 
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import Hytale.Modification.hydustrylib.Components.ConsumerMachineComponent;
import Hytale.Modification.hydustrylib.Components.ConveyorTransporterComponent;
import Hytale.Modification.hydustrylib.Components.HarvestingEngineComponent;
import Hytale.Modification.hydustrylib.Interactions.ConsumerMachineOpenInputItemsHyDustryInteraction;
import Hytale.Modification.hydustrylib.Interactions.ConsumerMachineOpenOutputItemsHyDustryInteraction; 
import Hytale.Modification.hydustrylib.Initializers.ConsumerMachineInitializer;
import Hytale.Modification.hydustrylib.Initializers.ConveyorTransporterInitializer;
import Hytale.Modification.hydustrylib.Initializers.HarvestingEngineInitializer;
import Hytale.Modification.hydustrylib.Listenings.ConsumerMachineListening;
import Hytale.Modification.hydustrylib.Listenings.ConveyorTransporterListening;
import Hytale.Modification.hydustrylib.Listenings.HarvestingEngineListening;
import Hytale.Modification.hydustrylib.Modifications.ItemComponentListening;
import Hytale.Modification.hydustrylib.TypesOfConsumerMachineOperation.Extractor;

import javax.annotation.Nonnull; 
public class HyDustryLib extends JavaPlugin {
  private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
  protected static HyDustryLib instance;  

  public static Map<String, Consumer<ConsumerMachineComponent>> ConsumerMachineOperationTypes = new HashMap<>();
  public static Map<String, Consumer<ConveyorTransporterComponent>> ConveyorOperationTypes = new HashMap<>();
  
  private ComponentType<ChunkStore, ConsumerMachineComponent> consumerMachineComponentType;  
  private ComponentType<ChunkStore, ConveyorTransporterComponent> conveyorTransporterComponentType;  
  private ComponentType<ChunkStore, HarvestingEngineComponent> harvestingEngineComponent;  

  public HyDustryLib(@Nonnull JavaPluginInit init) {
    super(init);
  }
  
  protected void setup() { 
    instance = this; 
    super.setup();   
    this.getCodecRegistry(Interaction.CODEC).register("Open Input Items Container", ConsumerMachineOpenInputItemsHyDustryInteraction.class, ConsumerMachineOpenInputItemsHyDustryInteraction.CODEC);
    this.getCodecRegistry(Interaction.CODEC).register("Open Output Items Container", ConsumerMachineOpenOutputItemsHyDustryInteraction.class, ConsumerMachineOpenOutputItemsHyDustryInteraction.CODEC); 
    this.getEntityStoreRegistry().registerSystem(new ItemComponentListening(ItemComponent.getComponentType())); 
    this.consumerMachineComponentType = this.getChunkStoreRegistry().registerComponent(
      ConsumerMachineComponent.class,
      "ConsumerMachineComponent",
      ConsumerMachineComponent.CODEC
    );   
    this.conveyorTransporterComponentType = this.getChunkStoreRegistry().registerComponent(
      ConveyorTransporterComponent.class,
      "ConveyorTransporterComponent",
      ConveyorTransporterComponent.CODEC
    );   
    this.harvestingEngineComponent = this.getChunkStoreRegistry().registerComponent(
      HarvestingEngineComponent.class,
      "HarvestingEngineComponent",
      HarvestingEngineComponent.CODEC
    );   

    this.getChunkStoreRegistry().registerSystem(new ConsumerMachineInitializer());
    this.getChunkStoreRegistry().registerSystem(new ConsumerMachineListening());   

    this.getChunkStoreRegistry().registerSystem(new ConveyorTransporterInitializer());
    this.getChunkStoreRegistry().registerSystem(new ConveyorTransporterListening());  

    this.getChunkStoreRegistry().registerSystem(new HarvestingEngineInitializer());
    this.getChunkStoreRegistry().registerSystem(new HarvestingEngineListening());  
    LoadedMessageInServerStartup(); 
    AddConsumerMachineOperationType("Essence Extractor", Extractor::Execute);
  }
  public static void AddConsumerMachineOperationType(String Name, Consumer<ConsumerMachineComponent> Function){ 
      ConsumerMachineOperationTypes.put(Name, Function);
  }
  public static void AddConveyorOperationType(String Name, Consumer<ConveyorTransporterComponent> Function){ 
      ConveyorOperationTypes.put(Name, Function);
  }
  public void LoadedMessageInServerStartup(){
    LOGGER.atInfo().log("|===================================================================================================|");  
    LOGGER.atInfo().log("|---[ HyDustryLibLoader.Library.Information ]=======================================================|");
    LOGGER.atInfo().log("|===================================================================================================|");  
    LOGGER.atInfo().log("| Author: LastKnight");
    LOGGER.atInfo().log("| Community: Hytale Modding Comunnity");
    LOGGER.atInfo().log("| ServerVersion: " + getManifest().getServerVersion());
    LOGGER.atInfo().log("| Version: " + getManifest().getVersion().toString());  
    LOGGER.atInfo().log("| Type: Library");
    LOGGER.atInfo().log("|===================================================================================================|");  
    LOGGER.atInfo().log("|---[ HyDustryLibLoader.Library.Packages ]==========================================================|");  
    LOGGER.atInfo().log("|===================================================================================================|");  
    try { 
      URI jarUri = HyDustryLib.class.getProtectionDomain().getCodeSource().getLocation().toURI(); 
      URI zipUri = new URI("jar:" + jarUri.toString());  
      try (FileSystem jarFileSystem = FileSystems.newFileSystem(zipUri, Collections.emptyMap())) { 
        Path rootInJar = jarFileSystem.getPath("/"); 
        try (Stream<Path> stream = Files.walk(rootInJar)) {
          stream.forEach(path -> {
            String internalPath = path.toString(); 
            if (internalPath.equals("/")) {
              return;
            } 
            int depth = (int) internalPath.chars().filter(ch -> ch == '/').count(); 
            String indent = "  ".repeat(Math.max(0, depth - 1)); 
            String name = path.getFileName() != null ? path.getFileName().toString() : internalPath; 
            if (Files.isDirectory(path)) {
              LOGGER.atInfo().log("|" + indent + "📁 [" + name + "]"); 
            } else {
              try {
                long bytes = Files.size(path);
                String sizeKbs = String.format("%.2f KB", bytes / 1024.0);
                LOGGER.atInfo().log("|" + indent + "📄 " + name + " (" + sizeKbs + ")"); 
              } catch (IOException e) {
                LOGGER.atInfo().log("|" + indent + "📄 " + name); 
              }
            }
          });
        }
      }
    } catch (URISyntaxException e) {
      LOGGER.atInfo().log("|===================================================================================================|");
      LOGGER.atInfo().log("| System: HyDustryLib does not have permission to display its folder directory, this does not affect file uploads."); 
      LOGGER.atInfo().log("|===========================================================================================|");  
    } catch (IOException e) { 
      LOGGER.atInfo().log("|===========================================================================================|"); 
      LOGGER.atInfo().log("| System: HyDustryLib could not display your folder directory; this does not affect file uploads. ");  
      LOGGER.atInfo().log("|===========================================================================================|"); 
    } 
    LOGGER.atInfo().log("|===========================================================================================|"); 
  } 
  
  public static HyDustryLib get() {
    return instance;
  } 
  public ComponentType<ChunkStore, ConsumerMachineComponent> getConsumerMachineComponentType() {
    return this.consumerMachineComponentType;
  } 
  public ComponentType<ChunkStore, ConveyorTransporterComponent> getConveyorTransporterComponentType() {
    return this.conveyorTransporterComponentType;
  }
  public ComponentType<ChunkStore, HarvestingEngineComponent> getHarvestingEngineComponentType() {
    return this.harvestingEngineComponent;
  }
    
}
