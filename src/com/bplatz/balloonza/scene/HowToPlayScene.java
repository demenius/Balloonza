package com.bplatz.balloonza.scene;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.util.adt.color.Color;

import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.manager.SceneManager.SceneType;

public class HowToPlayScene extends BaseScene implements IOnMenuItemClickListener
{
	private MenuScene menuChildScene;
	private int tab = 0;
	
	private BaseSprite[] info;
	
	private IMenuItem prevMenuItem;
	private IMenuItem nextMenuItem;
	private IMenuItem gameMenuItem;

	@Override
	public void createScene()
	{
		info = new BaseSprite[6];
		
		float x = 240;
		
		for(int i = 0; i < 6; i++)
		{
			info[i] = new BaseSprite(x, 400, ResourceManager.getInstance().how_to_play_info_region[i], ResourceManager.getInstance().vbom);
			attachChild(info[i]);
			x+=480;
		}
		
		createMenuScene();
	}
	
	private void createMenuScene()
	{
		menuChildScene = new MenuScene(resourceManager.camera);
		menuChildScene.setPosition(0, 0);
		
		
		prevMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(Constant.MENU_PREV, resourceManager.large_font, "Prev", resourceManager.vbom), 1.025f, 1);
		nextMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(Constant.MENU_NEXT, resourceManager.large_font, "Next", resourceManager.vbom), 1.025f, 1);
		gameMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(Constant.MENU_GAME, resourceManager.large_font, "Menu", resourceManager.vbom), 1.025f, 1);
		
		prevMenuItem.setColor(Color.YELLOW);
		nextMenuItem.setColor(Color.YELLOW);
		gameMenuItem.setColor(Color.RED);
		prevMenuItem.setVisible(false);
		gameMenuItem.setVisible(false);
		menuChildScene.addMenuItem(prevMenuItem);
		menuChildScene.addMenuItem(nextMenuItem);
		menuChildScene.addMenuItem(gameMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		prevMenuItem.setPosition(60, 50);
		nextMenuItem.setPosition(420, 50);
		gameMenuItem.setPosition(420, 50);
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
	}

	@Override
	public void onBackKeyPressed()
	{
		tab = 0;
		prevMenuItem.setVisible(false);
		gameMenuItem.setVisible(false);
		nextMenuItem.setVisible(true);
		SceneManager.getInstance().unloadHowToPlayScene(resourceManager.engine);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_HOW_TO_PLAY;
	}

	@Override
	public void disponeScene()
	{
		for(int i = 0; i < 5; i++)
		{
			info[i].detachSelf();
			info[i].dispose();
			info[i] = null;
		}
		info = null;
		
		prevMenuItem.detachSelf();
		prevMenuItem.dispose();
		prevMenuItem = null;
		
		nextMenuItem.detachSelf();
		nextMenuItem.dispose();
		nextMenuItem = null;
		
		gameMenuItem.detachSelf();
		gameMenuItem.dispose();
		gameMenuItem = null;
		
		detachSelf();
		dispose();
	}

	//--------------------------------
		//TODO IOnMenuItemClickListener
		//--------------------------------
		@Override
		public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
		{
			switch(pMenuItem.getID())
			{
				case Constant.MENU_PREV:
					if(tab == 0)
						return false;
					ResourceManager.getInstance().button_click.play();
					tab--;
					((SmoothCamera)ResourceManager.getInstance().camera).setCenter(240 + tab*480, 400);
					if(tab == 0)
						prevMenuItem.setVisible(false);
					else if(tab == 4)
					{
						gameMenuItem.setVisible(false);
						nextMenuItem.setVisible(true);
					}
					return true;
				case Constant.MENU_NEXT:
					if(tab == 5)
						return false;
					ResourceManager.getInstance().button_click.play();
					tab++;
					((SmoothCamera)ResourceManager.getInstance().camera).setCenter(240 + tab*480, 400);
					if(tab == 5)
					{
						nextMenuItem.setVisible(false);
						gameMenuItem.setVisible(true);
					}
					else if(tab == 1)
						prevMenuItem.setVisible(true);
					return true;
				case Constant.MENU_GAME:
					if(tab != 5)
						return false;
					ResourceManager.getInstance().button_click.play();
					onBackKeyPressed();
				default:
					return false;
			}
		}
}
