package com.sweetrpg.crafttracker.common.advancement;

import com.google.gson.JsonObject;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SimpleTrigger extends CriterionTriggerBase<SimpleTrigger.Instance> implements ITriggerable {

    public static final String MAIN_CRITERION = "main";

    public SimpleTrigger(String id) {
        super(id);
    }

    public Instance createInstance(JsonObject json, ConditionArrayParser context) {
        return new Instance(this.getId());
    }

    public void trigger(ServerPlayerEntity player) {
        super.trigger(player, (List) null);
    }

    public Instance instance() {
        return new Instance(this.getId());
    }

    public static class Instance extends CriterionTriggerBase.Instance {
        public Instance(ResourceLocation idIn) {
            super(idIn, EntityPredicate.AndPredicate.ANY);
        }

        protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
            return true;
        }
    }
}
