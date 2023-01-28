package nl.brandonvdongen.cactuscrafts.event;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.ModEntityTypes;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;

@Mod.EventBusSubscriber(modid = CactusCrafts.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void entityAttributeEvents(EntityAttributeCreationEvent event){
        event.put(ModEntityTypes.AUTOMATON.get(), AutomatonEntity.setAttributes());
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        //event.registerBlockEntityRenderer(ModBlockEntities.AUTO_WINDER_ENTITY.get(), AutoWinderRenderer::new);
    }
}
