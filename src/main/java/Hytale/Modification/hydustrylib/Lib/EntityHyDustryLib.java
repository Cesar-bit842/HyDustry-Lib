package Hytale.Modification.hydustrylib.Lib;
 
import org.joml.Vector3d;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.PreventItemMerging;
import com.hypixel.hytale.server.core.modules.entity.item.PreventPickup;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class EntityHyDustryLib { 
    public static boolean EntityIsValid(Ref<EntityStore> ref){
        if(ref == null || !ref.isValid()) {
            return false;  
        } 
        Store<EntityStore> store = ref.getStore();
        if (store == null) {
            return false;  
        }
        UUIDComponent uuidComponent = (UUIDComponent) store.getComponent(ref, UUIDComponent.getComponentType()); 
        if (uuidComponent == null) {
           return false; 
        }
        return true;
    }
    public static void ToggleBehaviourInteractionItems(TransformComponent transform, boolean BH, Store<EntityStore> store, Ref<EntityStore> ref){
        PreventItemMerging preventItemMerging = (PreventItemMerging) store.getComponent(ref, PreventItemMerging.getComponentType());  
        PreventPickup preventPickup = (PreventPickup ) store.getComponent(ref, PreventPickup .getComponentType());   
        if(!BH){
            if(preventItemMerging == null){
                preventItemMerging = (PreventItemMerging) store.addComponent(ref, PreventItemMerging.getComponentType());  
            }   
            if(preventPickup == null){
                preventPickup = (PreventPickup) store.addComponent(ref, PreventPickup.getComponentType());  
            }     
        }
        else{ 
            Vector3d Position = transform.getPosition();  
             Vector3d FloorPosition = new Vector3d(
                Math.floor(Position.x), 
                Math.floor(Position.y), 
                Math.floor(Position.z));
            Vector3d NextPosition = new Vector3d(
                FloorPosition.x + 0.5, 
                FloorPosition.y + 0.25, 
                FloorPosition.z + 0.5);
            if(preventItemMerging != null){ 
                store.removeComponent(ref, PreventItemMerging.getComponentType());  
                transform.setPosition(NextPosition);
            }   
            if(preventPickup != null){ 
                store.removeComponent(ref, PreventPickup.getComponentType());  
                transform.setPosition(NextPosition);
            }    
        }
    } 
}
