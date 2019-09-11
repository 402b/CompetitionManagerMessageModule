package com.github.b402.cmc.message;

import com.github.b402.cmc.core.module.Module;

import org.apache.log4j.Logger;

public class Main extends Module {
    public void onLoad() {

    }

    public void onEnable() {
        Message.checkTable();
        Logger.getLogger(Main.class).info("信息模块加载完成");
    }

    public void onDisable() {

    }
}
