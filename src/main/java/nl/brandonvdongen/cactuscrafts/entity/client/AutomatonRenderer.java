package nl.brandonvdongen.cactuscrafts.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ColorHandlerEvent;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class AutomatonRenderer extends ExtendedGeoEntityRenderer<AutomatonEntity> {
    public AutomatonRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AutomatonModel());
        this.shadowRadius = 0.25f;
    }

    @Override
    public ResourceLocation getTextureLocation(AutomatonEntity instance){
        return new ResourceLocation(CactusCrafts.MOD_ID, "textures/entity/automaton/automaton.png");
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {
        return false;
    }

    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String boneName, AutomatonEntity animatable) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String boneName, AutomatonEntity entity) {

        if(!(entity.getMainHandItem().getItem() instanceof BlockItem) && boneName.equals("HeldItem")){
            return entity.getMainHandItem();
        }
        else return null;
    }

    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack stack, String boneName) {
        return ItemTransforms.TransformType.GROUND;
    }
    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String boneName, AutomatonEntity entity) {
        if((entity.getMainHandItem().getItem() instanceof BlockItem) && boneName.equals("HeldBlock")){
            return ((BlockItem) entity.getMainHandItem().getItem()).getBlock().defaultBlockState();
        }
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack poseStack, ItemStack stack, String boneName, AutomatonEntity animatable, IBone bone) {

    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState state, String boneName, AutomatonEntity animatable) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack stack, String boneName, AutomatonEntity animatable, IBone bone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState state, String boneName, AutomatonEntity animatable) {

    }

    @Override
    public RenderType getRenderType(AutomatonEntity animatable, float partialTicks, PoseStack stack, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        stack.scale(1,1,1);
        return super.getRenderType(animatable, partialTicks, stack, renderTypeBuffer, vertexBuilder, packedLightIn, textureLocation);
    }
}
