package nl.brandonvdongen.cactuscrafts.entity.ai.goal.automaton;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;
import nl.brandonvdongen.create.blocks.automatonwinder.AutomatonWinderTileEntity;
import nl.brandonvdongen.create.index.CreateBlocks;

import java.util.EnumSet;

public class FindWinderGoal extends MoveToBlockGoal {
    private final AutomatonEntity mob;

    public FindWinderGoal(AutomatonEntity mob, double speedModifier, int searchRange) {
        super(mob, speedModifier, searchRange);
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if(!(this.mob.getTension() < AutomatonEntity.MAX_TENSION * 0.2f))return false;
        return this.findNearestBlock();
    }

    @Override
    public boolean canContinueToUse() {
        return this.isValidTarget(this.mob.level, this.blockPos) && this.mob.getTension() < AutomatonEntity.MAX_TENSION * 0.99f;
    }

    @Override
    protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos) {
        if(pPos == null)return false;

        BlockEntity be = pLevel.getBlockEntity(pPos);
        boolean canEnter = false;
        if(be instanceof AutomatonWinderTileEntity) {
            AutomatonWinderTileEntity entity = (AutomatonWinderTileEntity) pLevel.getBlockEntity(pPos);
            canEnter = entity.canEnter();
        }
        return canEnter;
    }

    @Override
    public double acceptedDistance(){
        return 2D;
    }

    @Override
    public void tick() {
        super.tick();
        if(!mob.level.isClientSide) {
            if (isReachedTarget()) {
                BlockEntity blockEntity = mob.level.getBlockEntity(this.blockPos);
                if(blockEntity instanceof AutomatonWinderTileEntity) {
                    AutomatonWinderTileEntity winder = (AutomatonWinderTileEntity)mob.level.getBlockEntity(this.blockPos);
                        //mob.setTension(mob.getTension() + (Math.abs(winder.RPS()/8)));
                        if(winder.canEnter()) {
                         winder.enter(mob);
                        }
                        //System.out.println(mob.getTension());
                }
            }
        }
    }
}
