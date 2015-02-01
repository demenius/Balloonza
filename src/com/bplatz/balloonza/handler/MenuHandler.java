package com.bplatz.balloonza.handler;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.math.MathUtils;

import android.content.Intent;
import android.net.Uri;

import com.bplatz.balloonza.base.BaseHandler;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.ResourceManager.BalloonPoolType;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.manager.SceneManager.SceneType;
import com.bplatz.balloonza.object.Balloon.BalloonType;
import com.bplatz.balloonza.scene.LogicScene;
import com.bplatz.ui.B2Dialog.B2DialogListener;

public class MenuHandler extends BaseHandler implements IOnMenuItemClickListener
{
	private TimerHandler menuBalloonTimer;
	private MenuScene menuChildScene;
	private Sprite title;

	private IMenuItem playMenuItem;
	private IMenuItem optionsMenuItem;
	private IMenuItem highscoreMenuItem;
	private IMenuItem rateUsMenuItem;
	private IMenuItem guestMenuItem;
	private IMenuItem howToPlayMenuItem;
	//private IMenuItem removeAdsMenuItem;
	//private IMenuItem loginMenuItem;
	

	public MenuHandler(LogicScene scene)
	{
		super(scene);
	}
	
	//--------------------------------
	//TODO Scene Creation
	//--------------------------------

	@Override
	public void create()
	{
		resourceManager.theme.play();
		createTitle();
		createMenuChildScene();
		menuBalloonTimer = new TimerHandler(0.5f,true,new ITimerCallback()        
		{                      
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler)
            {
            	spawnBalloon();
            }
        });
		scene.registerUpdateHandler(menuBalloonTimer);
		
	}
	
	private void createTitle()
	{
		title = new BaseSprite(240, 850, resourceManager.title_region, resourceManager.vbom);
		scene.attachChild(title);
	}
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(resourceManager.camera);
		menuChildScene.setPosition(240, 400);
		
		
		playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_PLAY, resourceManager.play_region, resourceManager.vbom), 1.025f, 1);
		optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_OPTIONS, resourceManager.options_region, resourceManager.vbom), 1.025f, 1);
		highscoreMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_HIGHSCORES, resourceManager.highscores_region, resourceManager.vbom), 1.025f, 1);
		//loginMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_LOGIN, resourceManager.login_region, resourceManager.vbom), 1.7f, 1.5f);
		rateUsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_RATE_US, resourceManager.rate_us_region, resourceManager.vbom), 1.025f, 1);
		guestMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_GUEST, resourceManager.guest_region, resourceManager.vbom), 1.025f, 1);
		howToPlayMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_HOW_TO_PLAY, resourceManager.how_to_play_region, resourceManager.vbom), 1.025f, 1);
		//removeAdsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_REMOVE_ADS, resourceManager.remove_ads_region, resourceManager.vbom), 1.025f, 1);
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(optionsMenuItem);
		menuChildScene.addMenuItem(highscoreMenuItem);
		//menuChildScene.addMenuItem(loginMenuItem);
		menuChildScene.addMenuItem(rateUsMenuItem);
		menuChildScene.addMenuItem(guestMenuItem);
		menuChildScene.addMenuItem(howToPlayMenuItem);
		//menuChildScene.addMenuItem(removeAdsMenuItem);
		
		//loginMenuItem.setVisible(!ResourceManager.getInstance().activity.usesFacebook());
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		rateUsMenuItem.setPosition(-170, 384);
		
		playMenuItem.setX(0f);
		guestMenuItem.setX(0f);
		optionsMenuItem.setX(0f);
		howToPlayMenuItem.setX(0f);
		highscoreMenuItem.setX(0f);
		//loginMenuItem.setPosition(180, -375);
		
		//removeAdsMenuItem.setPosition(-95, -380);
		menuChildScene.setOnMenuItemClickListener(this);
		scene.setChildScene(menuChildScene);
	}
	
	//----------------------------------
	// End Scene Creation
	//----------------------------------
	
	//----------------------------------
	//TODO BaseHandler Override Methods
	//----------------------------------

	@Override
	public void disposeScene()
	{
		ResourceManager.getInstance().unloadMenuResources();
		scene.unregisterUpdateHandler(menuBalloonTimer);
		menuBalloonTimer = null;
		
		menuChildScene.detachChildren();
		menuChildScene.dispose();
		menuChildScene = null;
		
		title.detachSelf();
		title.dispose();
		title = null;

		playMenuItem.clearEntityModifiers();
		playMenuItem.clearUpdateHandlers();
		playMenuItem.detachSelf();
		playMenuItem.dispose();
		playMenuItem = null;
		
		optionsMenuItem.clearEntityModifiers();
		optionsMenuItem.clearUpdateHandlers();
		optionsMenuItem.detachSelf();
		optionsMenuItem.dispose();
		optionsMenuItem = null;
		
		highscoreMenuItem.clearEntityModifiers();
		highscoreMenuItem.clearUpdateHandlers();
		highscoreMenuItem.detachSelf();
		highscoreMenuItem.dispose();
		highscoreMenuItem = null;
		
		rateUsMenuItem.clearEntityModifiers();
		rateUsMenuItem.clearUpdateHandlers();
		rateUsMenuItem.detachSelf();
		rateUsMenuItem.dispose();
		rateUsMenuItem = null;
		
		guestMenuItem.clearEntityModifiers();
		guestMenuItem.clearUpdateHandlers();
		guestMenuItem.detachSelf();
		guestMenuItem.dispose();
		guestMenuItem = null;
		
		howToPlayMenuItem.clearEntityModifiers();
		howToPlayMenuItem.clearUpdateHandlers();
		howToPlayMenuItem.detachSelf();
		howToPlayMenuItem.dispose();
		howToPlayMenuItem = null;
	}

	@Override
	public void pause()
	{
		//Nothing To Do
	}

	@Override
	public void unload()
	{

		title.detachSelf();
		scene.clearChildScene();
		scene.unregisterUpdateHandler(menuBalloonTimer);
		
		scene.clearBalloonLayer();
	}
	
	@Override
	public void load()
	{
		scene.registerUpdateHandler(menuBalloonTimer);
		scene.attachChild(title);
		scene.setChildScene(menuChildScene);
		
		title.resetEntityModifiers();
		playMenuItem.resetEntityModifiers();
		optionsMenuItem.resetEntityModifiers();
		highscoreMenuItem.resetEntityModifiers();
		//loginMenuItem.resetEntityModifiers();
		guestMenuItem.resetEntityModifiers();
		howToPlayMenuItem.resetEntityModifiers();
		
		//loginMenuItem.setVisible(!ResourceManager.getInstance().activity.usesFacebook());
		
	}

	@Override
	public void addToScore(int i, BalloonType type, boolean poppedByBomb){}

	@Override
	public void activateFreeze(){}

	@Override
	public void activateX2(){}

	@Override
	public void onBackKeyPressed()
	{
		resourceManager.activity.showQuitMenu();
	}
	
	@Override
	public boolean started()
	{
		return false;
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_MENU;
	}
	
	//----------------------------------
	// End BaseHandler Override Methods
	//----------------------------------
	
	public void registerMenuModifiers()
	{
		MoveYModifier mod = new MoveYModifier(0.75f, -450, 105);
		mod.setAutoUnregisterWhenFinished(false);
		playMenuItem.registerEntityModifier(mod);
		
		mod = new MoveYModifier(0.75f, -450, 40);
		mod.setAutoUnregisterWhenFinished(false);
		guestMenuItem.registerEntityModifier(mod);
		
		mod = new MoveYModifier(0.75f, -450, -25);
		mod.setAutoUnregisterWhenFinished(false);
		optionsMenuItem.registerEntityModifier(mod);
		
		mod = new MoveYModifier(0.75f, -450, -90);
		mod.setAutoUnregisterWhenFinished(false);
		highscoreMenuItem.registerEntityModifier(mod);
		
		mod = new MoveYModifier(0.75f, -450, -155);
		mod.setAutoUnregisterWhenFinished(false);
		howToPlayMenuItem.registerEntityModifier(mod);

		mod = new MoveYModifier(0.75f, 850, 725);
		mod.setAutoUnregisterWhenFinished(false);
		title.registerEntityModifier(mod);
	}
	
	private void spawnBalloon()
	{
		BalloonPoolType type = ResourceManager.BalloonPoolType.valueOf(MathUtils.random(0, 2));
		int balloon = MathUtils.random(6);
		if(type == ResourceManager.BalloonPoolType.SKULL)
			balloon+=6;
		if(type == ResourceManager.BalloonPoolType.BONUS)
			balloon = MathUtils.random(3) + 12;
	
		scene.spawnBalloon(type, balloon);
	}
	
	//public void updateFacebookButton()
	//{
	//	loginMenuItem.setVisible(!ResourceManager.getInstance().activity.usesFacebook());
	//}
	
	//--------------------------------
	//TODO IOnMenuItemClickListener
	//--------------------------------
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch(pMenuItem.getID())
		{
			case Constant.MENU_PLAY:
				// Load Game Scene
				ResourceManager.getInstance().button_click.play();
				if(ResourceManager.getInstance().gameSettings.game_started && ResourceManager.getInstance().gameSettings.guest_play)
				{
					resourceManager.activity.showConfirmDialog("Guest Game In Progress.\n Restart In Normal Mode?", new B2DialogListener()
					{

						@Override
						public void onPositiveClick()
						{
							scene.resetGame();
							ResourceManager.getInstance().gameSettings.guest_play = false;
							SceneManager.getInstance().setScene(SceneType.SCENE_GAME);
						}
						
					});
					return true;
				}
				ResourceManager.getInstance().gameSettings.guest_play = false;
				SceneManager.getInstance().setScene(SceneType.SCENE_GAME);
				return true;
			case Constant.MENU_GUEST:
				// Load Game Scene
				ResourceManager.getInstance().button_click.play();
				if(ResourceManager.getInstance().gameSettings.game_started && !ResourceManager.getInstance().gameSettings.guest_play)
				{
					resourceManager.activity.showConfirmDialog("Normal Game In Progress.\n Restart In Guest Mode?", new B2DialogListener()
					{

						@Override
						public void onPositiveClick()
						{
							scene.resetGame();
							ResourceManager.getInstance().gameSettings.guest_play = true;
							SceneManager.getInstance().setScene(SceneType.SCENE_GAME);
						}
						
					});
					return true;
				}
				ResourceManager.getInstance().gameSettings.guest_play = true;
				SceneManager.getInstance().setScene(SceneType.SCENE_GAME);
				return true;
			case Constant.MENU_OPTIONS:
				ResourceManager.getInstance().button_click.play();
				SceneManager.getInstance().setScene(SceneType.SCENE_OPTION);
				return true;
			case Constant.MENU_HIGHSCORES:
				ResourceManager.getInstance().button_click.play();
				SceneManager.getInstance().loadHighScoreScene(resourceManager.engine);
				return true;
			case Constant.MENU_HOW_TO_PLAY:
				ResourceManager.getInstance().button_click.play();
				SceneManager.getInstance().loadHowToPlayScene(resourceManager.engine);
				return true;
			//case Constant.MENU_LOGIN:
				//if(loginMenuItem.isVisible())
				//	ResourceManager.getInstance().activity.facebookLogin();
				//ResourceManager.getInstance().button_click.play();
				//loginMenuItem.setVisible(false);
			//	return true;
			case Constant.MENU_RATE_US:
				resourceManager.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("market://details?id="
								+ resourceManager.activity.getPackageName())));
				return true;
			default:
				return false;
		}
	}
}
