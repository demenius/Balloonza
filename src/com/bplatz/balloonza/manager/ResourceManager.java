package com.bplatz.balloonza.manager;

import java.io.IOException;
import java.text.DecimalFormat;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.bplatz.balloonza.Balloonza;
import com.bplatz.balloonza.base.GameSettings;
import com.bplatz.balloonza.base.GameSettings.GameOption;
import com.bplatz.balloonza.base.GameVariable;
import com.bplatz.balloonza.custom.HighScoreDB;
import com.bplatz.balloonza.custom.ScoreInfo;
import com.bplatz.balloonza.object.Balloon;
import com.bplatz.balloonza.object.Balloon.BalloonType;
import com.bplatz.balloonza.object.pool.BalloonPool;
import com.bplatz.balloonza.object.pool.BonusPool;
import com.bplatz.balloonza.object.pool.ClockPool;
import com.bplatz.balloonza.object.pool.CloudPool;
import com.bplatz.balloonza.object.pool.ConfettiPool;
import com.bplatz.balloonza.object.pool.ExplosionPool;
import com.bplatz.balloonza.object.pool.SkullEffectPool;
import com.bplatz.balloonza.object.pool.SkullPool;

public class ResourceManager
{
	//-------------------------
	// VARIABLES
	//-------------------------
	private static ResourceManager INSTANCE;
	
	public Engine engine;
	public Balloonza activity;
	public Camera camera;
	public VertexBufferObjectManager vbom;
	public HighScoreDB highScoreDB;
	public GameVariable gameVariable;
	public GameSettings gameSettings;

	public final static DecimalFormat FORMATTER = new DecimalFormat("#,###");
	
	
	//-------------------------
	// MUSIC
	//-------------------------
	
	public Music theme;

	//-------------------------
	// TEXTURES & TEXTURE REGIONS
	//-------------------------
	
	// Splash Scene
	public ITextureRegion splash_region;
	public ITextureRegion badge_region;
	
	private BitmapTextureAtlas splashTextureAtlas;
	
	// Menu Scene
	public ITextureRegion play_region;
	public ITextureRegion options_region;
	public ITextureRegion highscores_region;
	//public ITextureRegion login_region;
	//public ITextureRegion logout_region;
	public ITextureRegion title_region;
	public ITextureRegion rate_us_region;
	public ITextureRegion guest_region;
	public ITextureRegion how_to_play_region;
	//public ITextureRegion remove_ads_region;
	
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	//Loading Scene
	public ITextureRegion loading_background_region;
	
	private BitmapTextureAtlas loadingTextureAtlas;
	
	// Game Scene
	public BalloonPool balloon_pool;
	public SkullPool skull_pool;
	public BonusPool bonus_pool;
	public CloudPool cloud_pool;
	public ExplosionPool explosion_pool;
	public ClockPool clock_pool;
	public ConfettiPool confetti_pool;
	public SkullEffectPool skull_effect_pool;
	
	// Balloon Pool Types
	public static enum BalloonPoolType
	{
		BALLOON, SKULL, BONUS;
		public static BalloonPoolType valueOf(int v)
		{
			switch(v)
			{
				case 1:
					return SKULL;
				case 2:
					return BONUS;
				default:
					return BALLOON;
			}
		}
		
		public static BalloonPoolType getPoolType(BalloonType type)
		{
			if(type.ordinal() < 6)
				return BALLOON;
			if(type.ordinal() < 12)
				return SKULL;
			return BONUS;
		}
	}
	//Particle Textures
	public BuildableBitmapTextureAtlas particleTextureAtlas;
	
	//Normal Balloons
	public ITiledTextureRegion blue_balloon_region;
	public ITiledTextureRegion green_balloon_region;
	public ITiledTextureRegion orange_balloon_region;
	public ITiledTextureRegion purple_balloon_region;
	public ITiledTextureRegion red_balloon_region;
	public ITiledTextureRegion yellow_balloon_region;

	//Bonus Balloons
	public ITiledTextureRegion bomb_balloon_region;
	public ITiledTextureRegion shrapnel_region;
	public ITiledTextureRegion time_balloon_region;
	public ITiledTextureRegion clock_region;
	public ITiledTextureRegion x2_balloon_region;
	public ITiledTextureRegion confetti_region;
	public ITiledTextureRegion skull_region;
	
	//Clouds
	public ITextureRegion cloud1_region;
	public ITextureRegion cloud2_region;
	public ITextureRegion cloud3_region;
	public ITextureRegion cloud4_region;
	public ITextureRegion cloud5_region;
	public ITextureRegion cloud6_region;
	public ITextureRegion cloud7_region;
	public ITextureRegion cloud8_region;
	
	// HUD
	public ITextureRegion balloon_backdrop_region;
	public ITiledTextureRegion mute_sound_region;
	public ITiledTextureRegion mute_music_region;
	public ITiledTextureRegion disable_vibrate_region;
	
	public ITextureRegion background_region;

	// Extras
	public ITiledTextureRegion fat_kid_region;
	public ITextureRegion clown_region;
	public ITiledTextureRegion clown_dialog_region;

	// Sliders
	public ITextureRegion on_off_slider_region;
	public ITiledTextureRegion on_off_button_region;
	
	public ITextureRegion[] how_to_play_info_region;

	public ITextureRegion heyzap_region;
	public ITextureRegion heyzap_leaderboard_region;
	public ITextureRegion heyzap_thumb_region;

	public BuildableBitmapTextureAtlas gameBalloonTextureAtlas;
	public BuildableBitmapTextureAtlas gameBonusTextureAtlas;
	public BuildableBitmapTextureAtlas gameCloudTextureAtlas;
	public BuildableBitmapTextureAtlas hudTextureAtlas;
	public BuildableBitmapTextureAtlas extrasTextureAtlas;
	public BuildableBitmapTextureAtlas uiTextureAtlas;
	public BuildableBitmapTextureAtlas howToPlayTextureAtlasA;
	public BuildableBitmapTextureAtlas howToPlayTextureAtlasB;
	public BuildableBitmapTextureAtlas howToPlayTextureAtlasC;
	public BuildableBitmapTextureAtlas highscoreTextureAtlas;
	
	public Sound[] balloon_pop_sounds;
	public Sound skull_pop;
	public Sound bomb_pop;
	public Sound clock_slow;
	public Sound clock_fast;
	public Sound button_click;
	public Sound bell;
	
	// Fonts For Scenes
	public Font small_font;
	public Font medium_font;
	public Font large_font;
	
	//-------------------------
	// CLASS LOGIC
	//-------------------------
	
	//TODO Final Disposal
	public void disposeAllGameResources()
	{
		disposeLoadingResources();
		disposeMenuResources();
		disposeHighscoreResources();
		disposeGameResources();
		disposeAudioResources();
		disposeUIResources();
		disposeFontResources();
		disposeHowToPlayResources();
	}
	
	//TODO Splash Resources
	public void createSplashResources()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 2048, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		badge_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "badge.png", 0, 800);
		splashTextureAtlas.load();
	}

	public void disposeSplashResources()
	{
		splashTextureAtlas.unload();
		splash_region = null;
		badge_region = null;
		
		splashTextureAtlas = null;
	}
	
	//TODO Loading Resources
	public void createLoadingResources()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		loadingTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 1024, TextureOptions.BILINEAR);
		loading_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(loadingTextureAtlas, activity, "loading_screen.png", 0, 0);
		this.loadingTextureAtlas.load();
		createFontResources();
	}

	private void disposeLoadingResources()
	{
		loadingTextureAtlas.unload();
		loading_background_region = null;
	}
	
	//TODO Menu Resources
	public void createMenuResources()
	{
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		highscoreTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
			
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "start_button.png");
		options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options_button.png");
		highscores_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "highscores_button.png");
		//login_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "login_button.png");
		//logout_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "logout_button.png");
		title_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "balloonza_title.png");
		rate_us_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "rate_us_button.png");
		guest_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "guest_button.png");
		how_to_play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "how_to_play_button.png");
		//remove_ads_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "remove_ads.png");

		heyzap_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highscoreTextureAtlas, activity, "heyzap.png");
		heyzap_leaderboard_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highscoreTextureAtlas, activity, "heyzap_leaderboard.png");
		heyzap_thumb_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(highscoreTextureAtlas, activity, "heyzap_thumb.png");
		
		try
		{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			this.highscoreTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			highscoreTextureAtlas.load();
		} catch (final TextureAtlasBuilderException ex)
		{
			Debug.e(ex);
		}
	}
	
	public void loadHighscoreResources()
	{
		highscoreTextureAtlas.load();
	}
	
	public void unloadHighscoreResources()
	{
		highscoreTextureAtlas.unload();
	}
	
	public void disposeHighscoreResources()
	{
		highscoreTextureAtlas.unload();
		heyzap_region = null;
		heyzap_thumb_region = null;
		heyzap_leaderboard_region = null;
		highscoreTextureAtlas = null;
	}
	
	public void loadMenuResources()
	{
		menuTextureAtlas.load();
	}
	
	public void unloadMenuResources()
	{
		menuTextureAtlas.unload();
	}
	
	private void disposeMenuResources()
	{
		menuTextureAtlas.unload();

		play_region = null;
		options_region = null;
		highscores_region = null;
		//login_region = null;
		title_region = null;
		rate_us_region = null;
		guest_region = null;
		how_to_play_region = null;
		//remove_ads_region = null;
		
		menuTextureAtlas = null;
	}
	
	//TODO Game Resources
	public void createGameResources()
	{
		createGameGraphics();
		createGameAudio();
		createUIResources();
		createHowToPlayGraphics();
		balloon_pool = new BalloonPool();
		skull_pool = new SkullPool();
		bonus_pool = new BonusPool();
		cloud_pool = new CloudPool();
	}
	
	private void createGameGraphics()
	{
		loadGameBalloons(true);
		
		gameBonusTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		gameCloudTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
		hudTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 512, TextureOptions.BILINEAR);
		particleTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 32, 32, TextureOptions.BILINEAR);
		extrasTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 512, TextureOptions.BILINEAR);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/bonus/");
		bomb_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBonusTextureAtlas, activity, "bomb_balloon.png", 4, 1);
		shrapnel_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBonusTextureAtlas, activity, "shrapnel.png", 3, 1);
		time_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBonusTextureAtlas, activity, "time_balloon.png", 4, 1);
		clock_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBonusTextureAtlas, activity, "time_balloon.png", 4, 1);
		x2_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBonusTextureAtlas, activity, "x2_balloon.png", 4, 1);
		confetti_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBonusTextureAtlas, activity, "confetti.png", 6, 3);
		skull_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBonusTextureAtlas, activity, "skull_image.png", 1, 1);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/clouds/");
		cloud1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCloudTextureAtlas, activity, "cloud_1.png");
		cloud2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCloudTextureAtlas, activity, "cloud_2.png");
		cloud3_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCloudTextureAtlas, activity, "cloud_3.png");
		cloud4_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCloudTextureAtlas, activity, "cloud_4.png");
		cloud5_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCloudTextureAtlas, activity, "cloud_5.png");
		cloud6_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCloudTextureAtlas, activity, "cloud_6.png");
		cloud7_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCloudTextureAtlas, activity, "cloud_7.png");
		cloud8_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameCloudTextureAtlas, activity, "cloud_8.png");
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/hud/");
		balloon_backdrop_region =  BitmapTextureAtlasTextureRegionFactory.createFromAsset(hudTextureAtlas, activity, "balloon_backdrop.png");

		mute_music_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hudTextureAtlas, activity, "mute_music.png", 2, 1);
		disable_vibrate_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hudTextureAtlas, activity, "vibrate_icon.png", 2, 1);
		mute_sound_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hudTextureAtlas, activity, "mute_sound.png", 2, 1);
		background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(hudTextureAtlas, activity, "background.png");
		

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/extra/");
		fat_kid_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(extrasTextureAtlas, activity, "fat_kid.png", 4, 1);
		clown_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(extrasTextureAtlas, activity, "clown.png");
		clown_dialog_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(extrasTextureAtlas, activity, "clown_dialogs.png", 4, 1);
		
		
		try
		{
			this.gameBonusTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			this.gameCloudTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			this.particleTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			this.extrasTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			this.hudTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
		} catch(final TextureAtlasBuilderException ex)
		{
			Debug.e(ex);
		}
	}
	
	private void createGameAudio()
	{
		MusicFactory.setAssetBasePath("mfx/");
		
		try {
			theme = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "theme.ogg");
			theme.setLooping(true);
		} catch (final IOException e) {
			Debug.e(e);
		}
		
		SoundFactory.setAssetBasePath("mfx/");
		this.balloon_pop_sounds = new Sound[4];
		try 
		{
			this.balloon_pop_sounds[0] = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "balloon_popping_2.ogg");
			this.balloon_pop_sounds[1] = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "balloon_popping_3.ogg");
			this.balloon_pop_sounds[2] = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "balloon_popping_4.ogg");
			this.balloon_pop_sounds[3] = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "balloon_popping_5.ogg");
			this.skull_pop = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "skull_pop.ogg");
			this.bomb_pop = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "explosion.ogg");
			this.clock_slow = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "clock_slow.ogg");
			this.clock_fast = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "clock_fast.ogg");
			this.button_click = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "button_click.ogg");
			this.bell = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "bell.ogg");
			
		} catch (final IOException e) 
		{
			Debug.e(e);
		}

		getInstance().muteMusic(gameSettings.music_muted);
		getInstance().muteSound(gameSettings.sound_muted);
		getInstance().disableVibrate(gameSettings.vibrate_disabled);
	}
	
	public void loadGameResources()
	{
		this.gameBalloonTextureAtlas.load();
		this.gameBonusTextureAtlas.load();
		this.gameCloudTextureAtlas.load();
		this.hudTextureAtlas.load();
		this.extrasTextureAtlas.load();
		this.uiTextureAtlas.load();
	}
	
	public void unloadGameResources()
	{
		this.gameBalloonTextureAtlas.unload();
		this.gameBonusTextureAtlas.unload();
		this.gameCloudTextureAtlas.unload();
		this.hudTextureAtlas.unload();
		this.extrasTextureAtlas.unload();
		this.uiTextureAtlas.unload();
	}
	
	private void disposeGameResources()
	{
		this.gameBalloonTextureAtlas.unload();
		this.gameBonusTextureAtlas.unload();
		this.gameCloudTextureAtlas.unload();
		this.hudTextureAtlas.unload();
		this.extrasTextureAtlas.unload();
		
		blue_balloon_region = null;
		green_balloon_region = null;
		orange_balloon_region = null;
		purple_balloon_region = null;
		red_balloon_region = null;
		yellow_balloon_region = null;

		bomb_balloon_region = null;
		shrapnel_region = null;
		time_balloon_region = null;
		clock_region = null;
		x2_balloon_region = null;
		confetti_region = null;
		
		cloud1_region = null;
		cloud2_region = null;
		cloud3_region = null;
		cloud4_region = null;
		cloud5_region = null;
		cloud6_region = null;
		cloud7_region = null;
		cloud8_region = null;
		
		balloon_backdrop_region = null;
		mute_music_region = null;
		disable_vibrate_region = null;
		mute_sound_region = null;
		background_region = null;
		
		fat_kid_region = null;
		clown_region = null;
		clown_dialog_region = null;

		balloon_pool.destroyPools();
		skull_pool.destroyPools();
		bonus_pool.destroyPools();
		cloud_pool.destroyPools();
		explosion_pool = null;
		clock_pool = null;
		confetti_pool = null;
		
		gameBalloonTextureAtlas = null;
		gameBonusTextureAtlas = null;
		gameCloudTextureAtlas = null;
		hudTextureAtlas = null;
		extrasTextureAtlas = null;
	}
	
	private void disposeAudioResources()
	{
		for(int i = 0; i < balloon_pop_sounds.length; i++)
		{
			balloon_pop_sounds[i].stop();
			balloon_pop_sounds[i].release();
			balloon_pop_sounds[i] = null;
		}
		
		balloon_pop_sounds = null;

		skull_pop.stop();
		bomb_pop.stop();
		clock_slow.stop();
		clock_fast.stop();
		theme.stop();

		skull_pop.release();
		bomb_pop.release();
		clock_slow.release();
		clock_fast.release();
		theme.release();
		
		skull_pop = null;
		bomb_pop = null;
		clock_slow = null;
		clock_fast = null;
		theme = null;
	}
	
	//TODO UI Resources
	private void createUIResources()
	{
		uiTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 256, 128, TextureOptions.BILINEAR);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/ui/");
		
		on_off_slider_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(uiTextureAtlas, activity, "slider.png");
		on_off_button_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(uiTextureAtlas, activity, "slider_buttons.png", 2, 1);
		
		try
		{
			this.uiTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
		} catch(final TextureAtlasBuilderException ex)
		{
			Debug.e(ex);
		}
	}
	
	private void disposeUIResources()
	{
		uiTextureAtlas.unload();
		
		on_off_slider_region = null;
		on_off_button_region = null;
		
		uiTextureAtlas = null;
	}
	
	private void createFontResources()
	{
		FontFactory.setAssetBasePath("font/");
		final ITexture smallFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture mediumFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture largeFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		small_font = FontFactory.createStrokeFromAsset(activity.getFontManager(), smallFontTexture, activity.getAssets(), "sniglet.ttf", 30, true, Color.WHITE, 3, Color.BLACK);
		small_font.load();
		medium_font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mediumFontTexture, activity.getAssets(), "sniglet.ttf", 35, true, Color.WHITE, 3, Color.BLACK);
		medium_font.load();
		large_font = FontFactory.createStrokeFromAsset(activity.getFontManager(), largeFontTexture, activity.getAssets(), "sniglet.ttf", 45, true, Color.WHITE, 3, Color.BLACK);
		large_font.load();
	}
	
	private void disposeFontResources()
	{
		small_font.unload();
		medium_font.unload();
		large_font.unload();
		
		small_font = null;
		medium_font = null;
		large_font = null;
	}
	
	//TODO HowToPlay Resources
	private void createHowToPlayGraphics()
	{
		howToPlayTextureAtlasA = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 2048, TextureOptions.BILINEAR);
		howToPlayTextureAtlasB = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 2048, TextureOptions.BILINEAR);
		howToPlayTextureAtlasC = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 2048, TextureOptions.BILINEAR);
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/how_to_play/");
		
		how_to_play_info_region = new ITextureRegion[6];
		how_to_play_info_region[0] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(howToPlayTextureAtlasA, activity, "how_to_play_screen_a.jpg");
		how_to_play_info_region[1] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(howToPlayTextureAtlasA, activity, "how_to_play_screen_b.jpg");
		how_to_play_info_region[2] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(howToPlayTextureAtlasB, activity, "how_to_play_screen_c.jpg");
		how_to_play_info_region[3] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(howToPlayTextureAtlasB, activity, "how_to_play_screen_d.jpg");
		how_to_play_info_region[4] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(howToPlayTextureAtlasC, activity, "how_to_play_screen_e.jpg");
		how_to_play_info_region[5] = BitmapTextureAtlasTextureRegionFactory.createFromAsset(howToPlayTextureAtlasC, activity, "how_to_play_screen_f.jpg");
		
		try
		{
			this.howToPlayTextureAtlasA.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			this.howToPlayTextureAtlasB.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			this.howToPlayTextureAtlasC.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
		} catch(final TextureAtlasBuilderException ex)
		{
			Debug.e(ex);
		}
	}
	
	public void loadHowToPlayResources()
	{
		this.howToPlayTextureAtlasA.load();
		this.howToPlayTextureAtlasB.load();
		this.howToPlayTextureAtlasC.load();
	}
	
	public void unloadHowToPlayResources()
	{
		this.howToPlayTextureAtlasA.unload();
		this.howToPlayTextureAtlasB.unload();
		this.howToPlayTextureAtlasC.unload();
	}
	
	private void disposeHowToPlayResources()
	{
		this.howToPlayTextureAtlasA.unload();
		this.howToPlayTextureAtlasB.unload();
		this.howToPlayTextureAtlasC.unload();
		
		for(int i = 0; i < 5; i++)
			how_to_play_info_region[i] = null;
		how_to_play_info_region = null;
		
		howToPlayTextureAtlasA = null;
		howToPlayTextureAtlasB = null;
		howToPlayTextureAtlasC = null;
	}
	
	//TODO OTHER
	
	public void createExplosionPool()
	{
		explosion_pool = new ExplosionPool(15);
		clock_pool = new ClockPool(15);
		confetti_pool = new ConfettiPool(20);
		skull_effect_pool = new SkullEffectPool(15);
	}
	
	public void recycleBalloon(BalloonType type, Balloon balloon)
	{
		BalloonPoolType poolType = BalloonPoolType.getPoolType(type);
		if(balloon.isRecycled()) //TODO DIRTY. Find Out Why It Is Already Recycled
			return;
		switch(poolType)
		{
			case BALLOON:
				balloon_pool.recyclePoolItem(type.ordinal(), balloon);
				break;
			case BONUS:
				bonus_pool.recyclePoolItem(type.ordinal(), balloon);
				break;
			case SKULL:
				skull_pool.recyclePoolItem(type.ordinal(), balloon);
				break;
			default:
				break;
				
		}
	}
	
	public void useNormalBalloons(boolean normal)
	{
		if(normal && gameSettings.gameOption == GameOption.COLOR_BLIND)
		{
			gameSettings.gameOption = GameOption.NORMAL;
			loadGameBalloons(false);
		} else if(!normal && gameSettings.gameOption == GameOption.NORMAL)
		{
			gameSettings.gameOption = GameOption.COLOR_BLIND;
			loadGameBalloons(false);
		}
	}
	
	private void loadGameBalloons(boolean firstLoad)
	{
		if(firstLoad)
			gameBalloonTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 2048, TextureOptions.BILINEAR);
		else
			gameBalloonTextureAtlas.clearTextureAtlasSources();

		if(gameSettings.gameOption == GameOption.COLOR_BLIND)
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/balloons_b/");
		else
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/balloons/");
		
		blue_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBalloonTextureAtlas, activity, "blue_balloon.png", 4, 1);
		green_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBalloonTextureAtlas, activity, "green_balloon.png", 4, 1);
		orange_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBalloonTextureAtlas, activity, "orange_balloon.png", 4, 1);
		purple_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBalloonTextureAtlas, activity, "purple_balloon.png", 4, 1);
		red_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBalloonTextureAtlas, activity, "red_balloon.png", 4, 1);
		yellow_balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameBalloonTextureAtlas, activity, "yellow_balloon.png", 4, 1);

		try
		{
			this.gameBalloonTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 1, 2));
			this.gameBalloonTextureAtlas.load();
		} catch(final TextureAtlasBuilderException ex)
		{
			Debug.e(ex);
		}
	}
	
	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br><br>
	 * We Use This Method At The Beginning For Game Loading, To Prepare Resource Manager Properly
	 * Sets All Needed Parameters So We Can Later Access Them From Different Classes (eg. Scenes)
	 */
	public static void prepareManager(Engine engine, Balloonza activity, Camera camera, VertexBufferObjectManager vbom, HighScoreDB highScoreDB, GameVariable gameVariable, GameSettings gameSettings)
	{
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
		getInstance().highScoreDB = highScoreDB;
		getInstance().gameVariable = gameVariable;
		getInstance().gameSettings = gameSettings;
	}

	//-------------------------
	// GETTERS AND SETTERS
	//-------------------------
	public void disableVibrate(boolean disable)
	{
		gameSettings.vibrate_disabled = disable;
		if(SceneManager.loaded)
			SceneManager.getInstance().getLogicScene().setVibrate(!disable);
	}
	
	public void muteSound(boolean mute)
	{
		gameSettings.sound_muted = mute;
		engine.getSoundManager().setMasterVolume(mute ? 0.0f : 1.0f);
		if(SceneManager.loaded)
			SceneManager.getInstance().getLogicScene().setSound(!mute);
	}
	
	public void muteMusic(boolean mute)
	{
		gameSettings.music_muted = mute;
		engine.getMusicManager().setMasterVolume(mute ? 0.0f : 1.0f);
		if(SceneManager.loaded)
			SceneManager.getInstance().getLogicScene().setMusic(!mute);
	}
	
	public void setDefaultUsername(final String username)
	{
		ScoreInfo.score_name = username;
		gameSettings.default_name = username;
	}
	
	public static ResourceManager getInstance()
	{
		return INSTANCE;
	}
	
	public static void create()
	{
		SceneManager.loaded = false;
		INSTANCE = new ResourceManager();
	}
}
