package com.sweetrpg.crafttracker.common.advancement;

import com.google.common.collect.Maps;
import com.sweetrpg.crafttracker.common.util.Util;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CriterionTriggerBase<T extends CriterionTriggerBase.Instance> implements ICriterionTrigger<T> {
    private final ResourceLocation id;
    protected final Map<PlayerAdvancements, Set<ICriterionTrigger.Listener<T>>> listeners = Maps.newHashMap();

    public CriterionTriggerBase(String id) {
        this.id = Util.getResource(id);
    }

    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        Set<Listener<T>> playerListeners = (Set) this.listeners.computeIfAbsent(playerAdvancementsIn, (k) -> new HashSet());
        playerListeners.add(listener);
    }

    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<T> listener) {
        Set<Listener<T>> playerListeners = (Set) this.listeners.get(playerAdvancementsIn);
        if(playerListeners != null) {
            playerListeners.remove(listener);
            if(playerListeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }

    }

    public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    public ResourceLocation getId() {
        return this.id;
    }

    protected void trigger(ServerPlayerEntity player, @Nullable List<Supplier<Object>> suppliers) {
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        Set<Listener<T>> playerListeners = (Set) this.listeners.get(playerAdvancements);
        if(playerListeners != null) {
            List<Listener<T>> list = new LinkedList();

            for(Listener<T> listener : playerListeners) {
                if(((Instance) listener.getTriggerInstance()).test(suppliers)) {
                    list.add(listener);
                }
            }

            list.forEach((listenerx) -> listenerx.run(playerAdvancements));
        }

    }

    public abstract static class Instance extends CriterionInstance {
        public Instance(ResourceLocation idIn, EntityPredicate.AndPredicate p_i231464_2_) {
            super(idIn, p_i231464_2_);
        }

        protected abstract boolean test(@Nullable List<Supplier<Object>> var1);
    }
}
