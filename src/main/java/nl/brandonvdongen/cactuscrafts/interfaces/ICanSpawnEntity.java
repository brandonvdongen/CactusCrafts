package nl.brandonvdongen.cactuscrafts.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;

public interface ICanSpawnEntity<E extends Entity> {
    public E SpawnEntityFromItem(ItemStack item, Level level, BlockPos pos);

}

