package com.sweetrpg.crafttracker.common.advancement;

import com.google.common.collect.Maps;
import com.sweetrpg.crafttracker.common.util.Util;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CriterionTriggerBase<T extends CriterionTriggerBase.Instance> implements CriterionTrigger<T> {
    private final ResourceLocation id;
    protected final Map<PlayerAdvancements, Set<Listener<T>>> listeners = Maps.newHashMap();

    public CriterionTriggerBase(String id) {
        this.id = Util.getResource(id);
    }

    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, CriterionTrigger.Listener<T> listener) {
        Set<CriterionTrigger.Listener<T>> playerListeners = (Set) this.listeners.computeIfAbsent(playerAdvancementsIn, (k) -> new HashSet());
        playerListeners.add(listener);
    }

    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, CriterionTrigger.Listener<T> listener) {
        Set<CriterionTrigger.Listener<T>> playerListeners = (Set) this.listeners.get(playerAdvancementsIn);
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

    protected void trigger(ServerPlayer player, @Nullable List<Supplier<Object>> suppliers) {
        PlayerAdvancements playerAdvancements = player.getAdvancements();
        Set<CriterionTrigger.Listener<T>> playerListeners = (Set) this.listeners.get(playerAdvancements);
        if(playerListeners != null) {
            List<CriterionTrigger.Listener<T>> list = new LinkedList();

            for(CriterionTrigger.Listener<T> listener : playerListeners) {
                if(((Instance) listener.getTriggerInstance()).test(suppliers)) {
                    list.add(listener);
                }
            }

            list.forEach((listenerx) -> listenerx.run(playerAdvancements));
        }

    }

    public abstract static class Instance extends AbstractCriterionTriggerInstance {
        public Instance(ResourceLocation idIn, ContextAwarePredicate p_i231464_2_) {
            super(idIn, p_i231464_2_);
        }

        protected abstract boolean test(@Nullable List<Supplier<Object>> var1);
    }
}
