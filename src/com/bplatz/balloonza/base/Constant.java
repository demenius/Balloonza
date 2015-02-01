package com.bplatz.balloonza.base;

public class Constant
{

	public static final float SCREEN_WIDTH = 480;
	public static final float SCREEN_HEIGHT = 800;
	
	//LifeBar
	public final static float PERCENT_PER_SECOND =  0.50f; // How Balloons Effect LifeBar
	public final static float LIFEBAR_PERCENT_DECREASE_PER_SECOND = 0.05f; // Percent Decrease When No Effect Is Active
	public final static float TIME_TO_DIE = 1.00f / LIFEBAR_PERCENT_DECREASE_PER_SECOND; // Time It Takes For The Bar To Decrease To ZERO With No Action
	
	public final static float PROGRESS_TIME = 0.25f; // Progress Bar Update Time

	public final static float[] PROGRESS_SPEED_INCREASE_TIME = {180, 1f}; // How Often the Progress Bars Value Will Increase. Last Value Is Repeated
	public final static float PROGRESS_MAX_EXTRA_SPEED_CAP = 0.075f; // Max Extra Progress Bar Speed Cap.
	public final static float PROGRESS_SPEED_INCREASE = 0.00125f; // How Much The Progress Bar Speeds Up By Per Value Increase
	
	//GameHandler
	
	public final static int[] COMBO_METER = {5, 15, 40, 60}; // When 2x,3x,4x,5x Multipliers Become Active. Must Be 4 Values
	public final static float X2_TIMER = 5.0f; // How Long The x2 Multiplier Lasts
	
	public final static float SKULL_MOD_TIMER = 120; // Time When Skulls Start Fading
	public final static float SKULL_FADE_MODIFIER = 0.5f; //the amount of time the skull takes to fade in and out.
	public final static float SKULL_VISIBLE_DURATION = 1.0f; //the amount of time the skull will be fully visible.
	public final static float SKULL_INVISIBLE_DURATION = 0.5f; //the amount of time the skull will be fully visible.

	public final static float LIFEBAR_PERCENT_BALLOON_INCREASE = 0.05f; // Balloons Give 5% To The Lifebar
	public final static float LIFEBAR_PERCENT_SKULL_DECREASE = -0.15f; // Skulls Take 15% Of The Lifebar

	public final static int BALLOON_POINTS = 1000; // Base Points Given By Balloons. Affected By Multiplier And Bombs
	public final static int BONUS_POINTS = 1500; // Base Points Given By Bonus. Affected By Multiplier And Bombs
	public final static int SKULL_POINTS = -2000; // Base Points Taken By Skulls. Affected By Bombs
	
	// 2.5: Skull 1; 5.0: Skull 2; 7.5: Skull 3; 17.5: Bonus 1; 22.5: Balloon 4; 25.0: Skull 4;
	// 35.0: Bonus 2; 40.0: Balloon 5; 42.5: Skull 5; 52.5: Bonus 3; 57.5: Balloon 6; 60.0: Skull 6
	// Balloon Time, Skull Time, Bonus Time
	public final static float BALLOON_TYPE_TIMER[] = {5f, 2.5f, 10f};
	
	public final static float MIN_COLOR_TIME = 3.0f; // Min Time For Lifebar Color Change
	public final static float MAX_COLOR_TIME = 6.0f; // Max Time For Lifebar Color Change

	public final static float MIN_BALLOON_TIME = 1.5f; // Min Time For A Balloon To Spawn. Affected By Total Balloons Available. 1.5/total
	public final static float MAX_BALLOON_TIME = 2.25f; // Max Time For A Balloon To Spawn. Affected By Total Balloons Available. 2.25/total

	public final static float MIN_BONUS_TIME = 9f; // Min Time For A Bonus To Spawn. Affected By Total Bonus Available. 6/total
	public final static float MAX_BONUS_TIME = 15f; // Max Time For A Bonus To Spawn. Affected By Total Bonus Available.12/total

	public final static float MIN_SKULL_TIME = 4.5f; // Min Time For A Skull To Spawn. Affected By Total Skulls Available. 3/total
	public final static float MAX_SKULL_TIME = 6.75f; // Max Time For A Skull To Spawn. Affected By Total Skulls Available. 4.5/total
	
	//LifeBar
	public final static float FREEZE_TIME = 5.0f; // How Long Time Freeze Lasts
	
	// Menu Button Codes
	
	//GameHandler
	public final static int MENU_REPLAY = 0;
	public final static int MENU_VIEW = 1;
	public final static int MENU_MORE = 2;
	public final static int MENU_SUBMIT_HEYZAP = 3;
	
	//MenuHandler
	public final static int MENU_PLAY = 0;
	public final static int MENU_OPTIONS = 1;
	public final static int MENU_HIGHSCORES = 2;
	public final static int MENU_LOGIN = 3;
	public final static int MENU_RATE_US = 4;
	public final static int MENU_GUEST = 5;
	public final static int MENU_HOW_TO_PLAY = 6;
	public final static int MENU_REMOVE_ADS = 7;
	
	//OptionScene
	public final static int MENU_LOGOUT = 0;
	public final static int MENU_RESET = 1;
	public final static int MENU_CHANGE = 2;
	public final static int MENU_HORIZONTAL = 3;
	public final static int MENU_VERTICAL = 4;
	public final static int MENU_TOP = 5;
	public final static int MENU_BOTTOM = 6;
	public final static int MENU_RIGHT = 7;
	public final static int MENU_LEFT = 8;
	
	//HighScoreScene
	public final static int MENU_HEYZAP = 0;
	
	//HowToPlayScene
	public final static int MENU_PREV = 0;
	public final static int MENU_NEXT = 1;
	public final static int MENU_GAME = 2;
	
	
	//Balloon
	public final static float BALLOON_MIN_BASE_SPEED = 75; // Minimum Base Speed For All Balloons
	public final static float BALLOON_MAX_BASE_SPEED = 150; // Maximum Base Speed For All Balloons

	// SPEED TIMERS
	// Minimum Value For Time Must Be Greater Than Or Equal To 1000/MAX_FPS = 1000/40 = 25ms = 0.025s
	public final static float BALLOON_MAX_EXTRA_SPEED_CAP = 350; // Maximum Extra Speed Added Onto Base Speed
	public final static float BALLOON_SPEED_INCREASE = 1.5f; // How Much Extra Speed Is Added Per Timer Tick
	public final static float[] BALLOON_SPEED_INCREASE_TIME = {30f, 1f}; // Timer Tick Values. Last Value Is Repeated Till Cap Is Reached
	
	public final static float BONUS_MAX_EXTRA_SPEED_CAP = 450; // Maximum Extra Speed Added Onto Base Speed
	public final static float BONUS_SPEED_INCREASE = 3f; // How Much Extra Speed Is Added Per Timer Tick
	public final static float[] BONUS_SPEED_INCREASE_TIME = {60, 1f}; // Timer Tick Values. Last Value Is Repeated Till Cap Is Reached
	
	public final static float SKULL_MAX_EXTRA_SPEED_CAP = 350; // Maximum Extra Speed Added Onto Base Speed
	public final static float SKULL_SPEED_INCREASE = 1.5f; // How Much Extra Speed Is Added Per Timer Tick
	public final static float[] SKULL_SPEED_INCREASE_TIME = {30, 1f}; // Timer Tick Values. Last Value Is Repeated Till Cap Is Reached
	
	// SPAWN TIMERS
	public final static float BALLOON_MIN_EXTRA_SPAWN_CAP = 1.5f; // Maximum Extra Time Added Onto Balloon Spawn Time
	public final static float BALLOON_SPAWN_DECREASE = 0.00f; // How Much Is Added Per Timer Tick
	public final static float[] BALLOON_SPAWN_DECREASE_TIME = {9000, 0.025f}; // Timer Tick Values. Last Value Is Repeated Till Cap Is Reached
	
	public final static float BONUS_MIN_EXTRA_SPAWN_CAP = 15.0f; // Maximum Extra Time Added Onto Bonus Spawn Time
	public final static float BONUS_SPAWN_DECREASE = 0.00f; // How Much Is Added Per Timer Tick
	public final static float[] BONUS_SPAWN_DECREASE_TIME = {6000, 0.025f}; // Timer Tick Values. Last Value Is Repeated Till Cap Is Reached
	
	public final static float SKULL_MAX_EXTRA_SPAWN_CAP = -0.70f; // Maximum Extra Time Added Onto Skull Spawn Time
	public final static float SKULL_SPAWN_INCREASE = -0.005f; // How Much Is Added Per Timer Tick
	public final static float[] SKULL_SPAWN_INCREASE_TIME = {30, 1f}; // Timer Tick Values. Last Value Is Repeated Till Cap Is Reached
	
	//Fat Kid
	public final static float FAT_KID_MAX_SPEED = 220; // Maximum Fat Kid Speed
	public final static float FAT_KID_MIN_SPEED = 100; // Minimum Fat Kid Speed
	public final static float FAT_KID_MAX_ACTIVE_TIME = 10; // Maximum Active Time
	public final static float FAT_KID_MIN_ACTIVE_TIME = 5; // Minimum Active Time
	public final static float[] FAT_KID_SPAWN_RATE = {60, 30, 15, 10}; // Timer Tick Values. Last Value Is Repeated Till Game Ends
	public final static int FAT_KID_POINTS = 25000; // How Many Points You Get When You Kill The Fat Kid

	public final static int FAT_KID_MAX_LIFE = 5; // How Many Times You Must Hit The Fat Kid Before He Dies
	public final static float FAT_KID_SIZE_SCALE = 1.125f; // Percentage By Which Fat Kid Grows Per Hit
	public final static float FAT_KID_SPEED_SCALE = 1.06125f; // Percentage By Which Fat Kid Gets Faster Per Hit
	
	//Clown
	public final static float CLOWN_ACTIVE_TIME = 1f; // How Long The Clowns Stays Up With The Dialog
	
	//Cloud
	public final static float CLOUD_MAX_SPEED = 150; // Maximum Cloud Speed
	public final static float CLOUD_MIN_SPEED = 25; // Minimum Cloud Speed
	
	//Explosion
	public final static int BOMB_SHRAPNEL = 10; // How Many Particles Are Spawn For Bomb Balloons
	public final static int CLOCK_SHRAPNEL = 10; // How Many Particles Are Spawn For Clock Balloons
	public final static int CONFETTI_SHRAPNEL = 10; // How Many Particles Are Spawn Per Balloon When x2 Is Active
	public final static int CANDY_SHRAPNEL = 50; // How Many Particles Are Spawn When You Kill The Fat Kid
}
