package nl.brandonvdongen.cactuscrafts.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Team;
import net.minecraftforge.event.ForgeEventFactory;
import nl.brandonvdongen.cactuscrafts.CactusCrafts;
import nl.brandonvdongen.cactuscrafts.entity.ai.goal.automaton.FindWinderGoal;
import nl.brandonvdongen.cactuscrafts.entity.ai.goal.automaton.UnpoweredGoal;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class AutomatonEntity extends TamableAnimal implements IAnimatable {


    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public static final float MAX_TENSION = 10000;
    private static final EntityDataAccessor<Float> TENSION = SynchedEntityData.defineId(AutomatonEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> SITTING = SynchedEntityData.defineId(AutomatonEntity.class, EntityDataSerializers.BOOLEAN);

    public AutomatonEntity(EntityType<? extends TamableAnimal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);

    }

    public static AttributeSupplier setAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
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
        this.goalSelector.addGoal(prio++, new FindWinderGoal(this, 1, 10));
        this.goalSelector.addGoal(prio++, new PanicGoal(this, 1.250));
        this.goalSelector.addGoal(prio++, new FollowOwnerGoal(this, 2, 10, 2, false));
        this.goalSelector.addGoal(prio++, new LookAtPlayerGoal(this, Player.class, 8f));
        this.goalSelector.addGoal(prio++, new WaterAvoidingRandomStrollGoal(this, 1));
        //this.goalSelector.addGoal(prio++, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimationData data) {
        AnimationController controller = new AnimationController(this, "controller",2, this::predicate);
        data.addAnimationController(controller);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(this.isSitting()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.automaton.sit", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        if(this.getTension() <= 0){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.automaton.unpowered", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }
        //System.out.println(this.isSitting());
        if (event.isMoving()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.automaton.walk", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.automaton.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;


    }

    @Override
    public void tick() {
        super.tick();
        if(getTension() > 0 && !isSitting())
        setTension(getTension()-1);
    }

    public void setTension(float tension){
        this.entityData.set(TENSION, tension);
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
        if(hand == InteractionHand.MAIN_HAND) {
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
                    setSitting(!isSitting());
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


    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
