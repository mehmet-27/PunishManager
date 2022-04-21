package com.mehmet_27.punishmanager.bungee.inventory;

import dev.simplix.protocolize.api.ClickType;
import dev.simplix.protocolize.api.item.ItemStack;

import java.util.HashMap;

public abstract class UIComponent {

    private final HashMap<ClickType, Runnable> listeners = new HashMap<>();
    private final HashMap<ClickType, String> permissions = new HashMap<>();

    public abstract ItemStack getItem();

    public abstract int getSlot();

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
}
