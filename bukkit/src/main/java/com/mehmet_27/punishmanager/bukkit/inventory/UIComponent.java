package com.mehmet_27.punishmanager.bukkit.inventory;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class UIComponent {

    private final HashMap<ClickType, Runnable> listeners = new HashMap<>();
    private final HashMap<ClickType, String> permissions = new HashMap<>();
    private final Set<ClickType> confirmationRequired = new HashSet<>();

    public abstract ItemStack getItem();

    public abstract int getSlot();

    public ItemMeta getItemMeta() {
        return getItem().getItemMeta();
    }

    public void setItemMeta(ItemMeta itemMeta) {
        getItem().setItemMeta(itemMeta);
    }

    public void setListener(ClickType click, Runnable listener) {
        listeners.put(click, listener);
    }

    public Runnable getListener(ClickType click) {
        return listeners.get(click);
    }

    public void setPermission(ClickType click, String permission) {
        permissions.put(click, permission);
    }

    public String getPermission(ClickType click) {
        return permissions.get(click);
    }

    public void setConfirmationRequired(ClickType click) {
        confirmationRequired.add(click);
    }

    public boolean isConfirmationRequired(ClickType click) {
        return confirmationRequired.contains(click);
    }
}
