package com.bplatz.balloonza.scene;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.manager.SceneManager.SceneType;

public class LoadingScene extends BaseScene implements IOnSceneTouchListener
{
	BaseSprite background;
	
	private void createBackground()
	{
		background = new BaseSprite(240, 400, resourceManager.loading_background_region, vbom);

		attachChild(background);
	}
	
	@Override
	public void createScene()
	{
		createBackground();
		attachChild(new Text(100, 30, resourceManager.medium_font, "Loading...", vbom));
		this.setOnSceneTouchListener(this);
	}

	@Override
	public void onBackKeyPressed()
	{
		return;
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disponeScene()
	{
		background.detachSelf();
		background.dispose();
		background = null;
		
		this.detachSelf();
		this.dispose();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{		
		return false;
	}
	
}
