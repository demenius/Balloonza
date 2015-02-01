package com.bplatz.balloonza.handler;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import com.bplatz.balloonza.base.BaseHandler;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.base.GameSettings.GameOption;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.manager.SceneManager.SceneType;
import com.bplatz.balloonza.object.Balloon.BalloonType;
import com.bplatz.balloonza.scene.LogicScene;
import com.bplatz.ui.OnOffSlider;
import com.bplatz.ui.OnOffSlider.OnOffState;

public class OptionHandler extends BaseHandler implements IOnMenuItemClickListener
{
	private Text defaultUsername;
	private Entity optionDisplay;
	private MenuScene menuChildScene;
	private OnOffSlider symbolSlider;
	private OnOffSlider soundSlider;
	private OnOffSlider musicSlider;
	private OnOffSlider vibrateSlider;
	
	//private OnOffSlider facebookSlider;
	//private OnOffSlider localSlider;
	
	public OptionHandler(LogicScene scene)
	{
		super(scene);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void create()
	{
		optionDisplay = new Entity(0, 0);
		createTitle();
		createDetail();
		createSymbolOption();
		createMuteVibrateOptions();
		createDefaultUsername();
		
		createButtons();
	}
	
	private void createDetail()
	{
		BaseSprite image;
		BaseSprite skull;
		
		image = new BaseSprite(100, 725, resourceManager.green_balloon_region, resourceManager.vbom);
		optionDisplay.attachChild(image);
		image = new BaseSprite(380, 725, resourceManager.yellow_balloon_region, resourceManager.vbom);
		optionDisplay.attachChild(image);
		
		skull = new BaseSprite(40, 60, resourceManager.skull_region, resourceManager.vbom);
		skull.setScale(0.50f);
		optionDisplay.attachChild(skull);
		skull = new BaseSprite(440, 60, resourceManager.skull_region, resourceManager.vbom);
		skull.setScale(0.50f);
		optionDisplay.attachChild(skull);
		
	}
	
	private void createTitle()
	{
		Text title = new Text(240, 725, resourceManager.large_font, "Options", resourceManager.vbom);
		title.setColor(Color.CYAN);
		optionDisplay.attachChild(title);
	}
	
	private void createSymbolOption()
	{
		Text useSymbol = new Text(25, 600, resourceManager.small_font, "Numbered Balloons", new TextOptions(HorizontalAlign.LEFT),
				resourceManager.vbom);
		useSymbol.setAnchorCenterX(0.0f);
		optionDisplay.attachChild(useSymbol);
		symbolSlider = new OnOffSlider(300, 600, resourceManager.vbom, new OnOffSlider.Callback()
		{
			@Override
			public void onStateChange(OnOffState pState)
			{
				resourceManager.useNormalBalloons(pState == OnOffState.OFF);
			}
		});
		symbolSlider.setState(resourceManager.gameSettings.gameOption == GameOption.COLOR_BLIND ? OnOffState.ON : OnOffState.OFF);
		optionDisplay.attachChild(symbolSlider);
	}
	
	private void createDefaultUsername()
	{
		Text defaultUsernameText = new Text(25, 525, resourceManager.small_font, "Username", new TextOptions(HorizontalAlign.LEFT),
				resourceManager.vbom);
		defaultUsernameText.setAnchorCenterX(0.0f);
		optionDisplay.attachChild(defaultUsernameText);
		
		defaultUsername = new Text(475, 525, resourceManager.small_font, "--------", new TextOptions(HorizontalAlign.RIGHT), resourceManager.vbom);
		defaultUsername.setAnchorCenterX(1.0f);
		if(resourceManager.gameSettings.default_name != null)
			defaultUsername.setText(resourceManager.gameSettings.default_name);
		optionDisplay.attachChild(defaultUsername);
	}
	
	private void createMuteVibrateOptions()
	{
		Text sound = new Text(25, 400, resourceManager.small_font, "Sound", new TextOptions(HorizontalAlign.LEFT),
				resourceManager.vbom);
		sound.setAnchorCenterX(0.0f);
		optionDisplay.attachChild(sound);
		soundSlider = new OnOffSlider(300, 400, resourceManager.vbom, new OnOffSlider.Callback()
		{
			@Override
			public void onStateChange(OnOffState pState)
			{
				resourceManager.muteSound(pState == OnOffState.OFF);
			}
		});
		soundSlider.setState(resourceManager.gameSettings.sound_muted ? OnOffState.OFF : OnOffState.ON);
		optionDisplay.attachChild(soundSlider);
		
		Text music = new Text(25, 340, resourceManager.small_font, "Music", new TextOptions(HorizontalAlign.LEFT),
				resourceManager.vbom);
		music.setAnchorCenterX(0.0f);
		optionDisplay.attachChild(music);
		musicSlider = new OnOffSlider(300, 340, resourceManager.vbom, new OnOffSlider.Callback()
		{
			@Override
			public void onStateChange(OnOffState pState)
			{
				resourceManager.muteMusic(pState == OnOffState.OFF);
			}
		});
		musicSlider.setState(resourceManager.gameSettings.music_muted ? OnOffState.OFF : OnOffState.ON);
		optionDisplay.attachChild(musicSlider);
		
		Text vibrate = new Text(25, 280, resourceManager.small_font, "Vibrate", new TextOptions(HorizontalAlign.LEFT),
				resourceManager.vbom);
		vibrate.setAnchorCenterX(0.0f);
		optionDisplay.attachChild(vibrate);
		vibrateSlider = new OnOffSlider(300, 280, resourceManager.vbom, new OnOffSlider.Callback()
		{
			@Override
			public void onStateChange(OnOffState pState)
			{
				resourceManager.disableVibrate(pState == OnOffState.OFF);
			}
		});
		vibrateSlider.setState(resourceManager.gameSettings.vibrate_disabled ? OnOffState.OFF : OnOffState.ON);
		optionDisplay.attachChild(vibrateSlider);
	}
	
	/*private void createLocalLeaderboardOptions()
	{
		Text leaderboards = new Text(240, 400, resourceManager.medium_font, "Highscore Table", resourceManager.vbom);
		leaderboards.setColor(Color.CYAN);
		optionDisplay.attachChild(leaderboards);
		
		Text useFacebook = new Text(25, 325, resourceManager.small_font, "Use Facebook Name", new TextOptions(HorizontalAlign.LEFT),
				resourceManager.vbom);
		useFacebook.setAnchorCenterX(0.0f);
		optionDisplay.attachChild(useFacebook);
		facebookSlider = new OnOffSlider(350, 325, resourceManager.vbom, new OnOffSlider.Callback()
		{
			@Override
			public void onStateChange(OnOffState pState)
			{
				// TODO FACEBOOK SLIDER
				resourceManager.gameSettings.use_facebook_name = (pState == OnOffState.ON);
			}
		});
		
		facebookSlider.setState(resourceManager.gameSettings.use_facebook_name ? OnOffState.ON : OnOffState.OFF);
		optionDisplay.attachChild(facebookSlider);
		
		Text localGame = new Text(25, 250, resourceManager.small_font, "Local Game Only", new TextOptions(HorizontalAlign.LEFT), resourceManager.vbom);
		localGame.setAnchorCenterX(0.0f);
		optionDisplay.attachChild(localGame);
		localSlider = new OnOffSlider(350, 250, resourceManager.vbom, new OnOffSlider.Callback()
		{
			@Override
			public void onStateChange(OnOffState pState)
			{
				// TODO LOCAL SLIDER
				resourceManager.gameSettings.local_game_only = (pState == OnOffState.ON);
			}
		});
		localSlider.setState(resourceManager.gameSettings.local_game_only ? OnOffState.ON : OnOffState.OFF);
		optionDisplay.attachChild(localSlider);
		
	}*/
	
	private void createButtons()
	{
		menuChildScene = new MenuScene(resourceManager.camera);
		menuChildScene.setPosition(240, 0);
		
		// logoutMenuItem = new ScaleMenuItemDecorator(new
		// SpriteMenuItem(Constant.MENU_LOGOUT, resourceManager.logout_region,
		// resourceManager.vbom), 1.7f, 1.5f);
		IMenuItem resetMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(Constant.MENU_RESET, resourceManager.medium_font, "Reset Highscores", resourceManager.vbom),
				1.1f, 1.0f);
		resetMenuItem.setColor(Color.RED);
		IMenuItem change = new ScaleMenuItemDecorator(new TextMenuItem(Constant.MENU_CHANGE, resourceManager.medium_font, "Change Username",
				resourceManager.vbom), 1.05f, 1.0f);
		change.setColor(Color.RED);
		
		// menuChildScene.addMenuItem(logoutMenuItem);
		
		
		menuChildScene.addMenuItem(resetMenuItem);
		menuChildScene.addMenuItem(change);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		// logoutMenuItem.setPosition(170, 25);
		
		resetMenuItem.setPosition(0, 60);
		change.setPosition(0, 475);
		
		menuChildScene.setOnMenuItemClickListener(this);
	}
	
	@Override
	public void pause()
	{
	}
	
	@Override
	public void unload()
	{
		scene.unregisterTouchArea(symbolSlider);
		scene.unregisterTouchArea(musicSlider);
		scene.unregisterTouchArea(soundSlider);
		scene.unregisterTouchArea(vibrateSlider);
		//scene.unregisterTouchArea(facebookSlider);
		//scene.unregisterTouchArea(localSlider);
		optionDisplay.detachSelf();

		scene.clearChildScene();
	}
	
	public void setSoundSlider(final boolean enabled)
	{
		soundSlider.setState(enabled ? OnOffState.ON : OnOffState.OFF);
	}
	
	public void setMusicSlider(final boolean enabled)
	{
		musicSlider.setState(enabled ? OnOffState.ON : OnOffState.OFF);
	}
	
	public void setVibrateSlider(final boolean enabled)
	{
		vibrateSlider.setState(enabled ? OnOffState.ON : OnOffState.OFF);
	}
	
	public void setDefaultUsername(final String username)
	{
		defaultUsername.setText(username);
	}
	
	@Override
	public void load()
	{
		scene.registerTouchArea(symbolSlider);
		scene.registerTouchArea(musicSlider);
		scene.registerTouchArea(soundSlider);
		scene.registerTouchArea(vibrateSlider);
		//scene.registerTouchArea(facebookSlider);
		//scene.registerTouchArea(localSlider);
		scene.attachChild(optionDisplay);

		scene.setChildScene(menuChildScene);
	}
	
	@Override
	public void addToScore(int i, BalloonType type, boolean poppedByBomb)
	{
	}
	
	@Override
	public void activateFreeze()
	{
	}
	
	@Override
	public void activateX2()
	{
	}
	
	@Override
	public void onBackKeyPressed()
	{
		SceneManager.getInstance().setScene(SceneType.SCENE_MENU);
	}
	
	@Override
	public boolean started()
	{
		return false;
	}
	
	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_OPTION;
	}
	
	// --------------------------------
	// TODO IOnMenuItemClickListener
	// --------------------------------
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch (pMenuItem.getID())
		{
			case Constant.MENU_LOGOUT:
				ResourceManager.getInstance().button_click.play();
				// ResourceManager.getInstance().activity.facebookLogout();
				return true;
			case Constant.MENU_RESET:
				ResourceManager.getInstance().button_click.play();
				ResourceManager.getInstance().activity.showResetMenu();
				return true;
			case Constant.MENU_CHANGE:
				ResourceManager.getInstance().button_click.play();
				ResourceManager.getInstance().activity.showB2UsernameDialog(true);
				return true;
			default:
				return false;
		}
	}

	@Override
	public void disposeScene()
	{
		
	}
	
}
