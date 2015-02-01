package com.bplatz.balloonza.base;

import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.SceneManager.SceneType;
import com.bplatz.balloonza.object.Balloon.BalloonType;
import com.bplatz.balloonza.scene.LogicScene;

public abstract class BaseHandler
{
	protected LogicScene scene;
	protected ResourceManager resourceManager;
	
	public BaseHandler(LogicScene scene)
	{
		this.scene = scene;
		resourceManager = ResourceManager.getInstance();
	}

	public abstract void create();
	public abstract void disposeScene();

	public abstract void pause();
	public abstract void unload();
	public abstract void load();

	public abstract void addToScore(int i, BalloonType type, boolean poppedByBomb);
	public abstract void activateFreeze();
	public abstract void activateX2();
	public abstract void onBackKeyPressed();
	public abstract boolean started();
	public abstract SceneType getSceneType();
}
