package nl.brandonvdongen.create.blocks.automatonwinder;

import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.ITE;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import nl.brandonvdongen.create.index.CreateTileEntities;
import nl.brandonvdongen.create.shapes.CAShapes;


public class AutomatonWinderBlock extends HorizontalKineticBlock implements ITE<AutomatonWinderTileEntity> {

	public static final VoxelShape AUTOMATON_WINDER_SHAPE = CAShapes.shape(0,0,0,16,5,16).add(2,0,2,14,16,14).build();

	public AutomatonWinderBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CreateTileEntities.AUTOMATON_WINDER.create(pos, state);
	}

	@Override
	public Class<AutomatonWinderTileEntity> getTileEntityClass() {
		return AutomatonWinderTileEntity.class;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return AUTOMATON_WINDER_SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		/*
		if (!player.getItemInHand(handIn).isEmpty())
			return InteractionResult.PASS;
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;

		withTileEntityDo(worldIn, pos, rollingMill -> {
			boolean emptyOutput = true;
			IItemHandlerModifiable inv = rollingMill.outputInv;
			for (int slot = 0; slot < inv.getSlots(); slot++) {
				ItemStack stackInSlot = inv.getStackInSlot(slot);
				if (!stackInSlot.isEmpty())
					emptyOutput = false;
				player.getInventory().placeItemBackInInventory(stackInSlot);
				inv.setStackInSlot(slot, ItemStack.EMPTY);
			}

			if (emptyOutput) {
				inv = rollingMill.inputInv;
				for (int slot = 0; slot < inv.getSlots(); slot++) {
					player.getInventory().placeItemBackInInventory(inv.getStackInSlot(slot));
					inv.setStackInSlot(slot, ItemStack.EMPTY);
				}
			}

			rollingMill.setChanged();
			rollingMill.sendData();
		});
*/
		return InteractionResult.SUCCESS;
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		super.updateEntityAfterFallOn(worldIn, entityIn);
/*
		if (entityIn.level.isClientSide)
			return;
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!entityIn.isAlive())
			return;

		RollingMillTileEntity rollingMill = null;
		for (BlockPos pos : Iterate.hereAndBelow(entityIn.blockPosition())) {
			rollingMill = getTileEntity(worldIn, pos);
		}
		if (rollingMill == null)
			return;

		ItemEntity itemEntity = (ItemEntity) entityIn;
		LazyOptional<IItemHandler> capability = rollingMill.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
		if (!capability.isPresent())
			return;

		ItemStack remainder = capability.orElse(new ItemStackHandler()).insertItem(0, itemEntity.getItem(), false);
		if (remainder.isEmpty())
			itemEntity.remove(Entity.RemovalReason.KILLED);
		if (remainder.getCount() < itemEntity.getItem().getCount())
			itemEntity.setItem(remainder);
 */
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		/*if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
			withTileEntityDo(worldIn, pos, te -> {
				ItemHelper.dropContents(worldIn, pos, te.inputInv);
				ItemHelper.dropContents(worldIn, pos, te.outputInv);
			});

			worldIn.removeBlockEntity(pos);
		}*/
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction prefferedSide = getPreferredHorizontalFacing(context);
		if (prefferedSide != null)
			return defaultBlockState().setValue(HORIZONTAL_FACING, prefferedSide);
		return super.getStateForPlacement(context);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}

	@Override
	public BlockEntityType<? extends AutomatonWinderTileEntity> getTileEntityType() {
		return CreateTileEntities.AUTOMATON_WINDER.get();
	}
}
