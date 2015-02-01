package com.bplatz.balloonza.handler;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;

import com.appflood.AppFlood;
import com.bplatz.balloonza.base.BaseHandler;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.custom.LifeBar;
import com.bplatz.balloonza.custom.Points;
import com.bplatz.balloonza.custom.ScoreInfo;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.ResourceManager.BalloonPoolType;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.manager.SceneManager.SceneType;
import com.bplatz.balloonza.object.Balloon.BalloonSprite;
import com.bplatz.balloonza.object.Balloon.BalloonType;
import com.bplatz.balloonza.scene.LogicScene;

public class GameHandler extends BaseHandler implements IOnSceneTouchListener, IOnMenuItemClickListener
{
	private MenuScene replayView;
	private IMenuItem replayMenuItem;
	private IMenuItem highScoreMenuItem;
	private IMenuItem moreGamesMenuItem;
	private IMenuItem heyzapMenuItem;
	
	private boolean started = false;
	private boolean gameOverDisplayed = false;
	private Text startText;
	private Text gameOverText;
	
	// Stats Display
	private Entity gameOverDisplay;
	private Text timePassedStat;
	private Text pointsStat;
	private Text comboStat;
	private Text scoreStat;
	
	private IEntity gameHUD;
	private BaseSprite balloonBackdrop;
	private LifeBar lifeBar;
	
	//private SequenceEntityModifier backdropBalloonFadeOutModifier;
	//private AlphaModifier backdropBalloonFadeInModifier;
	private SequenceEntityModifier backdropBalloonScaleOutModifier;
	private SequenceEntityModifier backdropBalloonScaleInModifier;
	
	private BaseSprite currentBalloon;
	private BalloonType currentType;
	
	private int[] currentBalloons;
	private int[] currentSkulls;
	private int[] currentBonus;
	
	private int[] remainingBalloons;
	private int[] remainingSkulls;
	private int[] remainingBonus;
	
	private BaseSprite blueBalloon;
	private BaseSprite greenBalloon;
	private BaseSprite orangeBalloon;
	private BaseSprite purpleBalloon;
	private BaseSprite redBalloon;
	private BaseSprite yellowBalloon;
	
	private TimerHandler mainTimer;
	private TimerHandler progressTimer;
	private TimerHandler colorTimer;
	private TimerHandler balloonTimer;
	private TimerHandler bonusTimer;
	private TimerHandler skullTimer;
	private TimerHandler balloonTypeTimer;
	private TimerHandler x2BalloonTimer;
	private TimerHandler skullModActivator;
	
	private TimerHandler balloonSpawnRateTimer;
	private int balloonSpawnRateIndex = 0;
	private TimerHandler bonusSpawnRateTimer;
	private int bonusSpawnRateIndex = 0;
	private TimerHandler skullSpawnRateTimer;
	private int skullSpawnRateIndex = 0;
	
	private TimerHandler balloonSpeedTimer;
	private int balloonSpeedIndex = 0;
	private TimerHandler bonusSpeedTimer;
	private int bonusSpeedIndex = 0;
	private TimerHandler skullSpeedTimer;
	private int skullSpeedIndex = 0;
	
	private TimerHandler progressSpeedTimer;
	private int progressSpeedIndex = 0;
	
	private TimerHandler fatKidTimer;
	private int fatKidIndex = 0;
	
	private Points points;
	private Text comboText;
	private int combo = 0;
	private int multiplier = 0;
	private boolean x2Active = false;
	private int topCombo = 0;
	private int finalScore = 0;
	
	// [0] Balloon : [1] Skull : [2] Bonus
	private int[] balloonCounts;
	
	private float timePassed = 0;
	
	private final Runnable UNREGISTER_HANDLER = new Runnable()
	{
		@Override
		public void run()
		{
			GameHandler.this.unregisterUpdateHandlers();
		}
	};
	
	private final Runnable REGISTER_HANDLER = new Runnable()
	{

		@Override
		public void run()
		{
			if (x2Active)
				scene.registerUpdateHandler(x2BalloonTimer);
			scene.registerUpdateHandler(progressTimer);
			scene.registerUpdateHandler(colorTimer);
			scene.registerUpdateHandler(balloonTimer);
			scene.registerUpdateHandler(bonusTimer);
			scene.registerUpdateHandler(skullTimer);
			scene.registerUpdateHandler(balloonTypeTimer);
			scene.registerUpdateHandler(mainTimer);
			scene.registerUpdateHandler(skullModActivator);
			
			scene.registerUpdateHandler(balloonSpawnRateTimer);
			scene.registerUpdateHandler(bonusSpawnRateTimer);
			scene.registerUpdateHandler(skullSpawnRateTimer);
			scene.registerUpdateHandler(balloonSpeedTimer);
			scene.registerUpdateHandler(bonusSpeedTimer);
			scene.registerUpdateHandler(skullSpeedTimer);

			scene.registerUpdateHandler(progressSpeedTimer);
			
			scene.registerUpdateHandler(fatKidTimer);
			lifeBar.registerScaler();
		}
		
	};
	
	
	private final Runnable COLOR_CHANGE_HANDLER = new Runnable()
	{
		@Override
		public void run()
		{
			final int color = currentBalloons[MathUtils.random(balloonCounts[0])];
			currentBalloon.setVisible(false);
			switch (color)
			{
				case 0:
					currentBalloon = blueBalloon;
					currentType = BalloonType.BLUE_BALLOON;
					lifeBar.setColor(Color.BLUE);
					break;
				case 1:
					currentBalloon = greenBalloon;
					currentType = BalloonType.GREEN_BALLOON;
					lifeBar.setColor(Color.GREEN);
					break;
				case 2:
					currentBalloon = orangeBalloon;
					currentType = BalloonType.ORANGE_BALLOON;
					lifeBar.setColor(1.0f, 0.55f, 0.0f);
					break;
				case 3:
					currentBalloon = purpleBalloon;
					currentType = BalloonType.PURPLE_BALLOON;
					lifeBar.setColor(0.63f, 0.13f, 0.94f);
					break;
				case 4:
					currentBalloon = redBalloon;
					currentType = BalloonType.RED_BALLOON;
					lifeBar.setColor(Color.RED);
					break;
				case 5:
					currentBalloon = yellowBalloon;
					currentType = BalloonType.YELLOW_BALLOON;
					lifeBar.setColor(Color.YELLOW);
					break;
			}
			currentBalloon.setVisible(true);
			//backdropBalloonFadeInModifier.reset();
			backdropBalloonScaleInModifier.reset();
			//currentBalloon.registerEntityModifier(backdropBalloonFadeInModifier);
			currentBalloon.registerEntityModifier(backdropBalloonScaleInModifier);
		}
	};
	
	public GameHandler(LogicScene scene)
	{
		super(scene);
	}
	
	// ----------------------------------
	// TODO Scene Creation
	// ----------------------------------
	
	@Override
	public void create()
	{
		setupBalloonColors();
		createHUD();
		createStartText();
		createGameOverText();
		createTimers();
		createEntityModifiers();
	}
	
	private void setupBalloonColors()
	{
		balloonCounts = new int[3];
		balloonCounts[0] = 0;
		balloonCounts[1] = 0;
		balloonCounts[2] = 0;
		remainingBalloons = new int[6];
		for (int i = 0, v = BalloonType.BLUE_BALLOON.ordinal(); v <= BalloonType.YELLOW_BALLOON.ordinal(); i++, v++)
			remainingBalloons[i] = v;
		
		remainingSkulls = new int[3]; // Only Need 3 Max For Beginning. Then
										// Only 1 At A Time
		
		remainingBonus = new int[3];
		for (int i = 0, v = BalloonType.BOMB_BALLOON.ordinal(); v <= BalloonType.X2_BALLOON.ordinal(); i++, v++)
			remainingBonus[i] = v;
		
		currentBalloons = new int[6];
		currentSkulls = new int[6];
		currentBonus = new int[3];
		addBalloon();
		addBalloon();
		addBalloon();
		changeColor();
	}
	
	private void createHUD()
	{
		gameHUD = new Entity();
		
		lifeBar = new LifeBar(253, 744, 410, 20);
		gameHUD.attachChild(lifeBar);
		
		// Create Score Text
		points = new Points(125, 770, resourceManager.medium_font, "Score:", new TextOptions(HorizontalAlign.LEFT), resourceManager.vbom);
		gameHUD.attachChild(points);
		
		// Create Combo Text
		Text combo = new Text(15, 685, resourceManager.medium_font, "Combo:", new TextOptions(HorizontalAlign.LEFT), resourceManager.vbom);
		combo.setAnchorCenter(0, 0);
		gameHUD.attachChild(combo);
		
		comboText = new Text(165, 685, resourceManager.medium_font, "0123456789", new TextOptions(HorizontalAlign.LEFT), resourceManager.vbom);
		comboText.setAnchorCenter(0, 0);
		comboText.setText("0");
		gameHUD.attachChild(comboText);
		
		balloonBackdrop = new BaseSprite(35, 765, resourceManager.balloon_backdrop_region, resourceManager.vbom);
		
		gameHUD.attachChild(balloonBackdrop);
		
		blueBalloon = createNewBackdropBalloon(resourceManager.blue_balloon_region);
		greenBalloon = createNewBackdropBalloon(resourceManager.green_balloon_region);
		orangeBalloon = createNewBackdropBalloon(resourceManager.orange_balloon_region);
		purpleBalloon = createNewBackdropBalloon(resourceManager.purple_balloon_region);
		redBalloon = createNewBackdropBalloon(resourceManager.red_balloon_region);
		yellowBalloon = createNewBackdropBalloon(resourceManager.yellow_balloon_region);
		
		balloonBackdrop.attachChild(blueBalloon);
		balloonBackdrop.attachChild(greenBalloon);
		balloonBackdrop.attachChild(orangeBalloon);
		balloonBackdrop.attachChild(purpleBalloon);
		balloonBackdrop.attachChild(redBalloon);
		balloonBackdrop.attachChild(yellowBalloon);
		
		currentBalloon = blueBalloon;
	}
	
	private BaseSprite createNewBackdropBalloon(ITiledTextureRegion region)
	{
		BaseSprite b = new BaseSprite(balloonBackdrop.getWidth() / 2, balloonBackdrop.getHeight() / 2, region, resourceManager.vbom);
		b.setScale(0.0f);
		//b.setScale(0.65f);
		//b.setAlpha(0.0f);
		b.setVisible(false);
		
		return b;
	}
	
	private void createStartText()
	{
		startText = new Text(resourceManager.camera.getCenterX(), resourceManager.camera.getCenterY(), resourceManager.medium_font,
				"Touch Screen To Start", resourceManager.vbom);
	}
	
	private void createGameOverText()
	{
		createGameOverMenu();
		
		gameOverDisplay = new Entity(resourceManager.camera.getCenterX(), resourceManager.camera.getCenterY());
		
		gameOverText = new Text(0, 90, resourceManager.medium_font, "GAME OVER!", resourceManager.vbom);
		gameOverText.setColor(Color.RED);
		timePassedStat = new Text(0, 0, resourceManager.medium_font, "Time Passed: 0123456789s", resourceManager.vbom);
		timePassedStat.setColor(1.0f, 0.55f, 0.0f);
		pointsStat = new Text(0, -30, resourceManager.medium_font, "Points: 0,123,456,789", resourceManager.vbom);
		pointsStat.setColor(Color.GREEN);
		comboStat = new Text(0, -60, resourceManager.medium_font, "Best Combo: 0123456789", resourceManager.vbom);
		comboStat.setColor(0.63f, 0.13f, 0.94f);
		scoreStat = new Text(0, -100, resourceManager.medium_font, "Final Score: 0,123,456,789", resourceManager.vbom);
		scoreStat.setColor(Color.YELLOW);
		
		gameOverText.setScale(2.0f);
		
		gameOverDisplay.attachChild(gameOverText);
		gameOverDisplay.attachChild(timePassedStat);
		gameOverDisplay.attachChild(pointsStat);
		gameOverDisplay.attachChild(comboStat);
		gameOverDisplay.attachChild(scoreStat);
		
		gameOverDisplay.setVisible(false);
	}
	
	private void createGameOverMenu()
	{
		replayView = new MenuScene(resourceManager.camera);
		replayView.setPosition(0, 250);
		
		replayMenuItem = new ScaleMenuItemDecorator(
				new TextMenuItem(Constant.MENU_REPLAY, resourceManager.small_font, "Replay", resourceManager.vbom), 1.1f, 1);
		highScoreMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(Constant.MENU_VIEW, resourceManager.small_font, " High\nScores",
				resourceManager.vbom), 1.1f, 1);
		moreGamesMenuItem = new ScaleMenuItemDecorator(new TextMenuItem(Constant.MENU_MORE, resourceManager.small_font, " More\nGames",
				resourceManager.vbom), 1.1f, 1);
		TextMenuItem submit = new TextMenuItem(Constant.MENU_SUBMIT_HEYZAP, resourceManager.small_font, "Submit To              ",
				resourceManager.vbom);
		BaseSprite heyzap = new BaseSprite(0, 0, resourceManager.heyzap_leaderboard_region, resourceManager.vbom);
		heyzap.setScale(0.75f);
		submit.attachChild(heyzap);
		submit.setColor(Color.YELLOW);
		
		heyzapMenuItem = new ScaleMenuItemDecorator(submit, 1.1f, 1);
		
		replayView.addMenuItem(replayMenuItem);
		replayView.addMenuItem(moreGamesMenuItem);
		replayView.addMenuItem(highScoreMenuItem);
		replayView.addMenuItem(heyzapMenuItem);
		
		replayView.buildAnimations();
		replayView.setBackgroundEnabled(false);

		replayMenuItem.setPosition(100, 0);
		moreGamesMenuItem.setPosition(240, 0);
		highScoreMenuItem.setPosition(380, 0);
		heyzapMenuItem.setPosition(240, -60);
		heyzap.setAnchorCenterY(0.0f);
		heyzap.setX(255);
		
		replayView.setOnMenuItemClickListener(this);
	}
	
	private void createTimers()
	{
		mainTimer = new TimerHandler(0.25f, true, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				timePassed += 0.25f;
			}
		});
		progressTimer = new TimerHandler(Constant.PROGRESS_TIME, true, lifeBar.getController());
		
		colorTimer = new TimerHandler(MathUtils.random(Constant.MIN_COLOR_TIME, Constant.MAX_COLOR_TIME), false, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				//backdropBalloonFadeOutModifier.reset();
				backdropBalloonScaleOutModifier.reset();
				//currentBalloon.registerEntityModifier(backdropBalloonFadeOutModifier);
				currentBalloon.registerEntityModifier(backdropBalloonScaleOutModifier);
			}
		});
		
		balloonTimer = new TimerHandler(MathUtils.random(Constant.MIN_BALLOON_TIME, Constant.MAX_BALLOON_TIME) / balloonCounts[0], false,
				new ITimerCallback()
				{
					@Override
					public void onTimePassed(final TimerHandler pTimerHandler)
					{
						spawnBalloon(BalloonPoolType.BALLOON);
						balloonTimer.setTimerSeconds((MathUtils.random(Constant.MIN_BALLOON_TIME, Constant.MAX_BALLOON_TIME) / balloonCounts[0])
								+ ResourceManager.getInstance().gameVariable.balloon_extra_spawn);
						balloonTimer.reset();
					}
				});
		
		bonusTimer = new TimerHandler(MathUtils.random(Constant.MIN_BONUS_TIME, Constant.MAX_BONUS_TIME), false, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				spawnBalloon(BalloonPoolType.BONUS);
				if (balloonCounts[2] > 0)
					bonusTimer.setTimerSeconds((MathUtils.random(Constant.MIN_BONUS_TIME, Constant.MAX_BONUS_TIME) / balloonCounts[2])
							+ ResourceManager.getInstance().gameVariable.bonus_extra_spawn);
				bonusTimer.reset();
			}
		});
		
		skullTimer = new TimerHandler(MathUtils.random(Constant.MIN_SKULL_TIME, Constant.MAX_SKULL_TIME), false, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				spawnBalloon(BalloonPoolType.SKULL);
				if (balloonCounts[1] > 0)
					skullTimer.setTimerSeconds((MathUtils.random(Constant.MIN_SKULL_TIME, Constant.MAX_SKULL_TIME) / balloonCounts[1])
							+ ResourceManager.getInstance().gameVariable.skull_extra_spawn);
				skullTimer.reset();
			}
		});
		
		balloonTypeTimer = new TimerHandler(Constant.BALLOON_TYPE_TIMER[1], false, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				// Read Algorithm From Bottom Up.
				
				// End Of Algorithm
				if (balloonCounts[1] == 6)
				{
				} // Do Nothing
					// Spawn Last Skull
				else if (balloonCounts[0] == 6)
					addSkull();
				// Spawn Last Balloon
				else if (balloonCounts[2] == 3)
					addBalloon();
				// Spawn Last Bonus Balloon
				else if (balloonCounts[1] == 5)
					addBonus();
				// Spawn Corresponding Skull Balloon
				else if (balloonCounts[0] == 5)
					addSkull();
				// Spawn 2C1 Remaining Balloon
				else if (balloonCounts[2] == 2)
					addBalloon();
				// Spawn 2C1 Bonus Balloon
				else if (balloonCounts[1] == 4)
					addBonus();
				// Spawn Corresponding Skull Balloon
				else if (balloonCounts[0] == 4)
					addSkull();
				// Spawn 3C1 Remaining Balloon
				else if (balloonCounts[2] == 1)
					addBalloon();
				// Spawn 3C1 Bonus Balloon
				else if (balloonCounts[1] == 3)
					addBonus();
				// Spawn The 3 Skull Balloons Corresponding To The First
				// 3 Balloons One At A Time
				// Setup In addBalloon Function. Can Only Spawn
				// Corresponding Skulls
				else if (balloonCounts[0] == 3)
					addSkull();
				// Start With 3 Random Colors. Done At Setup
				
				balloonTypeTimer.reset();
			}
		});
		
		x2BalloonTimer = new TimerHandler(Constant.X2_TIMER, false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				scene.unregisterUpdateHandler(x2BalloonTimer);
				x2Active = false;
				comboText.setColor(Color.WHITE);
				setComboText();
			}
			
		});
		
		skullModActivator = new TimerHandler(Constant.SKULL_MOD_TIMER, false, new ITimerCallback()
		{
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				scene.unregisterUpdateHandler(skullModActivator);
				ResourceManager.getInstance().gameVariable.skull_mod_active = true;
			}
		});
		
		balloonSpawnRateTimer = new TimerHandler(Constant.BALLOON_SPAWN_DECREASE_TIME[balloonSpawnRateIndex], false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				ResourceManager.getInstance().gameVariable.balloon_extra_spawn += Constant.BALLOON_SPAWN_DECREASE;
				
				if (ResourceManager.getInstance().gameVariable.balloon_extra_spawn >= Constant.BALLOON_MIN_EXTRA_SPAWN_CAP)
				{
					ResourceManager.getInstance().gameVariable.balloon_extra_spawn = Constant.BALLOON_MIN_EXTRA_SPAWN_CAP;
					scene.unregisterUpdateHandler(balloonSpawnRateTimer);
					return;
				}
				
				if (!balloonSpawnRateTimer.isAutoReset())
				{
					balloonSpawnRateIndex++;
					if (balloonSpawnRateIndex >= Constant.BALLOON_SPAWN_DECREASE_TIME.length - 1)
					{
						balloonSpawnRateIndex = Constant.BALLOON_SPAWN_DECREASE_TIME.length - 1;
						balloonSpawnRateTimer.setAutoReset(true);
					}
					balloonSpawnRateTimer.setTimerSeconds(Constant.BALLOON_SPAWN_DECREASE_TIME[balloonSpawnRateIndex]);
					
					balloonSpawnRateTimer.reset();
				}
			}
			
		});
		
		bonusSpawnRateTimer = new TimerHandler(Constant.BONUS_SPAWN_DECREASE_TIME[bonusSpawnRateIndex], false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				ResourceManager.getInstance().gameVariable.bonus_extra_spawn += Constant.BONUS_SPAWN_DECREASE;
				if (ResourceManager.getInstance().gameVariable.bonus_extra_spawn >= Constant.BONUS_MIN_EXTRA_SPAWN_CAP)
				{
					ResourceManager.getInstance().gameVariable.bonus_extra_spawn = Constant.BONUS_MIN_EXTRA_SPAWN_CAP;
					scene.unregisterUpdateHandler(bonusSpawnRateTimer);
					return;
				}
				
				if (!bonusSpawnRateTimer.isAutoReset())
				{
					bonusSpawnRateIndex++;
					if (bonusSpawnRateIndex >= Constant.BONUS_SPAWN_DECREASE_TIME.length - 1)
					{
						bonusSpawnRateIndex = Constant.BONUS_SPAWN_DECREASE_TIME.length - 1;
						bonusSpawnRateTimer.setAutoReset(true);
					}
					bonusSpawnRateTimer.setTimerSeconds(Constant.BONUS_SPAWN_DECREASE_TIME[bonusSpawnRateIndex]);
					bonusSpawnRateTimer.reset();
				}
			}
			
		});
		skullSpawnRateTimer = new TimerHandler(Constant.SKULL_SPAWN_INCREASE_TIME[skullSpawnRateIndex], false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				ResourceManager.getInstance().gameVariable.skull_extra_spawn += Constant.SKULL_SPAWN_INCREASE;

				if (ResourceManager.getInstance().gameVariable.skull_extra_spawn <= Constant.SKULL_MAX_EXTRA_SPAWN_CAP)
				{
					ResourceManager.getInstance().gameVariable.skull_extra_spawn = Constant.SKULL_MAX_EXTRA_SPAWN_CAP;
					scene.unregisterUpdateHandler(skullSpawnRateTimer);
					return;
				}
				if (!skullSpawnRateTimer.isAutoReset())
				{
					skullSpawnRateIndex++;
					if (skullSpawnRateIndex >= Constant.SKULL_SPAWN_INCREASE_TIME.length - 1)
					{
						skullSpawnRateIndex = Constant.SKULL_SPAWN_INCREASE_TIME.length - 1;
						skullSpawnRateTimer.setAutoReset(true);
					}
					skullSpawnRateTimer.setTimerSeconds(Constant.SKULL_SPAWN_INCREASE_TIME[skullSpawnRateIndex]);
					
					skullSpawnRateTimer.reset();
				}
			}
			
		});
		balloonSpeedTimer = new TimerHandler(Constant.BALLOON_SPEED_INCREASE_TIME[balloonSpeedIndex], false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				ResourceManager.getInstance().gameVariable.balloon_extra_speed += Constant.BALLOON_SPEED_INCREASE;

				if (ResourceManager.getInstance().gameVariable.balloon_extra_speed >= Constant.BALLOON_MAX_EXTRA_SPEED_CAP)
				{
					ResourceManager.getInstance().gameVariable.balloon_extra_speed = Constant.BALLOON_MAX_EXTRA_SPEED_CAP;
					scene.unregisterUpdateHandler(balloonSpeedTimer);
					return;
				}
				
				if (!balloonSpeedTimer.isAutoReset())
				{
					balloonSpeedIndex++;
					if (balloonSpeedIndex >= Constant.BALLOON_SPEED_INCREASE_TIME.length - 1)
					{
						balloonSpeedIndex = Constant.BALLOON_SPEED_INCREASE_TIME.length - 1;
						balloonSpeedTimer.setAutoReset(true);
					}
					balloonSpeedTimer.setTimerSeconds(Constant.BALLOON_SPEED_INCREASE_TIME[balloonSpeedIndex]);
					
					balloonSpeedTimer.reset();
				}
			}
			
		});
		
		bonusSpeedTimer = new TimerHandler(Constant.BONUS_SPEED_INCREASE_TIME[bonusSpeedIndex], false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				ResourceManager.getInstance().gameVariable.bonus_extra_speed += Constant.BONUS_SPEED_INCREASE;

				if (ResourceManager.getInstance().gameVariable.bonus_extra_speed >= Constant.BONUS_MAX_EXTRA_SPEED_CAP)
				{
					ResourceManager.getInstance().gameVariable.bonus_extra_speed = Constant.BONUS_MAX_EXTRA_SPEED_CAP;
					scene.unregisterUpdateHandler(bonusSpeedTimer);
					return;
				}
				
				if (!bonusSpeedTimer.isAutoReset())
				{
					bonusSpawnRateIndex++;
					if (bonusSpawnRateIndex >= Constant.BONUS_SPEED_INCREASE_TIME.length - 1)
					{
						bonusSpeedIndex = Constant.BONUS_SPEED_INCREASE_TIME.length - 1;
						bonusSpeedTimer.setAutoReset(true);
					}
					bonusSpeedTimer.setTimerSeconds(Constant.BONUS_SPEED_INCREASE_TIME[bonusSpawnRateIndex]);
					bonusSpeedTimer.reset();
				}
			}
			
		});
		
		skullSpeedTimer = new TimerHandler(Constant.SKULL_SPEED_INCREASE_TIME[skullSpeedIndex], false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				ResourceManager.getInstance().gameVariable.skull_extra_speed += Constant.SKULL_SPEED_INCREASE;

				if (ResourceManager.getInstance().gameVariable.skull_extra_speed >= Constant.SKULL_MAX_EXTRA_SPEED_CAP)
				{
					ResourceManager.getInstance().gameVariable.skull_extra_speed = Constant.SKULL_MAX_EXTRA_SPEED_CAP;
					scene.unregisterUpdateHandler(skullSpeedTimer);
					return;
				}
				
				if (!skullSpeedTimer.isAutoReset())
				{
					skullSpeedIndex++;
					if (skullSpeedIndex >= Constant.SKULL_SPEED_INCREASE_TIME.length - 1)
					{
						skullSpeedIndex = Constant.SKULL_SPEED_INCREASE_TIME.length - 1;
						skullSpeedTimer.setAutoReset(true);
					}
					skullSpeedTimer.setTimerSeconds(Constant.SKULL_SPEED_INCREASE_TIME[skullSpeedIndex]);
					
					skullSpeedTimer.reset();
				}
			}
			
		});
		
		progressSpeedTimer = new TimerHandler(Constant.PROGRESS_SPEED_INCREASE_TIME[progressSpeedIndex], false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				ResourceManager.getInstance().gameVariable.progress_extra_speed += Constant.PROGRESS_SPEED_INCREASE;
				if (ResourceManager.getInstance().gameVariable.progress_extra_speed >= Constant.PROGRESS_MAX_EXTRA_SPEED_CAP)
				{
					ResourceManager.getInstance().gameVariable.progress_extra_speed = Constant.PROGRESS_MAX_EXTRA_SPEED_CAP;
					scene.unregisterUpdateHandler(progressSpeedTimer);
					return;
				}
				
				if (!progressSpeedTimer.isAutoReset())
				{
					progressSpeedIndex++;
					if (progressSpeedIndex >= Constant.PROGRESS_SPEED_INCREASE_TIME.length - 1)
					{
						progressSpeedIndex = Constant.PROGRESS_SPEED_INCREASE_TIME.length - 1;
						progressSpeedTimer.setAutoReset(true);
					}
					progressSpeedTimer.setTimerSeconds(Constant.PROGRESS_SPEED_INCREASE_TIME[progressSpeedIndex]);
					
					progressSpeedTimer.reset();
				}
			}
			
		});
		
		fatKidTimer = new TimerHandler(Constant.FAT_KID_SPAWN_RATE[fatKidIndex], false, new ITimerCallback()
		{
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler)
			{
				scene.spawnFatKid();
				fatKidIndex++;
			}
			
		});
	}
	
	public void resetFatKid()
	{
		if (fatKidIndex >= Constant.FAT_KID_SPAWN_RATE.length - 1)
			fatKidIndex = Constant.FAT_KID_SPAWN_RATE.length - 1;
		fatKidTimer.setTimerSeconds(Constant.FAT_KID_SPAWN_RATE[fatKidIndex]);
		fatKidTimer.reset();
	}
	
	private void createEntityModifiers()
	{
		// --------- BACKDROP BALLOON MODIFIERS ----------//
		/*backdropBalloonFadeOutModifier = new SequenceEntityModifier(new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem)
			{
				
			}
			
			@Override
			public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity)
			{
				changeColor();
			}
		}, new AlphaModifier(1, 1, 0), new AlphaModifier(1, 0, 1), new AlphaModifier(1, 1, 0));
		
		backdropBalloonFadeInModifier = new AlphaModifier(1, 0, 1, new IEntityModifierListener()
		{
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem)
			{
			}
			
			@Override
			public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity)
			{
				colorTimer.setTimerSeconds(MathUtils.random(Constant.MIN_COLOR_TIME, Constant.MAX_COLOR_TIME));
				colorTimer.reset();
			}
		});*/

		backdropBalloonScaleOutModifier = new SequenceEntityModifier(new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem)
			{
				resourceManager.bell.play();
			}
			
			@Override
			public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity)
			{
				changeColor();
			}
		}, new ScaleModifier(0.25f, 0.65f, 0.975f), new ScaleModifier(0.75f, 0.975f, 0f));
		
		backdropBalloonScaleInModifier = new SequenceEntityModifier(new IEntityModifierListener()
		{
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem)
			{
			}
			
			@Override
			public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity)
			{
				colorTimer.setTimerSeconds(MathUtils.random(Constant.MIN_COLOR_TIME, Constant.MAX_COLOR_TIME));
				colorTimer.reset();
			}
		}, new ScaleModifier(0.75f, 0, 0.975f), new ScaleModifier(0.25f, 0.975f, 0.65f));
	}
	
	// ----------------------------------
	// End Scene Creation
	// ----------------------------------
	
	// ----------------------------------
	// TODO BaseHandler Override Methods
	// ----------------------------------
	@Override
	public void disposeScene()
	{
		resourceManager.engine.runOnUpdateThread(UNREGISTER_HANDLER);
		
		replayView.detachChildren();
		replayView.dispose();
		replayView = null;
		
		replayMenuItem.clearEntityModifiers();
		replayMenuItem.clearUpdateHandlers();
		replayMenuItem.detachSelf();
		replayMenuItem.dispose();
		replayMenuItem = null;
		
		highScoreMenuItem.clearEntityModifiers();
		highScoreMenuItem.clearUpdateHandlers();
		highScoreMenuItem.detachSelf();
		highScoreMenuItem.dispose();
		highScoreMenuItem = null;
		
		moreGamesMenuItem.clearEntityModifiers();
		moreGamesMenuItem.clearUpdateHandlers();
		moreGamesMenuItem.detachSelf();
		moreGamesMenuItem.dispose();
		moreGamesMenuItem = null;
		
		startText.detachSelf();
		startText.dispose();
		startText = null;
		
		gameOverText.detachSelf();
		gameOverText.dispose();
		gameOverText = null;
		
		// Stats Display
		gameOverDisplay.detachSelf();
		gameOverDisplay.dispose();
		gameOverDisplay = null;
		
		timePassedStat.detachSelf();
		timePassedStat.dispose();
		timePassedStat = null;
		
		pointsStat.detachSelf();
		pointsStat.dispose();
		pointsStat = null;
		
		comboStat.detachSelf();
		comboStat.dispose();
		comboStat = null;
		
		scoreStat.detachSelf();
		scoreStat.dispose();
		scoreStat = null;
		
		gameHUD.detachSelf();
		gameHUD.dispose();
		gameHUD = null;
		
		balloonBackdrop.detachSelf();
		balloonBackdrop.dispose();
		balloonBackdrop = null;
		
		currentBalloon.detachSelf();
		currentBalloon.dispose();
		currentBalloon = null;
		
		currentBalloons = null;
		currentSkulls = null;
		currentBonus = null;
		
		remainingBalloons = null;
		remainingSkulls = null;
		remainingBonus = null;
		
		blueBalloon.detachSelf();
		blueBalloon.dispose();
		blueBalloon = null;
		
		greenBalloon.detachSelf();
		greenBalloon.dispose();
		greenBalloon = null;
		
		orangeBalloon.detachSelf();
		orangeBalloon.dispose();
		orangeBalloon = null;
		
		purpleBalloon.detachSelf();
		purpleBalloon.dispose();
		purpleBalloon = null;
		
		redBalloon.detachSelf();
		redBalloon.dispose();
		redBalloon = null;
		
		yellowBalloon.detachSelf();
		yellowBalloon.dispose();
		yellowBalloon = null;
		
		points.remove();
		
		comboText.detachSelf();
		comboText.dispose();
		comboText = null;
		
		balloonCounts = null;
	}
	
	@Override
	public void pause()
	{
		if (!gameOverDisplayed)
		{
			startText.setVisible(true);
			started = false;
			resourceManager.engine.runOnUpdateThread(UNREGISTER_HANDLER);
			scene.pauseBalloonLayer();
		}
	}
	
	@Override
	public void unload()
	{
		if (!gameOverDisplayed)
		{
			unregisterUpdateHandlers();
		} else
		{
			resetGame();
		}
		gameOverDisplay.detachSelf();
		scene.setOnSceneTouchListener(null);
		gameHUD.detachSelf();
		gameOverDisplay.setVisible(false);
		startText.detachSelf();
		started = false;
		gameOverDisplayed = false;
		scene.clearBalloonLayer();
	}
	
	public void resetGame()
	{
		// Reset Game

		ScoreInfo.score_name = null;
		finalScore = 0;
		points.reset();
		combo = 0;
		multiplier = 0;
		x2Active = false;
		comboText.setText("" + combo);
		comboText.setColor(Color.WHITE);
		topCombo = 0;
		setupBalloonColors();
		timePassed = 0;
		lifeBar.reset();
		scene.clearChildScene();
		
		
		// RESET TIMERS
		progressTimer.reset();
		colorTimer.reset();
		balloonTimer.setTimerSeconds(MathUtils.random(Constant.MIN_BALLOON_TIME, Constant.MAX_BALLOON_TIME) / balloonCounts[0]);
		balloonTimer.reset();
		bonusTimer.setTimerSeconds(MathUtils.random(Constant.MIN_BONUS_TIME, Constant.MAX_BONUS_TIME));
		bonusTimer.reset();
		skullTimer.setTimerSeconds(MathUtils.random(Constant.MIN_SKULL_TIME, Constant.MAX_SKULL_TIME));
		skullTimer.reset();
		
		balloonTypeTimer.setTimerSeconds(Constant.BALLOON_TYPE_TIMER[1]);
		balloonTypeTimer.reset();
		
		mainTimer.reset();
		
		skullModActivator.reset();

		balloonSpawnRateIndex = 0;
		balloonSpawnRateTimer.setTimerSeconds(Constant.BALLOON_SPAWN_DECREASE_TIME[balloonSpawnRateIndex]);
		balloonSpawnRateTimer.setAutoReset(false);
		balloonSpawnRateTimer.reset();

		bonusSpawnRateIndex = 0;
		bonusSpawnRateTimer.setTimerSeconds(Constant.BONUS_SPAWN_DECREASE_TIME[bonusSpawnRateIndex]);
		bonusSpawnRateTimer.setAutoReset(false);
		bonusSpawnRateTimer.reset();

		skullSpawnRateIndex = 0;
		skullSpawnRateTimer.setTimerSeconds(Constant.SKULL_SPAWN_INCREASE_TIME[skullSpawnRateIndex]);
		skullSpawnRateTimer.setAutoReset(false);
		skullSpawnRateTimer.reset();

		balloonSpeedIndex = 0;
		balloonSpeedTimer.setTimerSeconds(Constant.BALLOON_SPEED_INCREASE_TIME[balloonSpeedIndex]);
		balloonSpeedTimer.setAutoReset(false);
		balloonSpeedTimer.reset();

		bonusSpeedIndex = 0;
		bonusSpeedTimer.setTimerSeconds(Constant.BONUS_SPEED_INCREASE_TIME[bonusSpeedIndex]);
		bonusSpeedTimer.setAutoReset(false);
		bonusSpeedTimer.reset();

		skullSpeedIndex = 0;
		skullSpeedTimer.setTimerSeconds(Constant.SKULL_SPEED_INCREASE_TIME[skullSpeedIndex]);
		skullSpeedTimer.setAutoReset(false);
		skullSpeedTimer.reset();

		progressSpeedIndex = 0;
		progressSpeedTimer.setTimerSeconds(Constant.PROGRESS_SPEED_INCREASE_TIME[progressSpeedIndex]);
		progressSpeedTimer.setAutoReset(false);
		progressSpeedTimer.reset();

		fatKidIndex = 0;
		fatKidTimer.setTimerSeconds(Constant.FAT_KID_SPAWN_RATE[fatKidIndex]);
		fatKidTimer.reset();
		
		ResourceManager.getInstance().gameVariable.reset();
		
		ResourceManager.getInstance().gameSettings.game_started = false;
		ResourceManager.getInstance().gameSettings.guest_play = false;
	}
	
	@Override
	public void load()
	{
		scene.gameHUD.attachChild(gameHUD);

		startText.setVisible(true);
		scene.attachChild(startText);
		scene.attachChild(gameOverDisplay);
		if(resourceManager.gameSettings.guest_play)
		{
			if(ScoreInfo.score_name == null)
				resourceManager.activity.showB2GuestUsernameDialog();
		} else if (resourceManager.gameSettings.default_name == null)
			resourceManager.activity.showB2UsernameDialog(false);
		else
			ScoreInfo.score_name = resourceManager.gameSettings.default_name;

		scene.setOnSceneTouchListener(this);
	}
	
	@Override
	public void addToScore(int i, BalloonType type, boolean poppedByBomb)
	{
		if (!started)
			return;
		if (type != null)
		{
			if (type == currentType)
			{
				combo++;
				if (combo > topCombo)
					topCombo = combo;
				if (multiplier < Constant.COMBO_METER.length && combo >= Constant.COMBO_METER[multiplier])
				{
					scene.activateClown(multiplier);
					multiplier++;
				}
				lifeBar.lifeGain(Constant.LIFEBAR_PERCENT_BALLOON_INCREASE * (multiplier + 1) * (x2Active ? 2 : 1));
			} else if (!poppedByBomb && BalloonPoolType.getPoolType(type) != BalloonPoolType.BONUS)
			{
				combo = 0;
				multiplier = 0;
			}
			if (BalloonPoolType.getPoolType(type) == BalloonPoolType.SKULL)
			{
				lifeBar.lifeGain(poppedByBomb ? Constant.LIFEBAR_PERCENT_SKULL_DECREASE / 2 : Constant.LIFEBAR_PERCENT_SKULL_DECREASE);
			}
		}
		final int pointsToAdd = (int) (i * (multiplier + 1) * (x2Active ? 2 : 1) * (poppedByBomb ? (i > 0 ? 2 : 0.5) : 1));
		points.addToScore(pointsToAdd);
		setComboText();
	}
	
	private void setComboText()
	{
		if (x2Active)
		{
			comboText.setText(combo + "(" + (multiplier + 1) * 2 + "X)");
		} else if (multiplier == 0)
			comboText.setText("" + combo);
		else
			comboText.setText(combo + "(" + (multiplier + 1) + "X)");
	}
	
	@Override
	public void activateFreeze()
	{
		lifeBar.timeFreeze();
	}
	
	@Override
	public void activateX2()
	{
		x2BalloonTimer.reset();
		if (started && !x2Active)
		{
			x2Active = true;
			comboText.setColor(1f, 0.87f, 0f);
			setComboText();
			scene.registerUpdateHandler(x2BalloonTimer);
		}
	}
	
	@Override
	public void onBackKeyPressed()
	{
		scene.resumeBalloonLayer();
		SceneManager.getInstance().setScene(SceneType.SCENE_MENU);
	}
	
	@Override
	public boolean started()
	{
		return this.started;
	}
	
	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_GAME;
	}
	
	// ----------------------------------
	// End BaseHandler Override Methods
	// ----------------------------------
	
	// ----------------------------------
	// TODO Balloon Logic
	// ----------------------------------
	
	private void changeColor()
	{
		resourceManager.engine.runOnUpdateThread(COLOR_CHANGE_HANDLER);
	}
	
	private void spawnBalloon(BalloonPoolType type)
	{
		if (balloonCounts[type.ordinal()] == 0)
			return;
		int balloon = MathUtils.random(balloonCounts[type.ordinal()]);
		
		switch (type)
		{
			case BONUS:
				balloon = currentBonus[balloon];
				break;
			case SKULL:
				balloon = currentSkulls[balloon];
				break;
			default:
				balloon = currentBalloons[balloon];
				break;
		}
		
		scene.registerTouchArea(((BalloonSprite) scene.spawnBalloon(type, balloon)).touchArea);
	}
	
	private void addBalloon()
	{
		if (balloonCounts[0] >= 6)
			return;
		if (balloonCounts[0] > 3)
			balloonTypeTimer.setTimerSeconds(Constant.BALLOON_TYPE_TIMER[1]);
		
		int index = MathUtils.random(6 - balloonCounts[0]);
		currentBalloons[balloonCounts[0]] = remainingBalloons[index];
		// Add Corresponding Skull Balloon To Remaining Skull List
		remainingSkulls[balloonCounts[0] - balloonCounts[1]] = currentBalloons[balloonCounts[0]] + 6;
		balloonCounts[0]++;
		
		for (int i = index; i < 6 - balloonCounts[0]; i++)
		{
			remainingBalloons[i] = remainingBalloons[i + 1];
		}
		remainingBalloons[6 - balloonCounts[0]] = -1;
	}
	
	private void addSkull()
	{
		if (balloonCounts[1] >= 6)
			return;
		if (balloonCounts[1] > 3)
			balloonTypeTimer.setTimerSeconds(Constant.BALLOON_TYPE_TIMER[2]);
		else
			balloonTypeTimer.setTimerSeconds(Constant.BALLOON_TYPE_TIMER[1]);
		
		// currentBalloonCount - currentSkullCount
		// Range Is From 0 To # Above
		// Ex If 1 Skull Balloon Is Active And 3 Balloons Are Active
		// The Range Is [0,2)
		int index = MathUtils.random(balloonCounts[0] - balloonCounts[1]);
		currentSkulls[balloonCounts[1]] = remainingSkulls[index];
		balloonCounts[1]++;
		
		for (int i = index; i < balloonCounts[0] - balloonCounts[1]; i++)
		{
			remainingSkulls[i] = remainingSkulls[i + 1];
		}
		remainingSkulls[balloonCounts[0] - balloonCounts[1]] = -1;
	}
	
	private void addBonus()
	{
		if (balloonCounts[2] >= 3)
			return;
		
		balloonTypeTimer.setTimerSeconds(Constant.BALLOON_TYPE_TIMER[0]);
		
		final int index = MathUtils.random(3 - balloonCounts[2]);
		currentBonus[balloonCounts[2]] = remainingBonus[index];
		balloonCounts[2]++;
		
		for (int i = index; i < 3 - balloonCounts[2]; i++)
		{
			remainingBonus[i] = remainingBonus[i + 1];
		}
		remainingBonus[3 - balloonCounts[2]] = -1;
	}
	
	// ----------------------------------
	// End Balloon Logic
	// ----------------------------------
	
	public boolean x2Active()
	{
		return x2Active;
	}
	
	// ----------------------------------
	// TODO Game Over Display
	// ----------------------------------
	
	public void displayGameOverText()
	{
		if (!started || gameOverDisplayed)
			return;
		
		final int hour = (int) (timePassed / 3600);
		final int min = (int) ((timePassed / 60) - (hour * 60));
		final float sec = timePassed - min * 60 - hour * 3600;
		
		String time = hour > 0 ? hour + ":" : "";
		time += min > 0 ? min + ":" : "";
		time += (sec < 10 ? (min > 0 ? "0" + sec : sec ) : sec);
		
		timePassedStat.setText("Time Passed: " + time);
		pointsStat.setText("Points: " + ResourceManager.FORMATTER.format(points.getPoints()));
		comboStat.setText("Best Combo: " + topCombo);
		
		finalScore = (int) (points.getPoints() + 10 * timePassed * (topCombo+1));
		
		scoreStat.setText("Final Score: " + ResourceManager.FORMATTER.format(finalScore));
		
		gameOverDisplay.setVisible(true);
		gameOverDisplayed = true;
		started = false;

		resourceManager.engine.runOnUpdateThread(UNREGISTER_HANDLER);
		SceneManager.getInstance().setHighScores(finalScore);
		
		scene.setChildScene(replayView);
	}
	
	// ------------------------------------
	// TODO UpdateHandler Control
	// ------------------------------------
	private void registerUpdateHandlers()
	{
		resourceManager.engine.runOnUpdateThread(REGISTER_HANDLER);
	}
	
	private void unregisterUpdateHandlers()
	{
		scene.unregisterUpdateHandler(x2BalloonTimer);
		scene.unregisterUpdateHandler(colorTimer);
		scene.unregisterUpdateHandler(progressTimer);
		scene.unregisterUpdateHandler(balloonTimer);
		scene.unregisterUpdateHandler(bonusTimer);
		scene.unregisterUpdateHandler(skullTimer);
		scene.unregisterUpdateHandler(balloonTypeTimer);
		scene.unregisterUpdateHandler(mainTimer);
		scene.unregisterUpdateHandler(skullModActivator);
		
		scene.unregisterUpdateHandler(balloonSpawnRateTimer);
		scene.unregisterUpdateHandler(bonusSpawnRateTimer);
		scene.unregisterUpdateHandler(skullSpawnRateTimer);
		scene.unregisterUpdateHandler(balloonSpeedTimer);
		scene.unregisterUpdateHandler(bonusSpeedTimer);
		scene.unregisterUpdateHandler(skullSpeedTimer);
		
		scene.unregisterUpdateHandler(progressSpeedTimer);
		
		scene.unregisterUpdateHandler(fatKidTimer);
		lifeBar.unregisterScaler();
	}
	
	// ----------------------------------
	// TODO IOnSceneTouchListener
	// ----------------------------------
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
	{
		if (pSceneTouchEvent.isActionDown())
		{
			if (!started && !gameOverDisplayed)
			{
				startText.setVisible(false);
				started = true;
				scene.resumeBalloonLayer();
				registerUpdateHandlers();

				ResourceManager.getInstance().gameSettings.game_started = true;
			}
		}
		return false;
	}
	
	// ----------------------------------
	// TODO IOnMenuItemClickListener
	// ----------------------------------
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch (pMenuItem.getID())
		{
			case Constant.MENU_REPLAY:
				ResourceManager.getInstance().button_click.play();
				SceneManager.getInstance().setScene(SceneType.SCENE_GAME);
				return true;
			case Constant.MENU_VIEW:
				ResourceManager.getInstance().button_click.play();
				unload();
				SceneManager.getInstance().loadHighScoreScene(resourceManager.engine);
				return true;
			case Constant.MENU_MORE:
				AppFlood.setEventDelegate(null);
				if(!AppFlood.isConnected())
					resourceManager.activity.toastOnUiThread("Cannot Connect To AppFlood");
				AppFlood.showList(resourceManager.activity, AppFlood.LIST_ALL);
				return true;
		}
		return false;
	}
}
