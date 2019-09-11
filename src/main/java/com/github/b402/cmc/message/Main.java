package com.github.b402.cmc.message;

import com.github.b402.cmc.core.module.Module;
import com.github.b402.cmc.core.servlet.DataServlet;
import com.github.b402.cmc.message.service.MessageDataService;
import com.github.b402.cmc.message.service.ReadMessageService;
import com.github.b402.cmc.message.service.UnreadMessageService;

import org.apache.log4j.Logger;

public class Main extends Module {
    public void onLoad() {

    }

    public void onEnable() {
        Message.checkTable();
        DataServlet.register(new UnreadMessageService());
        DataServlet.register(new ReadMessageService());
        DataServlet.register(new MessageDataService());
        Logger.getLogger(Main.class).info("信息模块加载完成");
    }

    public void onDisable() {

    }
}
