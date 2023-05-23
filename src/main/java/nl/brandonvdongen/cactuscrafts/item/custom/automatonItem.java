package nl.brandonvdongen.cactuscrafts.item.custom;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.ModEntityTypes;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;
import nl.brandonvdongen.cactuscrafts.interfaces.ICanSpawnEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class automatonItem extends Item implements ICanSpawnEntity<AutomatonEntity> {

    public automatonItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return pStack.hasTag();
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        return Math.round(13.0F * Mth.clamp(pStack.getTag().getFloat(AutomatonEntity.TENSION_NBT_KEY) / ((float) AutomatonEntity.MAX_TENSION), 0, 1));
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        return Mth.hsvToRgb(0.45f,1,1);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        if(pStack.hasTag()) {
            try {
            float totalSecs = pStack.getTag().getFloat(AutomatonEntity.TENSION_NBT_KEY) / 20;
            float minutes = totalSecs / 60;
            float seconds = totalSecs % 60;
            String timeString = String.format("%01d:%02d", (int) minutes, (int) seconds);


            pTooltipComponents.add(new TranslatableComponent("item." + CactusCrafts.MOD_ID + ".automaton.tooltip.tension", new TextComponent(timeString)).withStyle(ChatFormatting.DARK_GRAY));


            ListTag heldItems = pStack.getTag().getList("HandItems", Tag.TAG_COMPOUND);
            CompoundTag mainHandNbt = heldItems.getCompound(0);

            ItemStack item = ItemStack.of(mainHandNbt);
            if(!item.isEmpty()) {
                pTooltipComponents.add(new TranslatableComponent("item." + CactusCrafts.MOD_ID + ".automaton.tooltip.item", new TextComponent(item.getHoverName().getString()), new TextComponent(Integer.toString(item.getCount()))).withStyle(ChatFormatting.DARK_GRAY));
            }
            } catch (Exception e){
                System.out.println("Shit broke!"+e.getMessage());
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().isClientSide)return InteractionResult.SUCCESS;

        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockState blockstate = level.getBlockState(blockpos);
        BlockPos spawnPos;
        if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
            spawnPos = blockpos;
        } else {
            spawnPos = blockpos.relative(direction);
        }

        AutomatonEntity entity = SpawnEntityFromItem(context.getItemInHand(), level, spawnPos.getX()+.5f, spawnPos.getY(), spawnPos.getZ()+.5f);
        if(context.getPlayer() != null)entity.tame(context.getPlayer());
        return InteractionResult.SUCCESS;
    }



    public AutomatonEntity SpawnEntityFromItem(ItemStack item, Level level, float x, float y, float z) {
        return SpawnEntityFromItem(item, level, new BlockPos(x,y,z));
    }
    public AutomatonEntity SpawnEntityFromItem(ItemStack item, Level level, BlockPos pos) {
        var entity = ModEntityTypes.AUTOMATON.get().create(level);
        if(item.getTag() != null)entity.deserializeNBT(item.getTag());
        if(item.hasCustomHoverName())entity.setCustomName(item.getHoverName());
        entity.setPos(pos.getX(),pos.getY(),pos.getZ());
        level.addFreshEntity(entity);
        item.shrink(1);
        return entity;
    }
}
