package com.bplatz.balloonza.base;

public class GameSettings
{
	public String default_name = null;
	
	public boolean sound_muted = false;
	public boolean music_muted = false;
	public boolean vibrate_disabled = false;
	
	public boolean guest_play = false;
	
	public boolean game_started = false;

	public enum GameOption
	{
		COLOR_BLIND, NORMAL
	}
	
	public GameOption gameOption = GameOption.NORMAL;
}
