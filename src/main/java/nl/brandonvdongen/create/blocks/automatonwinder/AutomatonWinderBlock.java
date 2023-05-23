package nl.brandonvdongen.create.blocks.automatonwinder;

import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.logistics.block.funnel.AbstractFunnelBlock;
import com.simibubi.create.foundation.block.ITE;


import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.fixes.EntityIdFix;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.network.NetworkHooks;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;
import nl.brandonvdongen.create.index.CreateTileEntities;
import nl.brandonvdongen.create.shapes.CAShapes;

import javax.annotation.Nullable;


public class AutomatonWinderBlock extends HorizontalKineticBlock implements ITE<AutomatonWinderTileEntity>, IRotate {


	public static VoxelShaper WINDER_SHAPE = CAShapes.shape(1, 1, 1, 14, 14, 16).forDirectional();

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return WINDER_SHAPE.get(state.getValue(HORIZONTAL_FACING));
	}
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction preferred = getPreferredHorizontalFacing(context);
		if ((context.getPlayer() != null && context.getPlayer()
				.isShiftKeyDown()) || preferred == null)
			return super.getStateForPlacement(context);
		return defaultBlockState().setValue(HORIZONTAL_FACING, preferred);
	}

	public AutomatonWinderBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(HORIZONTAL_FACING);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
				.getAxis();
	}

	@Override
	public BlockEntityType<? extends AutomatonWinderTileEntity> getTileEntityType() {
		return CreateTileEntities.AUTOMATON_WINDER.get();
	}

	@Override
	public Class<AutomatonWinderTileEntity> getTileEntityClass() {
		return AutomatonWinderTileEntity.class;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return CreateTileEntities.AUTOMATON_WINDER.create(pos, state);
	}



	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.SLOW;
	}

	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if(!pLevel.isClientSide){
			BlockEntity entity =pLevel.getBlockEntity(pPos);
			if(entity instanceof AutomatonWinderTileEntity){
				NetworkHooks.openGui((ServerPlayer) pPlayer, (AutomatonWinderTileEntity)entity, pPos);
			}else{
				throw new IllegalStateException("Our Container Provider is missing");
			}
		}
		return InteractionResult.sidedSuccess(pLevel.isClientSide());
	}

	@Nullable
	public static Direction getFunnelFacing(BlockState state) {
		if (!(state.getBlock() instanceof AutomatonWinderBlock))
			return null;
		return ((AutomatonWinderBlock) state.getBlock()).getFacing(state);
	}

	protected Direction getFacing(BlockState state) {
		return state.getValue(HORIZONTAL_FACING);
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if(pState.getBlock() != pNewState.getBlock()){
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			if(blockEntity instanceof AutomatonWinderTileEntity){
				((AutomatonWinderTileEntity)blockEntity).drops();
			}
		}
		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}
}
