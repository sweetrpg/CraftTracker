package com.sweetrpg.crafttracker.common.advancement;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
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

    public void trigger(Entity player) {
        super.trigger(player, (List) null);
    }

    public Instance instance() {
        return new Instance(this.getId());
    }

    public static class Instance extends CriterionTriggerBase.Instance {
        public Instance(ResourceLocation idIn) {
            super(idIn, Composite.ANY);
        }

        protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
            return true;
        }
    }
}
