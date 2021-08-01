package com.mehmet_27.punishmanager;

import com.mehmet_27.punishmanager.managers.MessageManager;

public class Reason {
    private final String reason;
    public Reason (String context){
        if (context != null){
            this.reason = context;
        }else{
            MessageManager messageManager = PunishManager.getInstance().getMessageManager();
            this.reason = messageManager.getMessage("main.defaultReason");
        }
    }
    public String getReason(){
        return reason;
    }
}
