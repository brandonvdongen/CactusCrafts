package nl.brandonvdongen.cactuscrafts.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, CactusCrafts.MOD_ID);

    public static final RegistryObject<EntityType<AutomatonEntity>> AUTOMATON = ENTITY_TYPES.register("automaton",
            ()-> EntityType.Builder.of(AutomatonEntity::new, MobCategory.CREATURE)
            .sized(1f,1f).build(new ResourceLocation(CactusCrafts.MOD_ID, "automaton").toString()));


    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }

}
