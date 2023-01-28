package nl.brandonvdongen.cactuscrafts.item.custom;


import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.ModEntityTypes;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class automatonItem extends Item {

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

            ItemStack item = ItemStack.of(pStack.getTag().getCompound(AutomatonEntity.ITEM_NBT_KEY));
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
        ItemStack itemstack = context.getItemInHand();
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockState blockstate = level.getBlockState(blockpos);
        BlockPos blockpos1;
        if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
            blockpos1 = blockpos;
        } else {
            blockpos1 = blockpos.relative(direction);
        }
        var entity = ModEntityTypes.AUTOMATON.get().create(context.getLevel());
        entity.setPos(blockpos1.getX(),blockpos1.getY(), blockpos1.getZ());
        if(context.getItemInHand().getTag() != null)entity.setNbtData(context.getItemInHand().getTag());
        if(context.getItemInHand().hasCustomHoverName())entity.setCustomName(context.getItemInHand().getHoverName());
        if(context.getPlayer() != null)entity.tame(context.getPlayer());

        context.getLevel().addFreshEntity(entity);
        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }


}
