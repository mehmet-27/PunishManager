package com.mehmet_27.punishmanager.objects;

import com.mehmet_27.punishmanager.PunishManager;
import com.mehmet_27.punishmanager.managers.MessageManager;

public class Reason {
    private final String reason;
    public Reason (String context, String playerName){
        if (context != null){
            this.reason = context;
        }else{
            MessageManager messageManager = PunishManager.getInstance().getMessageManager();
            this.reason = messageManager.getMessage("main.defaultReason", playerName);
        }
    }
    public String getReason(){
        return reason;
    }
}
