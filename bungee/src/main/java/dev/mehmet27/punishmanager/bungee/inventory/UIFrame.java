package dev.mehmet27.punishmanager.bungee.inventory;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class UIFrame {
    private final UIFrame parent;
    private final ProxiedPlayer viewer;
    private final Set<UIComponent> components = ConcurrentHashMap.newKeySet();

    public UIFrame(UIFrame parent, ProxiedPlayer viewer) {
        this.parent = parent;
        this.viewer = viewer;
    }

    public abstract String getTitle();

    public ProxiedPlayer getViewer() {
        return viewer;
    }

    public UIFrame getParent() {
        return parent;
    }

    public abstract int getSize();

    public abstract void createComponents();

    public UIComponent getComponent(int slot) {
        for (UIComponent component : getComponents()) {
            if (component.getSlot() == slot) {
                return component;
            }
        }
        return null;
    }

    public void add(UIComponent component) {
        components.add(component);
    }

    public void clear() {
        components.clear();
    }

    public Set<UIComponent> getComponents() {
        return components;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof UIFrame) {
            UIFrame otherFrame = (UIFrame) other;
            return getSize() == otherFrame.getSize() && getTitle().equals(otherFrame.getTitle());
        }
        return false;
    }
}
