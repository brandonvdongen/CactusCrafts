package nl.brandonvdongen.create.blocks.automatonwinder;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;

public class AutomatonWinderTileEntity extends KineticTileEntity {

	public static float RPS;

	public AutomatonWinderTileEntity(BlockEntityType<? extends AutomatonWinderTileEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void tick() {
		super.tick();
	}

	public float RPS(){
		return this.speed;
	}

	public float calculateStressApplied() {
		return 4;
	}
}

