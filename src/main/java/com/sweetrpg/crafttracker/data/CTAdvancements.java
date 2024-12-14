package com.sweetrpg.crafttracker.data;

import com.google.common.collect.Maps;
import net.java.games.input.Component;
import net.minecraft.advancements.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class CTAdvancements implements Consumer<Consumer<Advancement>> {

    @Override
    public void accept(Consumer<Advancement> register) {
//        Advancement advancement = Advancement.Builder.advancement().display(ModItems.TRAINING_TREAT.get(), new TranslatableComponent("advancements.cat.root.title"), new TranslatableComponent("advancements.cat.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, false, false, false).addCriterion("crafting_table", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.CRAFTING_TABLE)).save(register, Util.getResourcePath("cat/find_cat"));
    }

    public static class Builder {
        private ResourceLocation parentId;
        private Advancement parent;
        private DisplayInfo display;
        private AdvancementRewards rewards = AdvancementRewards.EMPTY;
        private Map<String, Criterion> criteria = Maps.newLinkedHashMap();
        private String[][] requirements;
        private IRequirementsStrategy requirementsStrategy = IRequirementsStrategy.AND;

        private Builder(@Nullable ResourceLocation parentIdIn, @Nullable DisplayInfo displayIn, AdvancementRewards rewardsIn, Map<String, Criterion> criteriaIn, String[][] requirementsIn) {
           this.parentId = parentIdIn;
           this.display = displayIn;
           this.rewards = rewardsIn;
           this.criteria = criteriaIn;
           this.requirements = requirementsIn;
        }

        private Builder() {
        }

        public static CTAdvancements.Builder builder() {
           return new CTAdvancements.Builder();
        }

        public CTAdvancements.Builder withParent(Advancement parentIn) {
           this.parent = parentIn;
           return this;
        }

        public CTAdvancements.Builder withParentId(ResourceLocation parentIdIn) {
           this.parentId = parentIdIn;
           return this;
        }

        public CTAdvancements.Builder withDisplay(ItemStack stack, ITextComponent title, ITextComponent description, @Nullable ResourceLocation background, FrameType frame, boolean showToast, boolean announceToChat, boolean hidden) {
           return this.withDisplay(new DisplayInfo(stack, title, description, background, frame, showToast, announceToChat, hidden));
        }

        public CTAdvancements.Builder withDisplay(DisplayInfo displayIn) {
           this.display = displayIn;
           return this;
        }

        public CTAdvancements.Builder withRewards(AdvancementRewards.Builder rewardsBuilder) {
           return this.withRewards(rewardsBuilder.build());
        }

        public CTAdvancements.Builder withRewards(AdvancementRewards rewards) {
           this.rewards = rewards;
           return this;
        }

        /**
         * Adds a criterion to the list of criteria
         */
        public CTAdvancements.Builder withCriterion(String key, ICriterionInstance criterionIn) {
           return this.withCriterion(key, new Criterion(criterionIn));
        }

        /**
         * Adds a criterion to the list of criteria
         */
        public CTAdvancements.Builder withCriterion(String key, Criterion criterionIn) {
           if (this.criteria.containsKey(key)) {
              throw new IllegalArgumentException("Duplicate criterion " + key);
           } else {
              this.criteria.put(key, criterionIn);
              return this;
           }
        }

        public CTAdvancements.Builder withRequirementsStrategy(IRequirementsStrategy strategy) {
           this.requirementsStrategy = strategy;
           return this;
        }

        /**
         * Tries to resolve the parent of this advancement, if possible. Returns true on success.
         */
        public boolean resolveParent(Function<ResourceLocation, Advancement> lookup) {
           if (this.parentId == null) {
              return true;
           } else {
              if (this.parent == null) {
                 this.parent = lookup.apply(this.parentId);
              }

              return this.parent != null;
           }
        }

        public Advancement build(ResourceLocation id) {
           if (!this.resolveParent((parentID) -> {
              return null;
           })) {
              throw new IllegalStateException("Tried to build incomplete advancement!");
           } else {
              if (this.requirements == null) {
                 this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
              }

              return new Advancement(id, this.parent, this.display, this.rewards, this.criteria, this.requirements);
           }
        }

        public Advancement register(Consumer<Advancement> consumer, String id) {
            Advancement advancement = this.build(new ResourceLocation(id));
            consumer.accept(advancement);
            return advancement;
         }
    }
}
