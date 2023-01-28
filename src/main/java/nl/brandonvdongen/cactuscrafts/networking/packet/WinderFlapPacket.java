package nl.brandonvdongen.cactuscrafts.networking.packet;

import com.simibubi.create.foundation.networking.TileEntityDataPacket;

import net.minecraft.network.FriendlyByteBuf;
import nl.brandonvdongen.create.blocks.automatonwinder.AutomatonWinderTileEntity;

public class WinderFlapPacket extends TileEntityDataPacket<AutomatonWinderTileEntity> {

    private final boolean inwards;

    public WinderFlapPacket(FriendlyByteBuf buffer) {
        super(buffer);

        inwards = buffer.readBoolean();
    }

    public WinderFlapPacket(AutomatonWinderTileEntity tile, boolean inwards) {
        super(tile.getBlockPos());
        this.inwards = inwards;
    }

    @Override
    protected void writeData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(inwards);
    }

    @Override
    protected void handlePacket(AutomatonWinderTileEntity tile) {
        tile.flap(inwards);
    }
}
