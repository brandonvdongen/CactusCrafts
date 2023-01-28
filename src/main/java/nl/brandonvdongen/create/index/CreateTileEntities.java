package nl.brandonvdongen.create.index;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.create.blocks.automatonwinder.AutomatonWinderInstance;
import nl.brandonvdongen.create.blocks.automatonwinder.AutomatonWinderRenderer;
import nl.brandonvdongen.create.blocks.automatonwinder.AutomatonWinderTileEntity;

public class CreateTileEntities {

	public static final BlockEntityEntry<AutomatonWinderTileEntity> AUTOMATON_WINDER = CactusCrafts.registrate()
			.tileEntity("automaton_winder", AutomatonWinderTileEntity::new)
			.instance(() -> AutomatonWinderInstance::new)
			.validBlocks(CreateBlocks.AUTOMATON_WINDER)
			.renderer(() -> AutomatonWinderRenderer::new)
			.register();
	
	public static void register() {}
}
