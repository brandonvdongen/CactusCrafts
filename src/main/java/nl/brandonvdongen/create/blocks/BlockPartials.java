package nl.brandonvdongen.create.blocks;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.Create;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;

public class BlockPartials {
    public static final PartialModel AUTOMATON_WINDER_SHAFT = block("automaton_winder/block_shaft_input");

    private static PartialModel block(String path) {
        return new PartialModel(CactusCrafts.asResource("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}
