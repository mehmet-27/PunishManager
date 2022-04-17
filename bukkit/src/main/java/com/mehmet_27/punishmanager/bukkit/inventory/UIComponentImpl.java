package com.mehmet_27.punishmanager.bukkit.inventory;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class UIComponentImpl extends UIComponent{

    private ItemStack item;
    private int slot;

    private UIComponentImpl() {
        item = new ItemStack(Material.STONE);
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

        public Builder(XMaterial material) {
            this(material.parseItem());
        }

        public Builder(Material material) {
            component.item = new ItemStack(material);
        }

        public Builder(ItemStack item) {
            if (item == null) {
                item = new ItemStack(Material.STONE);
            }
            component.item = item;
        }

        public Builder name(String displayName) {
            ItemMeta itemMeta = component.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(displayName);
                component.setItemMeta(itemMeta);
            }
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
            ItemMeta itemMeta = component.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setLore(lore);
                component.setItemMeta(itemMeta);
            }
            return component;
        }
    }
}
