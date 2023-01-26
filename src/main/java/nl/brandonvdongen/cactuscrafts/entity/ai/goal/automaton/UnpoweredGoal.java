package nl.brandonvdongen.cactuscrafts.entity.ai.goal.automaton;

import mcp.client.Start;
import net.minecraft.world.entity.ai.goal.Goal;
import nl.brandonvdongen.cactuscrafts.entity.custom.AutomatonEntity;

import java.util.EnumSet;

public class UnpoweredGoal extends Goal {
    private final AutomatonEntity mob;
    public UnpoweredGoal(AutomatonEntity entity) {
        this.mob = entity;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        return this.mob.getTension()<=0 || mob.isSitting();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getTension()<=0 || mob.isSitting();
    }

    public void start(){
        this.mob.getNavigation().stop();
    }
}