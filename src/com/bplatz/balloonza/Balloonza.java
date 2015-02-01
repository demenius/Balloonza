package com.bplatz.balloonza;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.appflood.AppFlood;
import com.appflood.AppFlood.AFEventDelegate;
import com.appflood.AppFlood.AFRequestDelegate;

import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.base.GameSettings;
import com.bplatz.balloonza.base.GameSettings.GameOption;
import com.bplatz.balloonza.base.GameVariable;
import com.bplatz.balloonza.custom.HighScoreDB;
import com.bplatz.balloonza.custom.ScoreInfo;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.ui.B2Dialog;
import com.bplatz.ui.B2Dialog.B2DialogListener;
import com.bplatz.ui.B2UsernameDialog;
import com.bplatz.ui.B2UsernameDialog.B2UsernameDialogListener;

public class Balloonza extends BaseGameActivity implements B2UsernameDialogListener
{
	public static final String PREFS_NAME = "***.***.***";
	private final String AF_APP_KEY = "***************";
	private final String AF_APP_SECRET = "******************";
	
	private final static int MAX_FPS = 40;
	
	private ResourceManager resourceManager;
	private Camera camera;
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions)
	{
		return new LimitedFPSEngine(pEngineOptions, MAX_FPS);
	}
	
	@Override
	public EngineOptions onCreateEngineOptions()
	{
		camera = new SmoothCamera(0, 0, Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT, 1250, 0, 0);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.camera);
		engineOptions.getAudioOptions().getSoundOptions().setMaxSimultaneousStreams(10);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		return engineOptions;
	}
	
	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
	{
		ResourceManager.create();
		
		GameSettings gameSettings = new GameSettings();
		SharedPreferences settings = getSharedPreferences(Balloonza.PREFS_NAME, MODE_PRIVATE);
		
		gameSettings.default_name = settings.getString("default_name", null);
		gameSettings.gameOption = settings.getBoolean("use_symbols", false) ? GameOption.COLOR_BLIND : GameOption.NORMAL;
		
		gameSettings.sound_muted = settings.getBoolean("sound_muted", false);
		gameSettings.music_muted = settings.getBoolean("music_muted", false);
		gameSettings.vibrate_disabled = settings.getBoolean("vibrate_disabled", false);
		
		ResourceManager
				.prepareManager(mEngine, this, camera, getVertexBufferObjectManager(), new HighScoreDB(this), new GameVariable(), gameSettings);
		SceneManager.create();
		resourceManager = ResourceManager.getInstance();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}
	
	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
	{
		mEngine.enableVibrator(this);
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}
	
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
	{
		mEngine.registerUpdateHandler(new TimerHandler(3.5f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				// Create Loading Scene To Load Game Resources
				SceneManager.getInstance().createLoadingScene();
				
				mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback()
				{
					public void onTimePassed(final TimerHandler pTimerHandler)
					{
						ResourceManager.getInstance().engine.unregisterUpdateHandler(pTimerHandler);
						// Load Game Resources, Create Menu Scene
						SceneManager.getInstance().createMenuScene();
					}
				}));
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	protected void onCreate(Bundle pSavedInstanceState)
	{
		super.onCreate(pSavedInstanceState);
		AppFlood.initialize(this, AF_APP_KEY, AF_APP_SECRET, AppFlood.AD_ALL);
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		
		launchGameIfChallenged(intent);
	}
	
	private void launchGameIfChallenged(Intent intent)
	{
		if (intent != null && intent.hasExtra("level"))
		{
			SceneManager.getInstance().goToMenu();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if (this.isGameLoaded() && resourceManager.theme != null && !resourceManager.theme.isPlaying())
			resourceManager.theme.play();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if (this.isGameLoaded())
		{
			
			if (resourceManager.theme != null && resourceManager.theme.isPlaying())
				resourceManager.theme.pause();
			SceneManager.getInstance().pauseLogicScene();
			
			final GameSettings gameSettings = ResourceManager.getInstance().gameSettings;
			
			SharedPreferences settings = getSharedPreferences(Balloonza.PREFS_NAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			
			// Necessary to clear first if we save preferences onPause.
			editor.clear();
			
			editor.putString("default_name", gameSettings.default_name);
			editor.putBoolean("use_symbols", gameSettings.gameOption == GameOption.COLOR_BLIND);
			
			editor.putBoolean("sound_muted", gameSettings.sound_muted);
			editor.putBoolean("music_muted", gameSettings.music_muted);
			editor.putBoolean("vibrate_disabled", gameSettings.vibrate_disabled);
			
			editor.putBoolean("is_first_load", false);
			
			final boolean committed = editor.commit();
			Log.d(PREFS_NAME, "Settings Commited: " + committed);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		if (this.isGameLoaded())
		{
			ResourceManager.getInstance().disposeAllGameResources();
			System.gc();
		}
		AppFlood.destroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
		return false;
	}
	
	/*
	 * public void startFacebook() { if (useFacebook) this.facebookLogout();
	 * else this.facebookLogin(); }
	 */
	
	public String getXmlFromFile(String filename)
	{
		StringBuffer buff = new StringBuffer();
		File root = Environment.getExternalStorageDirectory();
		try
		{
			File xml = new File(root, "pathToYourFile");
			BufferedReader reader = new BufferedReader(new FileReader(xml));
			String line = null;
			
			while ((line = reader.readLine()) != null)
			{
				buff.append(line).append("\n");
			}
			
			reader.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buff.toString();
	}
	
	public void showB2UsernameDialog(final boolean inOptionMenu)
	{
		FragmentManager fm = this.getFragmentManager();
		B2UsernameDialog b2Dialog = new B2UsernameDialog();
		Bundle b = new Bundle();
		b.putBoolean("in_option_menu", inOptionMenu);
		b.putBoolean("guest", false);
		b2Dialog.setArguments(b);
		
		b2Dialog.show(fm, "b2_username_dialog");
	}
	
	public void showB2GuestUsernameDialog()
	{
		FragmentManager fm = this.getFragmentManager();
		B2UsernameDialog b2Dialog = new B2UsernameDialog();
		Bundle b = new Bundle();
		b.putBoolean("in_option_menu", false);
		b.putBoolean("guest", true);
		b2Dialog.setArguments(b);
		
		b2Dialog.show(fm, "b2_username_dialog");
	}
	
	public void showQuitMenu()
	{
		FragmentManager fm = this.getFragmentManager();
		B2Dialog b2Dialog = new B2Dialog();
		b2Dialog.setListener(new B2DialogListener()
		{
			@Override
			public void onPositiveClick()
			{
				finish();
			}
			
		});
		b2Dialog.setMessage("Are you sure you want to quit?");
		b2Dialog.show(fm, "b2_dialog");
	}
	
	public void showResetMenu()
	{
		FragmentManager fm = this.getFragmentManager();
		B2Dialog b2Dialog = new B2Dialog();
		b2Dialog.setListener(new B2DialogListener()
		{
			@Override
			public void onPositiveClick()
			{
				resourceManager.highScoreDB.clearTables();
			}
			
		});
		b2Dialog.setMessage("Are you sure you want to reset highscores?");
		b2Dialog.show(fm, "b2_dialog");
	}
	
	public boolean isFirstLoad()
	{
		SharedPreferences settings = getSharedPreferences(Balloonza.PREFS_NAME, MODE_PRIVATE);
		return settings.getBoolean("is_first_load", true);
	}
	
	public boolean appInstalledOrNot(String uri)
	{
		PackageManager pm = getPackageManager();
		boolean app_installed = false;
		try
		{
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e)
		{
			app_installed = false;
		}
		return app_installed;
	}
	
	public void showConfirmDialog(final String msg, final B2DialogListener listener)
	{
		FragmentManager fm = this.getFragmentManager();
		B2Dialog b2Dialog = new B2Dialog();
		b2Dialog.setListener(listener);
		b2Dialog.setMessage(msg);
		b2Dialog.show(fm, "b2_dialog");
	}
	
	@Override
	public void onFinishEditDialog(String inputText, boolean inOptions, boolean guest)
	{
		if (!guest)
		{
			resourceManager.setDefaultUsername(inputText);
			SceneManager.getInstance().setDefaultUsername();
			if (!inOptions)
				this.toastOnUiThread("Default username set\nTo change go to options", Toast.LENGTH_LONG);
			
		} else
			ScoreInfo.score_name = inputText;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.d("GAME", "WAZZUP");
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// TODO Facebook Code
	/*
	 * public boolean usesFacebook() { return useFacebook; }
	 * 
	 * @Override public void onActivityResult(int requestCode, int resultCode,
	 * Intent data) { super.onActivityResult(requestCode, resultCode, data);
	 * Session.getActiveSession().onActivityResult(this, requestCode,
	 * resultCode, data); }
	 * 
	 * public void facebookLogin() { Log.d("GAME", "LOGIN");
	 * Session.openActiveSession(this, true, new Session.StatusCallback() {
	 * 
	 * @Override public void call(Session session, SessionState state, Exception
	 * exception) { if (session.isOpened()) {
	 * Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	 * 
	 * @Override public void onCompleted(GraphUser user, Response response) { if
	 * (user != null) { // CALLBACK: USER IS LOGGED IN // DO YOUR STUFF HERE
	 * fbUsername = user.getFirstName(); Toast.makeText(Balloonza.this, "Hello "
	 * + fbUsername, Toast.LENGTH_LONG).show(); useFacebook = true; // this will
	 * save the users first name to a // public variable fbUsername } } }); } }
	 * }); }
	 * 
	 * public void facebookRequestDialog() { Bundle params = new Bundle();
	 * params.putString("message", "Check out this great game!");
	 * 
	 * WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(this,
	 * Session.getActiveSession(), params)).setOnCompleteListener( new
	 * OnCompleteListener() {
	 * 
	 * @Override public void onComplete(Bundle values, FacebookException error)
	 * { if (error != null) { if (error instanceof
	 * FacebookOperationCanceledException) { // Request cancelled } else { //
	 * Network Error } } else { final String requestId =
	 * values.getString("request"); if (requestId != null) { // Request sent }
	 * else { // Request cancelled } } } }).build(); requestsDialog.show(); }
	 * 
	 * 
	 * public void facebookFeedDialog(String pRecord, int score, int combo) {
	 * if(!useFacebook) return; final Bundle params = new Bundle();
	 * params.putString("name", fbUsername + " Achieved A New Record!" +
	 * " Score: " + score + " Combo: " + combo + " Time: " + pRecord);
	 * params.putString("caption",
	 * "Pop as many balloons as you can before time runs out." +
	 * "Compete with your friends and earn bragging rights by beating their highscores!"
	 * ); params.putString("description",
	 * "Click on Get Balloonza button to pop your way into the highscores!");
	 * params.putString("link", "https://www.facebook.com/Bartholemule");
	 * params.putString("picture",
	 * "http://i1294.photobucket.com/albums/b618/BSquared_Gaming/ic_launcher_zps68493950.png"
	 * ); JSONObject actions = new JSONObject(); try { actions.put("name",
	 * "Get Balloonza"); //actions.put("link",
	 * "http://play.google.com/store/apps/details?id=yourPackageName");
	 * actions.put("link", "https://www.facebook.com/Braydon.Berthelet"); }
	 * catch (Exception e) { } ; params.putString("actions",
	 * actions.toString());
	 * 
	 * this.runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { WebDialog feedDialog = (new
	 * WebDialog.FeedDialogBuilder(Balloonza.this, Session.getActiveSession(),
	 * params)).setOnCompleteListener( new OnCompleteListener() {
	 * 
	 * @Override public void onComplete(Bundle values, FacebookException error)
	 * { if (error == null) { final String postId = values.getString("post_id");
	 * if (postId != null) { // POSTED } else { // POST CANCELLED } } else if
	 * (error instanceof FacebookOperationCanceledException) { // POST CANCELLED
	 * } else { // ERROR POSTING } }
	 * 
	 * }).build(); feedDialog.show(); }
	 * 
	 * }); }
	 * 
	 * public void checkFaceBook() { Session.openActiveSession(this, false, new
	 * Session.StatusCallback() {
	 * 
	 * @Override public void call(Session session, SessionState state, Exception
	 * exception) { if (session.isOpened()) {
	 * Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	 * 
	 * @Override public void onCompleted(GraphUser user, Response response) { if
	 * (user != null) { fbUsername = user.getFirstName(); useFacebook = true;
	 * Toast.makeText(Balloonza.this, "Welcome Back " + fbUsername,
	 * Toast.LENGTH_LONG).show(); } } }); } } }); }
	 * 
	 * public void postOnTFacebookTimeline() {
	 * 
	 * Bundle params = new Bundle(); params.putString("your object",
	 * "your object url");
	 * 
	 * Request request = new Request(Session.getActiveSession(),
	 * "me/<your app namespace>:<your action>", params, HttpMethod.POST, new
	 * Callback() {
	 * 
	 * @Override public void onCompleted(Response response) { // TODO
	 * Auto-generated method stub if (response.getError() == null) { // post
	 * successful } else {
	 * 
	 * } // Error occured } }); Response response = request.executeAndWait(); }
	 */
	
	/*
	 * public void facebookLogout() { if(!useFacebook) return; Session
	 * activeSession = Session.getActiveSession(); if (activeSession != null) {
	 * useFacebook = false; postToast("Have A Great Day " + fbUsername);
	 * activeSession.closeAndClearTokenInformation(); } }
	 */
	
	// @Override
	protected void dealWithIabSetupSuccess()
	{
		
	}
	
	// @Override
	protected void dealWithIabSetupFailure()
	{
		toastOnUiThread("Sorry In App Billing isn't available on your device");
	}
}
