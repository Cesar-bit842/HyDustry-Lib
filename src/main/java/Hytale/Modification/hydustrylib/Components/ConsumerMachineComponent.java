package Hytale.Modification.hydustrylib.Components;
  
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type; 

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList; 
import java.util.Map;
import java.util.LinkedHashMap; 
import java.util.List; 

import java.util.function.Consumer;
import javax.annotation.Nullable;

import org.joml.Vector3d;
import org.joml.Vector3i;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec; 
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store; 
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.inventory.ItemStack; 
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore; 

import Hytale.Modification.hydustrylib.HyDustryLib;
import Hytale.Modification.hydustrylib.Lib.ItemContainerHyDustryLib; 

public class ConsumerMachineComponent implements Component<ChunkStore> {
    private static final Gson GSON = new Gson();
    public long ManufacturingTime = 1000; 
    public boolean RequireEnergy = false; 
    public boolean RequireFuel = false; 
    public ItemContainerBlock InputItems;
    public ItemContainerBlock OutputItems;
    public String OperationType = "Default"; 
    public String LastManufacture = "1970-01-01 00:00:00"; 
    public String SystemRaw = "Undefined"; 
    protected String Position = "[Do Not Modify]";  
    public static final BuilderCodec<ConsumerMachineComponent> CODEC =
        BuilderCodec.builder(ConsumerMachineComponent.class, ConsumerMachineComponent::new)  
            .append(new KeyedCodec<>("Manufacturing time (1s -> 1000mls)", Codec.LONG),
                    (state, nr) -> state.ManufacturingTime = nr,
                    s -> s.ManufacturingTime)
            .add()   
            .append(new KeyedCodec<>("It requires energy", Codec.BOOLEAN),
                    (state, nr) -> state.RequireEnergy = nr,
                    s -> s.RequireEnergy)
            .add()   
            .append(new KeyedCodec<>("It requires fuel", Codec.BOOLEAN),
                    (state, nr) -> state.RequireFuel = nr,
                    s -> s.RequireFuel)
            .add()   
            .append(new KeyedCodec<>("Type of operation", Codec.STRING),
                    (state, nr) -> state.OperationType = nr,
                    s -> s.OperationType)
            .add() 
            .append(new KeyedCodec<>("InputItemsContainer", ItemContainerBlock.CODEC),
                    (state, nr) -> state.InputItems = nr,
                    s -> s.InputItems)
            .add()
            .append(new KeyedCodec<>("OutputItemsContainer", ItemContainerBlock.CODEC),
                    (state, nr) -> state.OutputItems = nr,
                    s -> s.OutputItems)
            .add()    
            .append(new KeyedCodec<>("Position by single check [PBSC]", Codec.STRING),
                    (state, nr) -> state.Position = nr,
                    s -> s.Position)
            .add()  
            .append(new KeyedCodec<>("SystemRaw [System]", Codec.STRING),
                    (state, nr) -> state.SystemRaw = nr,
                    s -> s.SystemRaw)
            .add()  
            .append(new KeyedCodec<>("LastManufacture [System]", Codec.STRING),
                    (state, nr) -> state.LastManufacture = nr,
                    s -> s.LastManufacture)
            .add()    
            .build(); 
            
    public ConsumerMachineComponent() {

    } 
    public static ComponentType<ChunkStore, ConsumerMachineComponent>  getComponentType() {
        return HyDustryLib.get().getConsumerMachineComponentType();
    } 
    public void ExecuteBehaviour(){ 
        Consumer<ConsumerMachineComponent> operacion = HyDustryLib.get().ConsumerMachineOperationTypes.getOrDefault(OperationType, (machine) -> {  
                machine.ExecuteBehaviourDefault();  
        }); 
        operacion.accept(this);
    }
    public boolean BehaviourDefault(){
        return (this.OperationType.equals("Undefined") || this.OperationType.equals("Default"));
    }
    public void ExecuteBehaviourDefault(){
        ItemContainerBlock OutputItemsContainer = this.GetOutputItemsHyDustryComponent();
        ItemContainerBlock InputItemsContainer = this.GetInputItemsHyDustryComponent(); 
        List<ItemStack> OuputItemsStacks = this.GetItemsOutput(); 
        if(OuputItemsStacks == null){ return;  }  

        String activeFuelId = null; 
        if (this.RequireFuel) {
                activeFuelId = this.getAvailableFuelId(); 
                if (activeFuelId == null) { 
                        return; 
                }
        }
        if(!this.ManufacturingAvailability()){ return; }
        if(OutputItemsContainer == null || InputItemsContainer == null){ return; } 
        if (!this.ItemsVerification(this.RequireFuel)) {
                return;
        }

        this.processItems(this.RequireFuel);

        if (this.RequireFuel && activeFuelId != null) {
                this.processFuel(activeFuelId);
        }

        long currentProcessTime = this.ManufacturingTime;  
        
        if (this.RequireFuel && activeFuelId != null) { 
                Map<String, Integer> timeUpgrade = this.getAutoUpgradeTimeRequirements();
                if (timeUpgrade != null && !timeUpgrade.isEmpty()) { 
                currentProcessTime = timeUpgrade.values().iterator().next().longValue();
                }
        } 
        this.RenewManufacturingAvailability(currentProcessTime); 
        for (ItemStack stack : OuputItemsStacks){
                if(ItemContainerHyDustryLib.ValidationItemForAdding(OutputItemsContainer.getItemContainer(), stack)){
                        OutputItemsContainer.getItemContainer().addItemStack(stack);
                }
        } 
    }

    public void InitializeConfiguration(Map<String, String[]> SystemVariables){ 
        if(SystemVariables.containsKey("HyDustryUtility.Machine.Behaviour")){
           String[] Array = SystemVariables.get("HyDustryUtility.Machine.Behaviour"); 
           this.OperationType = (Array != null && Array.length > 0) ? Array[0] : "Default";
        }    
        this.SystemRaw = SerializerMap(SystemVariables); 
    }
     
    private static String SerializerMap(Map<String, String[]> map) {
       if (map == null || map.isEmpty()) {
                return "{}";
        }
        return GSON.toJson(map);
    }
    public static Map<String, String[]> DeserializerMap(String serializedData) {
        if (serializedData == null || serializedData.isEmpty() || !serializedData.startsWith("{")) {
                return new LinkedHashMap<>();
        }
        
        try {
                Type type = new TypeToken<LinkedHashMap<String, String[]>>() {}.getType();
                Map<String, String[]> map = GSON.fromJson(serializedData, type);
                return map != null ? map : new LinkedHashMap<>();
        } catch (Exception e) { 
                return new LinkedHashMap<>();
        }
    }
    public List<ItemStack> GetItemsOutput(){ 
        Map<String, Integer> outputs = ConversionOfValuesMap("Output.Items", "Output.Counts");  
        List<ItemStack> outputStacks = new ArrayList<>();
        
        if (outputs == null || outputs.isEmpty()) {
                return outputStacks;  
        }
 
        for (Map.Entry<String, Integer> entry : outputs.entrySet()) {
                String itemId = entry.getKey();
                int count = entry.getValue(); 
                if (itemId == null || itemId.trim().isEmpty() || count <= 0) {
                  continue;
                } 
                ItemStack Item = new ItemStack(itemId, count);
                if(!Item.isValid()){ continue; }
                outputStacks.add(Item);
        }  
        return outputStacks;
    } 

    private Map<String, Integer> ConversionOfValuesMap(String KeysName, String ValuesName) {
        Map<String, String[]> systemRaw = this.DeserializerMap(this.SystemRaw);   
        if (!systemRaw.containsKey("HyDustryUtility.Machine." + KeysName)) {
                return null; 
        }

        String[] itemIds = systemRaw.get("HyDustryUtility.Machine." + KeysName);
        String[] itemCounts = systemRaw.containsKey("HyDustryUtility.Machine." + ValuesName) 
                ? systemRaw.get("HyDustryUtility.Machine." + ValuesName) 
                : new String[0];

        if (itemIds == null || itemIds.length == 0) {
                return null; 
        }
 
        Map<String, Integer> Result = new LinkedHashMap<>(); 
        
        for (int i = 0; i < itemIds.length; i++) {
                String itemId = itemIds[i];
                if (itemId == null || itemId.trim().isEmpty()) {
                continue;
                }

                int count = 1;   
                if (itemCounts != null && i < itemCounts.length) {
                try {
                        count = Integer.parseInt(itemCounts[i].trim());
                        if (count <= 0) count = 1; 
                } 
                catch (NumberFormatException e) {
                        count = 1;  
                }
                }

                Result.put(itemId, Result.getOrDefault(itemId, 0) + count);
        }
         
        return Result;
    }
    public Map<String, Integer> GetItemsRequirements(){
        Map<String, Integer> Items = ConversionOfValuesMap("Requiere.Items", "Requiere.Counts");
        return Items; 
    }
    public Map<String, Integer> GetFuelRequirements(){
        Map<String, Integer> Items = ConversionOfValuesMap("Fuel.Items", "Fuel.Counts");
        return Items; 
    }
    public Map<String, Integer> getAutoUpgradeItemsRequirements() {
        String activeFuelId = getAvailableFuelId(); 
        if (activeFuelId == null) {
                return null;
        } 
        return getUpgradeItemsRequirements(activeFuelId);
    }
    public Map<String, Integer> getAutoUpgradeTimeRequirements() {
        String activeFuelId = getAvailableFuelId(); 
        if (activeFuelId == null) {
                return null;
        } 
        return getUpgradeTimeRequirements(activeFuelId);
    }
    public Map<String, Integer> getUpgradeItemsRequirements(String fuelId) {
        int targetUpgrade = getUpgradeTierIndex(fuelId);
        if (targetUpgrade == -1) return null;

        return ConversionOfValuesMap("Fuel.Upgrades." + targetUpgrade + ".Items", "Fuel.Upgrades." + targetUpgrade + ".Counts");
    } 
    public Map<String, Integer> getUpgradeTimeRequirements(String fuelId) {
        int targetUpgrade = getUpgradeTierIndex(fuelId);
        if (targetUpgrade == -1) return null;

        return ConversionOfValuesMap("Fuel.Upgrades." + targetUpgrade + ".ManufacturingTime", "Fuel.Upgrades." + targetUpgrade + ".TimeCounts");
    } 
    public String GetPositionInWorld(){
        return this.Position;
    }
    private int getUpgradeTierIndex(String fuelId) {
        Map<String, String[]> systemRaw = this.DeserializerMap(this.SystemRaw);
        if (!systemRaw.containsKey("HyDustryUtility.Machine.Fuel.Items")) {
                return -1;
        }

        String[] itemIds = systemRaw.get("HyDustryUtility.Machine.Fuel.Items");
        int fuelIndex = -1;
 
        for (int i = 0; i < itemIds.length; i++) {
                if (itemIds[i] != null && itemIds[i].equals(fuelId)) {
                fuelIndex = i;
                break;
                }
        }

        if (fuelIndex == -1) return -1;
 
        int targetUpgrade = fuelIndex + 1;
 
        int maxUpgradeFound = 1;
        while (systemRaw.containsKey("HyDustryUtility.Machine.Fuel.Upgrades." + (maxUpgradeFound + 1) + ".Items")) {
                maxUpgradeFound++;
        }

        return Math.min(targetUpgrade, maxUpgradeFound);
    } 

    public static boolean GenerateItemEntity(World world, Vector3i Position, ItemStack Item){
        if(Item != null){ Item.setOverrideDroppedItemAnimation(true); } 
        if(Item == null || Item.isEmpty() || !Item.isValid()){ return false; }
        Store<EntityStore> SafetyStore = world.getEntityStore().getStore();
        Holder<EntityStore> itemEntityHolder = ItemComponent.generateItemDrop(
                SafetyStore,
                Item,
                new Vector3d(Position.x + 0.5, Position.y + 0.5, Position.z + 0.5),
                Rotation3f.IDENTITY,
                0.0F, 0.0F, 0.0F);
        if (itemEntityHolder == null) { return false; } 
        EntityScaleComponent ModifierItemComponent = (EntityScaleComponent) itemEntityHolder.getComponent(EntityScaleComponent.getComponentType());
        if (ModifierItemComponent != null) { ModifierItemComponent.setScale(0.25f); } 
        SafetyStore.addEntity(itemEntityHolder, AddReason.SPAWN); 
        return true;
    }
 
    public boolean fuelVerification() {
        return getAvailableFuelId() != null;
    }
    public String getAvailableFuelId() {
        Map<String, Integer> fuelRequirements = GetFuelRequirements();
        if (fuelRequirements == null || fuelRequirements.isEmpty()) {
                return null;
        }
        
        var entries = new ArrayList<>(fuelRequirements.entrySet());

        for (int i = entries.size() - 1; i >= 0; i--) {
                var entry = entries.get(i);
                String fuelId = entry.getKey();
                int requiredCount = entry.getValue();
        
                int currentTotalCount = 0;
                for (short slot = 0; slot < this.InputItems.getItemContainer().getCapacity(); ++slot) {
                ItemStack stack = this.InputItems.getItemContainer().getItemStack(slot);
                if (stack != null && !stack.isEmpty() && stack.getItem().getId().equals(fuelId)) {
                        currentTotalCount += stack.getQuantity();
                }
                }
        
                if (currentTotalCount >= requiredCount) {
                return fuelId;
                }
        }

        return null;
    }
    public boolean processFuel(String targetFuelId) {
        Map<String, Integer> fuelRequirements = GetFuelRequirements();
        if (fuelRequirements == null || !fuelRequirements.containsKey(targetFuelId)) {
                return false;
        }
 
        int requiredCount = fuelRequirements.get(targetFuelId);
 
        int checkCount = 0;
        for (short slot = 0; slot < this.InputItems.getItemContainer().getCapacity(); ++slot) {
                ItemStack stack = this.InputItems.getItemContainer().getItemStack(slot);
                if (stack != null && !stack.isEmpty() && stack.getItem().getId().equals(targetFuelId)) {
                checkCount += stack.getQuantity();
                }
        }
        if (checkCount < requiredCount) {
                return false;
        }
 
        for (short slot = 0; slot < this.InputItems.getItemContainer().getCapacity(); ++slot) {
                ItemStack stack = this.InputItems.getItemContainer().getItemStack(slot);
                if (stack != null && !stack.isEmpty() && stack.getItem().getId().equals(targetFuelId)) {
                int currentQuantity = stack.getQuantity();

                if (requiredCount >= currentQuantity) {
                        this.InputItems.getItemContainer().removeItemStackFromSlot(slot);
                        requiredCount -= currentQuantity;
                } else {
                        this.InputItems.getItemContainer().setItemStackForSlot(slot, stack.withQuantity(currentQuantity - requiredCount));
                        requiredCount = 0;
                }

                if (requiredCount <= 0) {
                        break;
                }
                }
        }

        return true;
    }
    
    public boolean ItemsVerification(boolean FuelRules){
        Map<String, Integer> requirements = (!FuelRules) ? GetItemsRequirements() : getAutoUpgradeItemsRequirements();
        if (requirements == null || requirements.isEmpty()) {
          return false; 
        }
        for (Map.Entry<String, Integer> requirement : requirements.entrySet()) {
                String requiredId = requirement.getKey();
                int requiredCount = requirement.getValue();
                
                int currentTotalCount = 0;
                
                for (short i = 0; i < this.InputItems.getItemContainer().getCapacity(); ++i){
                        ItemStack stack = this.InputItems.getItemContainer().getItemStack(i);
                        if (stack != null && !stack.isEmpty()){
                                if (stack.getItem().getId().equals(requiredId)) {
                                        currentTotalCount += stack.getQuantity();
                                }
                        }
                } 
                
                if (currentTotalCount < requiredCount) {
                        return false;
                }
        }

        return true;
    }
    public void processItems(boolean FuelRules) {
        Map<String, Integer> requirements = (!FuelRules) ? GetItemsRequirements() : getAutoUpgradeItemsRequirements(); 
        if (requirements == null || requirements.isEmpty()) {
          return;  
        }
        for (Map.Entry<String, Integer> requirement : requirements.entrySet()) {
                String requiredId = requirement.getKey();
                int requiredCount = requirement.getValue(); 
 
                if (requiredCount <= 0) {
                        continue;
                }

                for (short i = 0; i < this.InputItems.getItemContainer().getCapacity(); ++i) {
                ItemStack stack = this.InputItems.getItemContainer().getItemStack(i);

                if (stack != null && !stack.isEmpty()) {
                        if (stack.getItem().getId().equals(requiredId)) {
                                int currentQuantity = stack.getQuantity();  

                                if (requiredCount >= currentQuantity) { 
                                        this.InputItems.getItemContainer().removeItemStackFromSlot(i); 
                                        requiredCount -= currentQuantity;
                                } 
                                else { 
                                        this.InputItems.getItemContainer().setItemStackForSlot(i, stack.withQuantity(currentQuantity - requiredCount)); 
                                        requiredCount = 0;
                                }  
                                if (requiredCount <= 0) {
                                        break;
                                }
                        }
                }
                }  
        } 
        }

    public boolean ManufacturingAvailability(){
        DateTimeFormatter Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime Now = LocalDateTime.now();
        if(!this.LastManufacture.equals("1970-01-01 00:00:00")){
                LocalDateTime Last = LocalDateTime.parse(this.LastManufacture, Formatter);
                if (Last.isAfter(Now)){ return false; }
        } 
        return true;
    }
    public boolean RenewManufacturingAvailability(Long time){
        DateTimeFormatter Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime Now = LocalDateTime.now(); 
        LocalDateTime NextDate = Now.plus(time, ChronoUnit.MILLIS);
        this.LastManufacture = NextDate.format(Formatter); 
        
        return true;
    }
    
    public ItemContainerBlock GetInputItemsHyDustryComponent(){
        return this.InputItems;
    }
    public ItemContainerBlock GetOutputItemsHyDustryComponent(){
        return this.OutputItems;
    }
 
    public static String Vector3dToString(Vector3d DeserializeVector) {
        if (DeserializeVector == null) {
            return "(0.0, 0.0, 0.0)";
        }
        return "(" + DeserializeVector.x + ", " + DeserializeVector.y + ", " + DeserializeVector.z + ")";
    } 
    public static Vector3d StringToVector3d(String SerializedVector) {
        if (SerializedVector == null || SerializedVector.trim().isEmpty()) {
            return new Vector3d(0.0, 0.0, 0.0);
        }

        try { 
            String[] parts = SerializedVector.replaceAll("[()\\s+]", "").split(","); 
            if (parts.length >= 3) {
                double x = Double.parseDouble(parts[0].trim());
                double y = Double.parseDouble(parts[1].trim());
                double z = Double.parseDouble(parts[2].trim());
                return new Vector3d(x, y, z);
        }
        } catch (NumberFormatException e) {} 
        return new Vector3d(0.0, 0.0, 0.0);
    }

    public void SetPosition(Vector3d Position){
        this.Position = Vector3dToString(Position);
    }
    public Vector3d GetPosition(){
        return StringToVector3d(Position);
    }
    public boolean ValidatePosition(){
        return this.Position != "[Do Not Modify]";
    }
    
    public Map<String, Double> getAutoUpgradeScannerMultiplicatorRequirements() {
        String activeFuelId = getAvailableFuelId(); 
        if (activeFuelId == null) {
            return null;
        } 
        return getUpgradeScannerMultiplicatorRequirements(activeFuelId);
    }
    public Map<String, Double> getUpgradeScannerMultiplicatorRequirements(String fuelId) {
        int targetUpgrade = getUpgradeTierIndex(fuelId);
        if (targetUpgrade == -1) return null;

        return ConversionOfValuesMapingDouble("Fuel.Scanner.Upgrades." + targetUpgrade + ".Items", "Fuel.Scanner.Upgrades." + targetUpgrade + ".Multiplicator");
    } 
    private Map<String, Double> ConversionOfValuesMapingDouble(String KeysName, String ValuesName) {
        Map<String, String[]> systemRaw = DeserializerMap(this.SystemRaw);   
        if (!systemRaw.containsKey("HyDustryUtility.Machine." + KeysName)) {
            return null; 
        }

        String[] itemIds = systemRaw.get("HyDustryUtility.Machine." + KeysName);
        String[] itemCounts = systemRaw.containsKey("HyDustryUtility.Machine." + ValuesName) 
                ? systemRaw.get("HyDustryUtility.Machine." + ValuesName) 
                : new String[0];

        if (itemIds == null || itemIds.length == 0) {
            return null; 
        }

        Map<String, Double> Result = new LinkedHashMap<>(); 
        for (int i = 0; i < itemIds.length; i++) {
            String itemId = itemIds[i];
            if (itemId == null || itemId.trim().isEmpty()) continue;

            double value = 1.0;   
            if (itemCounts != null && i < itemCounts.length) {
                try {
                    value = Double.parseDouble(itemCounts[i].trim());
                    if (value <= 0.0) value = 1.0; 
                } catch (NumberFormatException e) {
                    value = 1.0;  
                }
            }
            Result.put(itemId, Result.getOrDefault(itemId, 0.0) + value);
        }
        return Result;
    }

    @Override
    @Nullable
    public Component<ChunkStore> clone() {
        ConsumerMachineComponent copy = new ConsumerMachineComponent(); 
        copy.ManufacturingTime = this.ManufacturingTime; 
        copy.RequireEnergy = this.RequireEnergy; 
        copy.RequireFuel = this.RequireFuel; 
        copy.OperationType = this.OperationType; 
        copy.SystemRaw = this.SystemRaw;
        copy.Position = this.Position;
        copy.OutputItems = this.OutputItems;
        copy.InputItems = this.InputItems;
        copy.LastManufacture = this.LastManufacture; 
        copy.InputItems = this.InputItems;
        copy.OutputItems = this.OutputItems;  
        return copy;
    }
}