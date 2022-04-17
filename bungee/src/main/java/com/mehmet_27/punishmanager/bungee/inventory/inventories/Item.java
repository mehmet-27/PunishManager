package com.mehmet_27.punishmanager.bungee.inventory.inventories;

import com.mehmet_27.punishmanager.utils.Messages;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;

public class Item {

    public ItemStack back(String player){
        ItemStack itemStack = new ItemStack(ItemType.ARROW);
        itemStack.displayName(Messages.GUI_BACKBUTTON_NAME.getString(player));
        return itemStack;
    }
}
