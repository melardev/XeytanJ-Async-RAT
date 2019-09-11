package com.melardev.xeytanj.services;

import com.melardev.xeytanj.IApplication;
import com.melardev.xeytanj.enums.SessionType;
import com.melardev.xeytanj.gui.chat.ChatGUI;
import com.melardev.xeytanj.logger.ILogger;
import com.melardev.xeytanj.net.packets.PacketChat;
import com.melardev.xeytanj.remote.PacketHandler;

public class ChatHandler implements PacketHandler<PacketChat> {
    private ChatGUI gui;
    private IApplication application;
    private ILogger logger;

    public ChatHandler(IApplication application) {
        this.application = application;
    }

    public void sendMessage(String msg) {
        logger.traceCurrentMethodName();
        PacketChat packet = new PacketChat(msg);
        application.sendPacket(packet);
    }

    public void onGuiDispose() {
        logger.traceCurrentMethodName();
        application.onSessionClosed(SessionType.CHAT_SERVICE);
        application.sendPacket(new PacketChat(PacketChat.ChatInstruction.STOP));
    }

    @Override
    public void handlePacket(PacketChat packet) {
        logger.trace("Handling " + packet.getChatInstruction());
        if (packet.getChatInstruction() == PacketChat.ChatInstruction.START) {
            logger.traceCurrentMethodName();

        }
        if (packet.getChatInstruction() == PacketChat.ChatInstruction.STOP) {
            if (gui == null)
                return;
            gui.setVisible(false);
            gui.dispose();
            gui = null;
            application.onSessionClosed(SessionType.CHAT_SERVICE);
        } else {

            if (gui == null)
                gui = new ChatGUI(this);

            gui.setVisible(true);

            if (packet.getChatInstruction() == PacketChat.ChatInstruction.MESSAGE)
                gui.appendMsg("Master: " + packet.getMessage() + "\n");
        }
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
}
