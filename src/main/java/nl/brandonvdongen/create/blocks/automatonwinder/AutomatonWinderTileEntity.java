package nl.brandonvdongen.create.blocks.automatonwinder;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.logistics.block.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.block.funnel.FunnelTileEntity;
import nl.brandonvdongen.create.networking.AllPackets;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import nl.brandonvdongen.cactuscrafts.networking.packet.WinderFlapPacket;

public class AutomatonWinderTileEntity extends KineticTileEntity {

	LerpedFloat flap;
	public static float RPS;

	public AutomatonWinderTileEntity(BlockEntityType<? extends AutomatonWinderTileEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		flap = createChasingFlap();
	}

	@Override
	public void tick() {
		flap.tickChaser();
		super.tick();
	}

	public float RPS(){
		return this.speed;
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

	public boolean hasFlap() {
		return true;
	}
	private LerpedFloat createChasingFlap() {
		return LerpedFloat.linear()
				.startWithValue(.25f)
				.chase(0, .05f, LerpedFloat.Chaser.EXP);
	}
}

