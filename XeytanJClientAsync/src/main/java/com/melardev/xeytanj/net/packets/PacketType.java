package com.melardev.xeytanj.net.packets;

public enum PacketType {
    LOGIN,
    PRESENTATION,

    /**
     * * * * * For the server:
     * - PacketType.CAMERA is issued to play/pause/stop
     * - PacketType.CAMERA_CONFIG is issued to request the cameras info available on the client
     * * * * * For the client
     * - PacketType.CAMERA is issued as response containing the picture taken
     * - PacketType.CAMERA_CONFIG is issued to send the cameras info
     */

    CAMERA_CONFIG,
    CAMERA,

    DESKTOP_CONFIG,
    DESKTOP,
    KEYLOGGER,
    FILE_EXPLORER,
    PROCESS,
    CHAT,
    SHELL,
    VOICE,
    FILE,
    TROLL,
    CLOSE,
    UNINSTALL
}
