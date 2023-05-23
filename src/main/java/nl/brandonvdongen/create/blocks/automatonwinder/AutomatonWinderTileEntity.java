package nl.brandonvdongen.create.blocks.automatonwinder;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.logistics.block.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.block.funnel.FunnelTileEntity;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.ModEntityTypes;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;
import nl.brandonvdongen.create.networking.AllPackets;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import nl.brandonvdongen.cactuscrafts.networking.packet.WinderFlapPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutomatonWinderTileEntity extends KineticTileEntity implements MenuProvider {
	public AutomatonWinderTileEntity(BlockEntityType<? extends AutomatonWinderTileEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		flap = createChasingFlap();
	}
	LerpedFloat flap;
	private CompoundTag occupant = new CompoundTag();

	private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}
	};

	private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		boolean added = false;

		if (!IRotate.StressImpact.isEnabled())
			return added;
		float stressAtBase = calculateStressApplied();
		if (Mth.equal(stressAtBase, 0))
			return added;


		tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("tooltip."+CactusCrafts.MOD_ID+".automaton_winder.header").withStyle(ChatFormatting.WHITE)));
		tooltip.add(new TextComponent(spacing).append(new TranslatableComponent("tooltip."+CactusCrafts.MOD_ID+".automaton_winder.occupant_info").withStyle(ChatFormatting.GRAY)).append(new TextComponent(Float.toString(occupant.getFloat("tension"))).withStyle(ChatFormatting.AQUA)));
		Lang.translate("gui.goggles.kinetic_stats")
				.forGoggles(tooltip);

		addStressImpactStats(tooltip, stressAtBase);

		return true;

	}

	@Override
	public void tick() {
		flap.tickChaser();

		if(!level.isClientSide){
			if(!occupant.isEmpty()) {
				try {
					float tension = occupant.getFloat("tension");
					tension = Math.min(tension + +Math.abs(this.speed / 4), AutomatonEntity.MAX_TENSION);
					occupant.putFloat("tension", tension);
					if (tension == AutomatonEntity.MAX_TENSION) {
						this.eject();
					}

				} catch (Exception e) {
					System.out.println("Oops:" + e.toString());
				}
			}
		}

		super.tick();
	}



	public float calculateStressApplied() {
		return 4;
	}


	public void flap(boolean inward) {
		if (!level.isClientSide) {

			AllPackets.channel.send(packetTarget(), new WinderFlapPacket( this, inward));
		} else {
			flap.setValue(inward ? 1 : -1);
			AllSoundEvents.FUNNEL_FLAP.playAt(level, worldPosition, 1, 1, true);
		}
	}

	public float getFlapOffset() {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof BeltFunnelBlock))
			return -1 / 16f;
		switch (blockState.getValue(BeltFunnelBlock.SHAPE)) {
			default:
			case RETRACTED:
				return 0;
			case EXTENDED:
				return 8 / 16f;
			case PULLING:
			case PUSHING:
				return -2 / 16f;
		}
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		itemHandler.deserializeNBT(compound.getCompound("inventory"));
		super.read(compound, clientPacket);
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		compound.put("inventory",itemHandler.serializeNBT());
		super.write(compound, clientPacket);
	}


	public void enter(AutomatonEntity entity){
	 occupant = entity.serializeNBT();
	 entity.remove(Entity.RemovalReason.DISCARDED);
	 //System.out.println(occupant.toString());
	 flap(true);
	}
	public void eject(){
		System.out.println(occupant.toString());
		if(!occupant.isEmpty()) {
			AutomatonEntity entity = ModEntityTypes.AUTOMATON.get().create(level);
			entity.deserializeNBT(occupant);
			Direction dir = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
			BlockPos pos = this.getBlockPos().relative(dir.getOpposite());
			entity.setPos(pos.getX()+0.5f,pos.getY(), pos.getZ()+0.5f);
			level.addFreshEntity(entity);
			occupant = new CompoundTag();
			flap(false);
		}
	}

	public boolean canEnter(){
		return occupant.isEmpty();
	}

	public boolean hasFlap() {
		return true;
	}
	private LerpedFloat createChasingFlap() {
		return LerpedFloat.linear()
				.startWithValue(.25f)
				.chase(0, .05f, LerpedFloat.Chaser.EXP);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("block.cactuscrafts.automaton_winder");
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return null;//TODO add menu
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return lazyItemHandler.cast();
		}
		return super.getCapability(cap);
	}

	@Override
	public void onLoad() {
		super.onLoad();
		lazyItemHandler= LazyOptional.of(()-> itemHandler);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		lazyItemHandler.invalidate();
	}

	public void drops(){
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		for(int i = 0; i < itemHandler.getSlots(); i++){
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}

		Containers.dropContents(this.level, this.worldPosition, inventory);
	}
}

