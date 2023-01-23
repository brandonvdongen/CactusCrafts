package nl.brandonvdongen.cactuscrafts.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CactusCrafts.MOD_ID);

    public static final RegistryObject<Item> CACTUS = ITEMS.register("cactus", () -> new Item(new Item.Properties().tab(ModCreativeModeTab.CACTUS_TAB)));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
