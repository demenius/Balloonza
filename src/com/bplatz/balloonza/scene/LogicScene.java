package com.bplatz.balloonza.scene;

import java.text.DecimalFormat;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;
import org.json.JSONObject;

import com.appflood.AppFlood;
import com.appflood.AppFlood.AFEventDelegate;
import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.base.BaseHandler;
import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.handler.GameHandler;
import com.bplatz.balloonza.handler.MenuHandler;
import com.bplatz.balloonza.handler.OptionHandler;
import com.bplatz.balloonza.manager.ResourceManager.BalloonPoolType;
import com.bplatz.balloonza.manager.SceneManager.SceneType;
import com.bplatz.balloonza.object.Balloon;
import com.bplatz.balloonza.object.Balloon.BalloonSprite;
import com.bplatz.balloonza.object.Balloon.BalloonType;
import com.bplatz.balloonza.object.Clown;
import com.bplatz.balloonza.object.FatKid;
import com.bplatz.balloonza.object.effect.Confetti;

//----------------------------
//TODO Setup Mute Buttons
//----------------------------

public class LogicScene extends BaseScene
{
	private TimerHandler cloudTimer;
	public final static DecimalFormat FORMATTER = new DecimalFormat("#,###.##");
	
	public HUD gameHUD;
	private TiledSprite muteSound;
	private TiledSprite muteMusic;
	private TiledSprite disableVibrate;
	
	private FatKid fatKid;
	private Clown clown;
	
	private BaseSprite background;
	private Entity cloudLayer;
	private Entity balloonLayer;
	
	private GameHandler gameHandler;
	private MenuHandler menuHandler;
	private OptionHandler optionHandler;
	
	private BaseHandler currentHandler;
	
	private final Runnable GOTOGAME = new Runnable()
	{
		
		@Override
		public void run()
		{
			if (!resourceManager.gameSettings.game_started && AppFlood.isConnected())
			{
				AppFlood.setEventDelegate(new AFEventDelegate()
				{
					
					@Override
					public void onClick(JSONObject arg0)
					{
					}
					
					@Override
					public void onClose(JSONObject arg0)
					{
						resourceManager.engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
						{
							
							@Override
							public void onTimePassed(TimerHandler pTimerHandler)
							{
								resourceManager.engine.unregisterUpdateHandler(pTimerHandler);
								currentHandler.unload();
								currentHandler = gameHandler;
								currentHandler.load();
							}
							
						}));
					}

					@Override
					public void onFinish(boolean arg0, int arg1)
					{
						// TODO Auto-generated method stub
						
					}
					
				});
				AppFlood.showFullScreen(resourceManager.activity);
			} else
			{
				currentHandler.unload();
				currentHandler = gameHandler;
				currentHandler.load();
			}
		}
	};
	
	private final Runnable GOTOMENU = new Runnable()
	{

		@Override
		public void run()
		{
			currentHandler.unload();
			currentHandler = menuHandler;
			currentHandler.load();
		}
		
	};
	
	private final Runnable GOTOOPTION = new Runnable()
	{

		@Override
		public void run()
		{
			currentHandler.unload();
			currentHandler = optionHandler;
			currentHandler.load();
		}
		
	};
	
	public void displayGameOverText()
	{
		gameHandler.displayGameOverText();
	}
	
	public void goToMenu()
	{
		resourceManager.engine.runOnUpdateThread(GOTOMENU);
	}
	
	public void goToGame()
	{
		resourceManager.engine.runOnUpdateThread(GOTOGAME);
	}
	
	public void goToOptions()
	{
		resourceManager.engine.runOnUpdateThread(GOTOOPTION);
	}
	
	public void resetGame()
	{
		gameHandler.resetGame();
	}
	
	private void loadLevel()
	{
		resourceManager.balloon_pool.initiatePool(vbom, this);
		resourceManager.skull_pool.initiatePool(vbom, this);
		resourceManager.bonus_pool.initiatePool(vbom, this);
		resourceManager.cloud_pool.initiatePool(vbom, this);
		
		gameHandler = new GameHandler(this);
		gameHandler.create();
		menuHandler = new MenuHandler(this);
		menuHandler.create();
		optionHandler = new OptionHandler(this);
		optionHandler.create();
		
		cloudTimer = new TimerHandler(0.4f, true, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				Sprite c = resourceManager.cloud_pool.obtainPoolItem(MathUtils.random(8)).getSprite();
				if (c.getParent() == null)
					cloudLayer.attachChild(c);
			}
		});
		
		registerUpdateHandler(cloudTimer);
		currentHandler = menuHandler;
	}
	
	private void createBackground()
	{
		setBackground(new Background(Color.BLUE));
		
		background = new BaseSprite(0, 0, resourceManager.background_region, vbom);
		background.setPosition(background.getWidth() / 2, background.getHeight() / 2);
		cloudLayer = new Entity();
		balloonLayer = new Entity();
		fatKid = new FatKid(240, 400, resourceManager.fat_kid_region, vbom, this);
		clown = new Clown(400, 125, resourceManager.clown_region, vbom);
		
		attachChild(background);
		attachChild(cloudLayer);
		attachChild(balloonLayer);
		attachChild(fatKid);
		attachChild(clown);
	}
	
	private void createMuteButtons()
	{
		gameHUD = new HUD();
		
		muteSound = new TiledSprite(460, 780, resourceManager.mute_sound_region, resourceManager.vbom)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.isActionUp())
				{
					final boolean mute = resourceManager.engine.getSoundManager().getMasterVolume() > 0.0f;
					resourceManager.muteSound(mute);
					this.setCurrentTileIndex(mute ? 1 : 0);
					return true;
				}
				return false;
			}
			
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		muteSound.setCurrentTileIndex(resourceManager.gameSettings.sound_muted ? 1 : 0);
		
		muteMusic = new TiledSprite(420, 780, resourceManager.mute_music_region, resourceManager.vbom)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.isActionUp())
				{
					final boolean mute = resourceManager.engine.getMusicManager().getMasterVolume() > 0.0f;
					resourceManager.muteMusic(mute);
					this.setCurrentTileIndex(mute ? 1 : 0);
					return true;
				}
				return false;
			}
			
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		muteMusic.setCurrentTileIndex(resourceManager.gameSettings.music_muted ? 1 : 0);
		
		disableVibrate = new TiledSprite(380, 780, resourceManager.disable_vibrate_region, resourceManager.vbom)
		{
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
			{
				if (pSceneTouchEvent.isActionUp())
				{
					final boolean disable = !resourceManager.gameSettings.vibrate_disabled;
					resourceManager.disableVibrate(disable);
					this.setCurrentTileIndex(disable ? 1 : 0);
					return true;
				}
				return false;
			}
			
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		disableVibrate.setCurrentTileIndex(resourceManager.gameSettings.vibrate_disabled ? 1 : 0);
		
		gameHUD.attachChild(muteSound);
		gameHUD.attachChild(muteMusic);
		gameHUD.attachChild(disableVibrate);
		
		this.registerTouchArea(muteMusic);
		this.registerTouchArea(muteSound);
		this.registerTouchArea(disableVibrate);
	}
	
	@Override
	public void createScene()
	{
		createBackground();
		createMuteButtons();
		loadLevel();
		this.setOnAreaTouchTraversalFrontToBack();
	}
	
	@Override
	public void onBackKeyPressed()
	{
		currentHandler.onBackKeyPressed();
	}
	
	@Override
	public SceneType getSceneType()
	{
		return currentHandler.getSceneType();
	}
	
	/*
	 * public void updateFacebookButton() { menuHandler.updateFacebookButton();
	 * }
	 */
	
	public Sprite spawnBalloon(BalloonPoolType type, int balloon)
	{
		Sprite s;
		switch (type)
		{
			case SKULL:
				s = resourceManager.skull_pool.obtainPoolItem(balloon).getSprite();
				break;
			case BONUS:
				s = resourceManager.bonus_pool.obtainPoolItem(balloon).getSprite();
				break;
			
			default:
				s = resourceManager.balloon_pool.obtainPoolItem(balloon).getSprite();
				break;
		}
		if (s.getParent() == null)
			balloonLayer.attachChild(s);
		return s;
	}
	
	public boolean spawnFatKid()
	{
		if(fatKid.activate())
		{
			this.registerTouchArea(fatKid);
			return true;
		}
		return false;
	}
	
	public void resetFatKid()
	{
		this.unregisterTouchArea(fatKid);
		gameHandler.resetFatKid();
	}
	
	public void activateClown(int dialog)
	{
		clown.activate(dialog);
	}
	
	public void registerMenuModifiers()
	{
		menuHandler.registerMenuModifiers();
	}
	
	public void setDefaultUsername(final String username)
	{
		optionHandler.setDefaultUsername(username);
	}
	
	public void clearBalloonLayer()
	{
		final int count = balloonLayer.getChildCount();
		for (int i = 0; i < count; i++)
		{
			IEntity e = balloonLayer.getChildByIndex(i);
			((BalloonSprite) e).balloon.pop(false, false);
		}
	}
	
	public void activateFreeze()
	{
		this.gameHandler.activateFreeze();
	}
	
	public void activateX2()
	{
		this.currentHandler.activateX2();
	}
	
	public boolean collisionCheck(final IEntity e)
	{
		final int count = balloonLayer.getChildCount();
		for (int i = 0; i < count; i++)
		{
			IEntity b = balloonLayer.getChildByIndex(i);
			
			/*
			 * final float bWidth = b.getWidth(); final float bHeight =
			 * b.getHeight(); final float bX = b.getX(); final float bY =
			 * b.getY();
			 * 
			 * final float eWidth = e.getWidth() * e.getScaleX(); final float
			 * eHeight = e.getHeight() * e.getScaleY(); final float eX =
			 * e.getX(); final float eY = e.getY();
			 * 
			 * if(bX + bWidth < eX) continue; if(bX > eX + eWidth) continue;
			 * if(bY + bHeight < eY) continue; if(bY > eY + eHeight) continue;
			 */
			if (e.collidesWith(b))
			{
				final String userData = e.getUserData().toString();
				if (userData.equals("SHRAPNEL"))
				{
					if (((Balloon) b.getUserData()).pop(true, false))
					{
						return true;
					}
				} else
				{
					if (((Balloon) b.getUserData()).pop(false, true))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public void activateBomb(final float x, final float y)
	{
		BaseExplosion e = resourceManager.explosion_pool.obtainPoolItem();
		if (!resourceManager.gameSettings.vibrate_disabled)
			resourceManager.engine.vibrate(250);
		e.activate(x, y);
	}
	
	public void activateSkull(final float x, final float y)
	{
		BaseExplosion e = resourceManager.skull_effect_pool.obtainPoolItem();
		e.activate(x, y);
	}
	
	public void activateConfetti(final float x, final float y, BalloonType type)
	{
		Confetti e = (Confetti) resourceManager.confetti_pool.obtainPoolItem();
		e.activateConfetti(x, y, type);
	}
	
	public void activateClock(final float x, final float y)
	{
		BaseExplosion e = resourceManager.clock_pool.obtainPoolItem();
		e.activate(x, y);
	}
	
	public boolean x2Active()
	{
		return gameHandler.x2Active();
	}
	
	public void pauseBalloonLayer()
	{
		resourceManager.engine.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				final int count = balloonLayer.getChildCount();
				for (int i = 0; i < count; i++)
				{
					IEntity e = balloonLayer.getChildByIndex(i);
					((BalloonSprite) e).balloon.pauseBalloon();
				}
			}
		});
		fatKid.pause();
	}
	
	public void resumeBalloonLayer()
	{
		resourceManager.engine.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				final int count = balloonLayer.getChildCount();
				for (int i = 0; i < count; i++)
				{
					IEntity e = balloonLayer.getChildByIndex(i);
					((BalloonSprite) e).balloon.resumeBalloon();
				}
			}
		});
		fatKid.resume();
	}
	
	public boolean started()
	{
		return this.currentHandler.started();
	}
	
	public void setVibrate(final boolean enabled)
	{
		optionHandler.setVibrateSlider(enabled);
		disableVibrate.setCurrentTileIndex(enabled ? 0 : 1);
	}
	
	public void setSound(final boolean enabled)
	{
		optionHandler.setSoundSlider(enabled);
		muteSound.setCurrentTileIndex(enabled ? 0 : 1);
	}
	
	public void setMusic(final boolean enabled)
	{
		optionHandler.setMusicSlider(enabled);
		muteMusic.setCurrentTileIndex(enabled ? 0 : 1);
	}
	
	@Override
	public void disponeScene()
	{
		// TODO
		camera.setHUD(null);
		camera.setCenter(240, 400);
		
		this.menuHandler.disposeScene();
		this.gameHandler.disposeScene();
		this.optionHandler.disposeScene();
		
		menuHandler = null;
		gameHandler = null;
		optionHandler = null;
		currentHandler = null;
		
		this.engine.unregisterUpdateHandler(cloudTimer);
		
		gameHUD.detachSelf();
		gameHUD.dispose();
		gameHUD = null;
		
		fatKid.remove();
		fatKid = null;
		clown.remove();
		clown = null;
		
		background.detachSelf();
		background.dispose();
		background = null;
		
		cloudLayer.detachSelf();
		cloudLayer.dispose();
		cloudLayer = null;
		
		balloonLayer.detachSelf();
		balloonLayer.dispose();
		balloonLayer = null;
		
		this.detachSelf();
		this.dispose();
	}
	
	public void addToScore(BalloonType type, boolean poppedByBomb)
	{
		if (type == null)
			currentHandler.addToScore(Constant.FAT_KID_POINTS, type, poppedByBomb);
		else if (BalloonPoolType.getPoolType(type) == BalloonPoolType.SKULL)
			currentHandler.addToScore(Constant.SKULL_POINTS, type, poppedByBomb);
		else if (BalloonPoolType.getPoolType(type) == BalloonPoolType.BONUS)
			currentHandler.addToScore(Constant.BONUS_POINTS, type, poppedByBomb);
		else
			currentHandler.addToScore(Constant.BALLOON_POINTS, type, poppedByBomb);
	}
	
	public void pause()
	{
		currentHandler.pause();
	}
}
