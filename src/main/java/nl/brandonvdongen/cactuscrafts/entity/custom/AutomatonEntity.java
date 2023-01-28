package nl.brandonvdongen.cactuscrafts.entity.custom;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.blocks.ModBlocks;
import nl.brandonvdongen.cactuscrafts.entity.ai.goal.automaton.FindWinderGoal;
import nl.brandonvdongen.cactuscrafts.entity.ai.goal.automaton.UnpoweredGoal;
import nl.brandonvdongen.cactuscrafts.helpers.SmoothedFloat;
import nl.brandonvdongen.cactuscrafts.item.ModItems;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import static net.minecraft.util.Mth.lerp;

public class AutomatonEntity extends TamableAnimal implements IAnimatable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public static final String TENSION_NBT_KEY = CactusCrafts.MOD_ID+":Tension";
    public static final String ITEM_NBT_KEY = CactusCrafts.MOD_ID+":HeldItem";

    public static final float MAX_TENSION = 36000;
    public SmoothedFloat keyRotation = new SmoothedFloat(0,0.1f);
    public SmoothedFloat DialRotation = new SmoothedFloat(0,0.1f);
    private static final EntityDataAccessor<Float> TENSION = SynchedEntityData.defineId(AutomatonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(AutomatonEntity.class, EntityDataSerializers.BOOLEAN);

    public AutomatonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean attackable(){
        return false;
    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2)
                .add(Attributes.ATTACK_DAMAGE, 3f)
                .add(Attributes.ATTACK_SPEED, 2f)
                .add(Attributes.MOVEMENT_SPEED, 0.15f)
                .build();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    protected void defineSynchedData(){
        super.defineSynchedData();
        this.entityData.define(TENSION, MAX_TENSION);
        this.entityData.define(SITTING,false);
    }
    public void registerGoals() {
        int prio = 0;
        this.goalSelector.addGoal(prio++, new UnpoweredGoal(this));
        this.goalSelector.addGoal(prio++, new FloatGoal(this));
        this.goalSelector.addGoal(prio++, new FindWinderGoal(this, 1, 20));
        this.goalSelector.addGoal(prio++, new PanicGoal(this, 1.250));
        this.goalSelector.addGoal(prio++, new FollowOwnerGoal(this, 2, 4, 2, false));
        this.goalSelector.addGoal(prio++, new LookAtPlayerGoal(this, Player.class, 8f));
        this.goalSelector.addGoal(prio++, new WaterAvoidingRandomStrollGoal(this, 1));
        //this.goalSelector.addGoal(prio++, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimationData data) {
        AnimationController controller = new AnimationController(this, "controller",2, this::predicate);
        data.addAnimationController(controller);
    }

    private static final AnimationBuilder
            ANIMATION_IDLE = new AnimationBuilder().addAnimation("animation.automaton.idle", ILoopType.EDefaultLoopTypes.LOOP),
            ANIMATION_IDLE_ITEM = new AnimationBuilder().addAnimation("animation.automaton.idle.item",ILoopType.EDefaultLoopTypes.LOOP),
            ANIMATION_IDLE_BLOCK = new AnimationBuilder().addAnimation("animation.automaton.idle.block",ILoopType.EDefaultLoopTypes.LOOP),

            ANIMATION_WALK = new AnimationBuilder().addAnimation("animation.automaton.walk", ILoopType.EDefaultLoopTypes.LOOP),
            ANIMATION_WALK_ITEM = new AnimationBuilder().addAnimation("animation.automaton.walk.item",ILoopType.EDefaultLoopTypes.LOOP),
            ANIMATION_WALK_BLOCK = new AnimationBuilder().addAnimation("animation.automaton.walk.block", ILoopType.EDefaultLoopTypes.LOOP),

            ANIMATION_SIT = new AnimationBuilder().addAnimation("animation.automaton.sit",ILoopType.EDefaultLoopTypes.LOOP),
            ANIMATION_UNPOWERED = new AnimationBuilder().addAnimation("animation.automaton.unpowered",ILoopType.EDefaultLoopTypes.LOOP);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<E> controller = event.getController();
        controller.setAnimationSpeed(1);
        if(this.isSitting()){
            controller.setAnimation(ANIMATION_SIT);
            return PlayState.CONTINUE;
        }
        else if(this.getTension() <= 0){
            controller.setAnimation(ANIMATION_UNPOWERED);
            return PlayState.CONTINUE;
        }
        //System.out.println(this.isSitting());
        else if (event.isMoving()){
            controller.setAnimationSpeed(event.getLimbSwingAmount()*3);
            //System.out.println(event.getLimbSwingAmount());
            if(this.getMainHandItem().isEmpty())controller.setAnimation(ANIMATION_WALK);
            else if(this.getMainHandItem().getItem() instanceof BlockItem) controller.setAnimation(ANIMATION_WALK_BLOCK);
            else controller.setAnimation(ANIMATION_WALK_ITEM);
            return PlayState.CONTINUE;
        }
        else {
            if (this.getMainHandItem().isEmpty()) controller.setAnimation(ANIMATION_IDLE);
            else if (this.getMainHandItem().getItem() instanceof BlockItem) controller.setAnimation(ANIMATION_IDLE_BLOCK);
            else controller.setAnimation(ANIMATION_IDLE_ITEM);
            return PlayState.CONTINUE;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        if(!level.isClientSide) {
            setTension(getTension()-2000);
            if (source.isBypassInvul()) return super.hurt(source, pAmount);
            if (source.getEntity() instanceof Player) {
                if (((Player) source.getEntity()).getMainHandItem().sameItem(new ItemStack(AllItems.WRENCH.get()))) {
                    this.markHurt();
                    return super.hurt(source, 0);
                }
            }
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if(getTension() > 0 && !isSitting())
        setTension(getTension()-1);

        if(level.isClientSide) {
            DialRotation.setTarget((getTension()/MAX_TENSION)*((float)Math.PI/2));
            keyRotation.setTarget((getTension()/20f)*((float)Math.PI));
        }
    }

    public void setTension(float tension){
        this.entityData.set(TENSION, Math.max(tension,0));
    }

    public float getTension(){
        return this.entityData.get(TENSION);
    }

    public void setSitting(boolean sitting){
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }

    public boolean isSitting(){
        return this.entityData.get(SITTING);
    }

    @Override
    public Team getTeam() {
        return super.getTeam();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(hand == InteractionHand.MAIN_HAND && player.getMainHandItem().isEmpty()) {
            if(this.level.isClientSide){return InteractionResult.CONSUME;}
            else{
                if (player.isCrouching()) {
                    if (getTension() < MAX_TENSION) {
                        setTension(getTension() + 200);
                    }
                    if (getTension() > MAX_TENSION) {
                        setTension(MAX_TENSION);
                    }
                }else if(!isTame()){//not crouching
                    if(!ForgeEventFactory.onAnimalTame(this, player)){
                        super.tame(player);
                        this.navigation.recomputePath();
                        this.setTarget(null);
                        this.level.broadcastEntityEvent(this, (byte)7);
                        setSitting(true);
                    }
                }else{
                    if(this.getMainHandItem().isEmpty()) {
                        setSitting(!isSitting());
                    }else{
                        this.spawnAtLocation(this.getMainHandItem().copy());
                        this.getMainHandItem().setCount(0);
                    }
                }
            }
        }
        else if (hand == InteractionHand.MAIN_HAND && !player.getMainHandItem().isEmpty()){
            if(this.level.isClientSide){return InteractionResult.CONSUME;}
            if(player.getMainHandItem().is(AllItems.WRENCH.get())){

                CompoundTag nbt = this.getNbtData();
                ItemStack item = new ItemStack(ModItems.AUTOMATON_ITEM.get());
                item.setTag(nbt);
                item.setHoverName(this.getCustomName());
                this.spawnAtLocation(item);
                this.remove(RemovalReason.DISCARDED);
                return InteractionResult.SUCCESS;
            }
            if(this.getMainHandItem().isEmpty()){
                this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND,player.getMainHandItem().copy());
                player.getMainHandItem().setCount(0);
            }else{
                if(player.getMainHandItem().sameItem(this.getMainHandItem())){

                    int playerHeld = player.getMainHandItem().getCount();
                    int free = this.getMainHandItem().getMaxStackSize()-this.getMainHandItem().getCount();;

                    player.getMainHandItem().shrink(Math.min(playerHeld,free));
                    this.getMainHandItem().grow(Math.min(playerHeld,free));

                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag){
        super.readAdditionalSaveData(tag);
        setTension(tag.getFloat("tension"));
        setSitting(tag.getBoolean("sitting"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag){
        super.addAdditionalSaveData(tag);
        tag.putFloat("tension", this.getTension());
        tag.putBoolean("sitting", this.isSitting());
    }

    public CompoundTag getNbtData(){
        CompoundTag tag = new CompoundTag();
        tag.put(ITEM_NBT_KEY, this.getMainHandItem().serializeNBT());
        tag.putFloat(TENSION_NBT_KEY, getTension());
        return tag;
    }

    public void setNbtData(CompoundTag tag){
        setTension(tag.getFloat(TENSION_NBT_KEY));
        this.setItemInHand(InteractionHand.MAIN_HAND,ItemStack.of(tag.getCompound(ITEM_NBT_KEY)));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
