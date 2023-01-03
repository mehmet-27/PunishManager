package dev.mehmet27.punishmanager.bungee.inventory;

import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;

import java.util.List;

public class UIComponentImpl extends UIComponent {

    private ItemStack item;
    private int slot;

    private UIComponentImpl() {
        item = new ItemStack(ItemType.STONE);
        slot = 0;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    public static class Builder {
        private final UIComponentImpl component = new UIComponentImpl();
        private List<String> lore;

        public Builder(ItemType material) {
            component.item = new ItemStack(material);
        }

        public Builder name(String displayName) {
            component.getItem().displayName(displayName);
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public Builder slot(int slot) {
            component.slot = slot;
            return this;
        }

        public UIComponent build() {
            if (lore != null) {
                component.getItem().lore(lore, true);
            }
            return component;
        }
    }
}
