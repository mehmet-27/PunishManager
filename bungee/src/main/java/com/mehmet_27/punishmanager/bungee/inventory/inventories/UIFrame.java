package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UIFrame extends Inventory {

    private final UIFrame parent;
    private final ProxiedPlayer viewer;

    public UIFrame(@Nullable UIFrame parent, InventoryType type, @NotNull ProxiedPlayer viewer) {
        super(type);
        this.parent = parent;
        this.viewer = viewer;
    }

    public UIFrame getParent(){
        return parent;
    }
    public ProxiedPlayer getViewer(){
        return viewer;
    }
}
