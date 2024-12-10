package com.sweetrpg.crafttracker.common.advancement;

import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SimpleTrigger extends CriterionTriggerBase<SimpleTrigger.Instance> {

    public static final String MAIN_CRITERION = "main";

    public SimpleTrigger(String id) {
        super(id);
    }

    public Instance createInstance(JsonObject json, DeserializationContext context) {
        return new Instance(this.getId());
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, (List) null);
    }

    public Instance instance() {
        return new Instance(this.getId());
    }

    public static class Instance extends CriterionTriggerBase.Instance {
        public Instance(ResourceLocation idIn) {
            super(idIn, ContextAwarePredicate.create());
        }

        protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
            return true;
        }
    }
}
