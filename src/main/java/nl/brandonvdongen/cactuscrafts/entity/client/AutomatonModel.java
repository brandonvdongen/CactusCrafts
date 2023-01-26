package nl.brandonvdongen.cactuscrafts.entity.client;

import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class AutomatonModel extends AnimatedGeoModel<AutomatonEntity> {

    IBone head = null;
    IBone dial = null;
    float dialRotation = 0;
    IBone key = null;
    float keyRotation = 0;

    public IBone getHead(){
        if(head == null){
            head = this.getAnimationProcessor().getBone("Head");
        }
        return head;
    }

    public IBone getDial(){
        if(dial == null){
            dial = this.getAnimationProcessor().getBone("Dial");
        }
        return dial;
    }
    public IBone getKey(){
        if(key == null){
            key = this.getAnimationProcessor().getBone("Key");
        }
        return key;
    }

    float lerp (float a, float b, float f) {
        return a + f * (b - a);
    }
    public float GetKeyRotation(AutomatonEntity entity){
        keyRotation = lerp(keyRotation, (entity.getTension()/20f)*((float)Math.PI),0.1f);
        return keyRotation;
    }
    public float GetDialRotation(AutomatonEntity entity){
        dialRotation = lerp(dialRotation, (entity.getTension()/entity.MAX_TENSION)*((float)Math.PI/2),0.1f);
        return dialRotation;
    }


    @Override
    public void setLivingAnimations(AutomatonEntity entity, Integer uniqueID, AnimationEvent customPredicate){
        super.setLivingAnimations(entity, uniqueID, customPredicate);


        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);
        int unpausedMultiplier = !Minecraft.getInstance().isPaused() || manager.shouldPlayWhilePaused ? 1 : 0;

        getHead().setRotationX(head.getRotationX() + extraData.headPitch * ((float) Math.PI / 180F) * unpausedMultiplier);
        getHead().setRotationY(head.getRotationY() + extraData.netHeadYaw * ((float) Math.PI / 180F) * unpausedMultiplier);
        getDial().setRotationZ(GetDialRotation(entity));
        getKey().setRotationZ(GetKeyRotation(entity));
        //System.out.println("Automaton("+entity.level.isClientSide+"):"+(entity.getTension()/entity.MAX_TENSION)*((float)Math.PI));
    }


    @Override
    public ResourceLocation getModelLocation(AutomatonEntity object) {
        return new ResourceLocation(CactusCrafts.MOD_ID, "geo/automaton.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(AutomatonEntity object) {
        return new ResourceLocation(CactusCrafts.MOD_ID, "textures/entity/automaton/automaton.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(AutomatonEntity animatable) {
        return new ResourceLocation(CactusCrafts.MOD_ID, "animations/automaton.animation.json");
    }
}
