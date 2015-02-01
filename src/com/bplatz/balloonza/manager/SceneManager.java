package com.bplatz.balloonza.manager;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.scene.HighScoreScene;
import com.bplatz.balloonza.scene.HowToPlayScene;
import com.bplatz.balloonza.scene.LoadingScene;
import com.bplatz.balloonza.scene.LogicScene;
import com.bplatz.balloonza.scene.SplashScene;

public class SceneManager
{
	//-------------------------
	// SCENES
	//-------------------------
	
	private BaseScene splashScene;
	private LogicScene logicScene; // Menu/Game/Option
	private BaseScene loadingScene;
	private HighScoreScene highScoreScene;
	private HowToPlayScene howToPlayScene;
	
	public static boolean loaded = false;

	//-------------------------
	// VARIABLES
	//-------------------------
	
	private static SceneManager INSTANCE;
	
	private SceneType currentSceneType = SceneType.SCENE_SPLASH;
	
	private BaseScene currentScene;
	
	private Engine engine = ResourceManager.getInstance().engine;
	
	public enum SceneType
	{
		SCENE_SPLASH,
		SCENE_MENU,
		SCENE_GAME,
		SCENE_LOADING,
		SCENE_HIGHSCORE,
		SCENE_OPTION,
		SCENE_HOW_TO_PLAY
	}
	
	//---------------------------
	// SCENE CREATION
	//---------------------------
	
	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
	{
		ResourceManager.getInstance().createSplashResources();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}
	
	public void createLoadingScene()
	{
		ResourceManager.getInstance().createLoadingResources();
		loadingScene = new LoadingScene();
		setScene(loadingScene);
	}
	
	public void createMenuScene()
	{		
		disposeSplashScene();
		ResourceManager.getInstance().createMenuResources();
		ResourceManager.getInstance().createGameResources();
		logicScene = new LogicScene();
		ResourceManager.getInstance().createExplosionPool();
		logicScene.registerMenuModifiers();
		highScoreScene = new HighScoreScene();
		howToPlayScene = new HowToPlayScene();
		if(ResourceManager.getInstance().activity.isFirstLoad())
			loadHowToPlayScene(ResourceManager.getInstance().engine);
		else
		{
			ResourceManager.getInstance().engine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
			{
				public void onTimePassed(final TimerHandler pTimerHandler)
				{
					ResourceManager.getInstance().loadGameResources();
					ResourceManager.getInstance().loadMenuResources();
					ResourceManager.getInstance().camera.setHUD(logicScene.gameHUD);
					setScene(logicScene);
				}
			}));
		}
		loaded = true;
	}
	
	public void loadHowToPlayScene(final Engine mEngine)
	{
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				setScene(SceneType.SCENE_LOADING);
				ResourceManager.getInstance().unloadGameResources();
				ResourceManager.getInstance().unloadMenuResources();
				ResourceManager.getInstance().camera.setHUD(null);
				ResourceManager.getInstance().loadHowToPlayResources();
				setScene(SceneType.SCENE_HOW_TO_PLAY);
			}
		}));
	}
	
	public void unloadHowToPlayScene(final Engine mEngine)
	{
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				setScene(SceneType.SCENE_LOADING);
				((SmoothCamera)ResourceManager.getInstance().camera).setCenterDirect(240, 400);
				ResourceManager.getInstance().unloadHowToPlayResources();
				ResourceManager.getInstance().loadGameResources();
				ResourceManager.getInstance().loadMenuResources();
				ResourceManager.getInstance().camera.setHUD(logicScene.gameHUD);
				setScene(logicScene);
				if(logicScene.getSceneType() != SceneType.SCENE_MENU)
				{
					setScene(SceneType.SCENE_MENU);
				}
			}
		}));
	}
	
	public void loadHighScoreScene(final Engine mEngine)
	{
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				setScene(SceneType.SCENE_LOADING);
				ResourceManager.getInstance().unloadGameResources();
				ResourceManager.getInstance().unloadMenuResources();
				//ResourceManager.getInstance().loadHighscoreResources();
				ResourceManager.getInstance().camera.setHUD(null);
				ResourceManager.getInstance().highScoreDB.open();
				highScoreScene.loadHighScoreTables();
				setScene(SceneType.SCENE_HIGHSCORE);
			}
		}));
	}
	
	public void unloadHighScoreScene(final Engine mEngine)
	{
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				setScene(SceneType.SCENE_LOADING);
				//ResourceManager.getInstance().unloadHighscoreResources();
				ResourceManager.getInstance().loadGameResources();
				ResourceManager.getInstance().loadMenuResources();
				ResourceManager.getInstance().camera.setHUD(logicScene.gameHUD);
				setScene(logicScene);
				if(logicScene.getSceneType() != SceneType.SCENE_MENU)
				{
					setScene(SceneType.SCENE_MENU);
				}
			}
		}));
	}
	
	public void pauseLogicScene()
	{
		if(currentSceneType == SceneType.SCENE_GAME)
		{
			logicScene.pause();
		}
	}
	
	public void goToMenu()
	{
		if(currentSceneType == SceneType.SCENE_HIGHSCORE)
			unloadHighScoreScene(ResourceManager.getInstance().engine);
		else if(currentSceneType == SceneType.SCENE_HOW_TO_PLAY)
			unloadHowToPlayScene(ResourceManager.getInstance().engine);
		else if(currentSceneType == SceneType.SCENE_OPTION)
			setScene(SceneType.SCENE_MENU);
	}
	
	//-------------------------
	// SCENE DISPOSAL
	//-------------------------
	
	public void disposeSplashScene()
	{
		ResourceManager.getInstance().disposeSplashResources();
		splashScene.disponeScene();
		splashScene = null;
	}
	
	public void disposeAllGameResources()
	{
		ResourceManager.getInstance().disposeAllGameResources();
		logicScene.disponeScene();
		logicScene = null;
		loadingScene.disponeScene();
		loadingScene = null;
		highScoreScene.disponeScene();
		highScoreScene = null;
		howToPlayScene.disponeScene();
		howToPlayScene = null;
	}
	
	// Activators
	
	public void activateFreeze()
	{
		logicScene.activateFreeze();
	}
	
	public void activateX2()
	{
		logicScene.activateX2();
	}
	
	public void displayGameOverText()
	{
		logicScene.displayGameOverText();
	}
	
	// -------------------
	// SETTERS/GETTERS
	// -------------------
	
	public void setScene(BaseScene scene)
	{
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}
	
	public void setScene(SceneType sceneType)
	{
		switch(sceneType)
		{
			case SCENE_MENU:
				logicScene.goToMenu();
				currentSceneType = SceneType.SCENE_MENU;
				break;
			case SCENE_GAME:
				logicScene.goToGame();
				currentSceneType = SceneType.SCENE_GAME;
				break;
			case SCENE_SPLASH:
				setScene(splashScene);
				break;
			case SCENE_LOADING:
				setScene(loadingScene);
				break;
			case SCENE_HIGHSCORE:
				setScene(highScoreScene);
				break;
			case SCENE_OPTION:
				logicScene.goToOptions();
				currentSceneType = SceneType.SCENE_OPTION;
				break;
			case SCENE_HOW_TO_PLAY:
				setScene(howToPlayScene);
			default:
				break;
		}
	}
	
	public void setDefaultUsername()
	{
		logicScene.setDefaultUsername(ResourceManager.getInstance().gameSettings.default_name);
	}
	
	public int setHighScores(final int score)
	{
		return highScoreScene.setHighScore(score);
	}
	
	public boolean gameStarted()
	{
		return logicScene.started();
	}
	
	public LogicScene getLogicScene()
	{
		return logicScene;
	}

	public BaseScene getCurrentScene()
	{
		return currentScene;
	}
	
	public SceneType getCurrentSceneType()
	{
		return currentSceneType;
	}
	
	public static SceneManager getInstance()
	{
		return INSTANCE;
	}
	
	public static void create()
	{
		INSTANCE = new SceneManager();
	}
}
