package nl.brandonvdongen.cactuscrafts;

import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.curiosities.weapons.BuiltinPotatoProjectileTypes;
import com.simibubi.create.content.schematics.filtering.SchematicInstances;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.CreateRegistry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.brandonvdongen.cactuscrafts.blocks.ModBlocks;
import nl.brandonvdongen.cactuscrafts.blocks.entity.ModBlockEntities;
import nl.brandonvdongen.cactuscrafts.entity.ModEntityTypes;
import nl.brandonvdongen.cactuscrafts.entity.client.AutomatonRenderer;
import nl.brandonvdongen.cactuscrafts.item.ModItems;
import nl.brandonvdongen.cactuscrafts.networking.ModMessages;
import nl.brandonvdongen.create.blocks.BlockPartials;
import nl.brandonvdongen.create.index.CreateBlocks;
import nl.brandonvdongen.create.index.CreateTileEntities;
import nl.brandonvdongen.create.networking.AllPackets;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CactusCrafts.MOD_ID)
public class CactusCrafts
{
    // Directly reference a slf4j logger
    public static final String MOD_ID = "cactuscrafts";
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("removal")
    private static final NonNullSupplier<CreateRegistrate> registrate = CreateRegistrate.lazy(CactusCrafts.MOD_ID);

    public CactusCrafts()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        ModEntityTypes.register(eventBus);

        CreateBlocks.register();
        CreateTileEntities.register();
        BlockPartials.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CactusCrafts::init);
    }

    public static void init(final FMLCommonSetupEvent event) {
        AllPackets.registerPackets();
    }

    private void clientSetup(final FMLClientSetupEvent event){
        EntityRenderers.register(ModEntityTypes.AUTOMATON.get(), AutomatonRenderer::new);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

        event.enqueueWork(()-> {
            ModMessages.register();
        });
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static CreateRegistrate registrate() {
        return registrate.get();
    }
}
