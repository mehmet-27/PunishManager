package com.mehmet_27.punishmanager.bukkit.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.mehmet_27.punishmanager.bukkit.utils.Paginator;
import com.mehmet_27.punishmanager.utils.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class Components {

    public static UIComponent getBackComponent(UIFrame parent, int slot, Player viewer) {
        UIComponent back = new UIComponentImpl.Builder(XMaterial.ARROW)
                .name(Messages.GUI_BACKBUTTON_NAME.getString(viewer.getName()))
                .slot(slot)
                .build();
        back.setListener(ClickType.LEFT, () -> InventoryDrawer.open(parent));
        return back;
    }

    public static UIComponent getAirComponent(int slot) {
        return new UIComponentImpl.Builder(XMaterial.AIR)
                .name("")
                .slot(slot)
                .build();
    }

    public static UIComponent getPreviousPageComponent(int slot, Runnable listener, Paginator paginator, Player viewer) {
        if (!paginator.hasPreviousPage()) {
            return getAirComponent(slot);
        }
        UIComponent c = new UIComponentImpl.Builder(XMaterial.FEATHER)
                .name(Messages.GUI_MANAGEPUNISHMENTS_PREVIOUS_NAME.getString(viewer.getName()))
                .slot(slot)
                .build();
        setOneTimeUseListener(c, listener);
        return c;
    }

    public static UIComponent getNextPageComponent(int slot, Runnable listener, Paginator paginator, Player viewer) {
        if (!paginator.hasNextPage()) {
            return getAirComponent(slot);
        }
        UIComponent c = new UIComponentImpl.Builder(XMaterial.FEATHER)
                .name(Messages.GUI_MANAGEPUNISHMENTS_NEXT_NAME.getString(viewer.getName()))
                .slot(slot)
                .build();
        setOneTimeUseListener(c, listener);
        return c;
    }

    private static void setOneTimeUseListener(UIComponent c, Runnable listener) {
        c.setListener(ClickType.LEFT, () -> {
            if (listener != null) {
                listener.run();
            }
            c.setListener(ClickType.LEFT, null);
        });
    }
}
