package nl.brandonvdongen.create.index;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.item.ModCreativeModeTab;
import nl.brandonvdongen.create.blocks.automatonwinder.AutomatonWinderBlock;

public class CreateBlocks {
	
	private static final CreateRegistrate REGISTRATE = CactusCrafts.registrate()
			.creativeModeTab(() -> ModCreativeModeTab.CACTUS_TAB);


	public static final BlockEntry<AutomatonWinderBlock> AUTOMATON_WINDER = REGISTRATE.block("automaton_winder", AutomatonWinderBlock::new)
			.initialProperties(SharedProperties::stone)
			.transform(BlockStressDefaults.setImpact(8))
			.tag(AllBlockTags.SAFE_NBT.tag) //Dono what this tag means (contraption safe?).
			.item()
			.transform(customItemModel())
			.register();

	
	public static void register() {

	}
}
