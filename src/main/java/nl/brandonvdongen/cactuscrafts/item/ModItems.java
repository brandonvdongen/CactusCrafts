package nl.brandonvdongen.cactuscrafts.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.ModEntityTypes;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CactusCrafts.MOD_ID);

    public static final RegistryObject<Item> CACTUS = ITEMS.register("cactus",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.CACTUS_TAB)));

    public static final RegistryObject<Item> AUTOMATON_SPAWN_EGG = ITEMS.register("automaton_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.AUTOMATON, 0x948e8d, 0x3b3635,
                    new Item.Properties().tab(ModCreativeModeTab.CACTUS_TAB)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
