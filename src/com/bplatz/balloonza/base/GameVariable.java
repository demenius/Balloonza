package com.bplatz.balloonza.base;

public class GameVariable
{
	public float progress_extra_speed = 0;
	
	public float balloon_extra_speed = 0;
	public float skull_extra_speed = 0;
	public float bonus_extra_speed = 0;
	public float balloon_extra_spawn = 0;
	public float skull_extra_spawn = 0;
	public float bonus_extra_spawn = 0;
	public boolean skull_mod_active = false;
	
	public void reset()
	{
		progress_extra_speed = 0;
		
		balloon_extra_speed = 0;
		skull_extra_speed = 0;
		bonus_extra_speed = 0;
		balloon_extra_spawn = 0;
		skull_extra_spawn = 0;
		bonus_extra_spawn = 0;
		skull_mod_active = false;
	}
}
