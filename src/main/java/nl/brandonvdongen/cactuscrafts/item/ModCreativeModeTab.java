package nl.brandonvdongen.cactuscrafts.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab CACTUS_TAB = new CreativeModeTab("cactustab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.CACTUS.get());
        }
    };
}
