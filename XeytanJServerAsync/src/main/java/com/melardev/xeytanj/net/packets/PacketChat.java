package com.melardev.xeytanj.net.packets;

public class PacketChat extends Packet {

    public enum ChatInstruction {
        START, STOP, MESSAGE
    }

    private ChatInstruction chatInstruction;
    private String message;

    public PacketChat(ChatInstruction chatInstruction) {
        super(PacketType.CHAT);
        this.chatInstruction = chatInstruction;
    }


    public PacketChat(String msg) {
        this(ChatInstruction.MESSAGE);
        message = msg;
    }

    public ChatInstruction getChatInstruction() {
        return chatInstruction;
    }

    public void setChatInstruction(ChatInstruction chatInstruction) {
        this.chatInstruction = chatInstruction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
