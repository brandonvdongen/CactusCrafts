package nl.brandonvdongen.create.blocks.automatonwinder;

import com.jozufozu.flywheel.api.InstanceData;
import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.content.contraptions.relays.encased.ShaftInstance;
import com.simibubi.create.content.logistics.block.flap.FlapData;
import com.simibubi.create.content.logistics.block.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.block.funnel.FunnelTileEntity;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import nl.brandonvdongen.cactuscrafts.helpers.SmoothedFloat;
import nl.brandonvdongen.create.blocks.BlockPartials;

import java.util.ArrayList;

public class AutomatonWinderInstance extends HalfShaftInstance implements DynamicInstance {

    private final ArrayList<FlapData> flaps;
    private RotatingData shaft;

    AutomatonWinderTileEntity entity;

    public AutomatonWinderInstance(MaterialManager modelManager, AutomatonWinderTileEntity tile) {
        super(modelManager, tile);
            flaps = new ArrayList<>(4);
            entity = tile;

            if(tile.flap != null){
            PartialModel flapPartial = AllBlockPartials.BELT_FUNNEL_FLAP;
            Instancer<FlapData> model = modelManager.defaultSolid()
                    .material(AllMaterialSpecs.FLAPS)
                    .getModel(flapPartial, blockState);

            int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
            int skyLight = world.getBrightness(LightLayer.SKY, pos);

            Direction direction = FunnelBlock.getFunnelFacing(blockState);

            float flapness = tile.flap.getValue(AnimationTickHolder.getPartialTicks());
            float horizontalAngle = getShaftDirection().toYRot();

            for (int segment = 0; segment <= 3; segment++) {
                float intensity = segment == 3 ? 1.5f : segment + 1;
                float segmentOffset = -3 / 16f * segment;

                FlapData key = model.createInstance();

                key.setPosition(getInstancePosition())
                        .setSegmentOffset(segmentOffset, 0.3f, -0.65f)
                        .setBlockLight(blockLight)
                        .setSkyLight(skyLight)
                        .setHorizontalAngle(horizontalAngle)
                        .setFlapness(flapness)
                        .setFlapScale(-1)
                        .setPivotVoxelSpace(0, 10, 9.5f)
                        .setIntensity(intensity);

                flaps.add(key);
            }
        }
    }


    /*

    protected Instancer<RotatingData> getModel() {
        Direction dir = getShaftDirection();
        return getRotatingMaterial().getModel(BlockPartials.AUTOMATON_WINDER_SHAFT, blockState, dir);
    }
 */
    protected Direction getShaftDirection() {
        return blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }


    @Override
    public void beginFrame() {
        if (flaps == null) return;

        float flapness = entity.flap.getValue(AnimationTickHolder.getPartialTicks());

        for (FlapData flap : flaps) {
            flap.setFlapness(flapness);
        }
    }

    @Override
    public void updateLight() {
        super.updateLight();
        if (flaps != null)
            relight(pos, flaps.stream());
    }

    @Override
    public void remove() {
        super.remove();
        if (flaps == null) return;

        flaps.forEach(InstanceData::delete);
    }
}