package com.bplatz.balloonza.scene;

import java.util.ArrayList;

import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.math.MathUtils;

import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.custom.HighScoreDB;
import com.bplatz.balloonza.custom.ScoreInfo;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.manager.SceneManager.SceneType;

public class HighScoreScene extends BaseScene implements IOnMenuItemClickListener
{
	private final int MAX_SHOWING = 10;
	private MenuScene menuChildScene;
	private IMenuItem heyzapMenuItem;
	
	private Rectangle scorePane;
	private Text highscores;
	private Entity backing;
	private Rectangle[] darks;
	
	private NameAndValue[] scores;
	
	@Override
	public void createScene()
	{
		createViews();
		createScores();
		createMenuChildScene();
	}
	
	private void createViews()
	{
		scorePane = new Rectangle(240, 400, 480, 800, vbom);
		scorePane.setColor(Color.RED);
		scorePane.attachChild(createHighscoreBacking(new Color(0.8f, 0, 0)));
		scorePane.setVisible(true);
		attachChild(scorePane);
		
		highscores = new Text(240, 700, resourceManager.large_font, "High Scores", vbom);
		attachChild(highscores);
	}
	
	private Entity createHighscoreBacking(Color color)
	{
		backing = new Entity();
		darks = new Rectangle[MAX_SHOWING/2];
		for (int i = 0; i < MAX_SHOWING/2; i ++)
		{
			darks[i] = new Rectangle(0, 0, 480, 50, vbom);
			darks[i].setColor(color);
			darks[i].setPosition(240, 600 - (i * 100));
			backing.attachChild(darks[i]);
		}
		return backing;
	}
	
	public void loadHighScoreTables()
	{
		loadScores();
	}
	
	private void createScores()
	{
		scores = new NameAndValue[MAX_SHOWING];
		for (int i = 0; i < MAX_SHOWING; i++)
		{
			scores[i] = new NameAndValue(i);
			scorePane.attachChild(scores[i].name);
			scorePane.attachChild(scores[i].value);
		}
	}
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(resourceManager.camera);
		menuChildScene.setPosition(240, 0);
		
		
		heyzapMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(Constant.MENU_PLAY, resourceManager.heyzap_leaderboard_region, resourceManager.vbom), 1.025f, 1);
		
		menuChildScene.addMenuItem(heyzapMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		heyzapMenuItem.setPosition(0, 40);
		
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
	}
	
	private void loadScores()
	{
		ArrayList<ScoreInfo> scoreTable = resourceManager.highScoreDB.getTable(HighScoreDB.SCORETABLE);
		int max = MathUtils.min(scoreTable.size(), MAX_SHOWING, 11);
		for (int i = 0; i < max; i++)
		{
			scores[i].setValues(i, scoreTable.get(i).name, ResourceManager.FORMATTER.format(scoreTable.get(i).value));
		}
		
		for(int i = max; i < MAX_SHOWING; i++)
		{
			scores[i].setValues(i, "---", "---");
		}
	}
	
	
	@Override
	public void onBackKeyPressed()
	{
		SceneManager.getInstance().unloadHighScoreScene(engine);
	}
	
	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_HIGHSCORE;
	}
	
	@Override
	public void disponeScene()
	{
		scorePane.detachSelf();
		scorePane.dispose();
		scorePane = null;
		
		highscores.detachSelf();
		highscores.dispose();
		highscores = null;
		
		backing.detachSelf();
		backing.dispose();
		backing = null;
		
		for (int i = 0; i < MAX_SHOWING/2; i ++)
		{
			darks[i].detachSelf();
			darks[i].dispose();
			darks[i] = null;
		}
		darks = null;
		
		for(int i = 0; i < MAX_SHOWING; i++)
		{
			scores[i].name.detachSelf();
			scores[i].value.detachSelf();
			scores[i].name.dispose();
			scores[i].value.dispose();

			scores[i].name = null;
			scores[i].value = null;
		}
		
		scores = null;
		
		heyzapMenuItem.detachSelf();
		heyzapMenuItem.dispose();
		heyzapMenuItem = null;
		
		menuChildScene.detachSelf();
		menuChildScene.dispose();
		menuChildScene = null;
	}
	
	private static class NameAndValue
	{
		
		private final static TextOptions LEFT = new TextOptions(HorizontalAlign.LEFT);
		private final static TextOptions RIGHT = new TextOptions(HorizontalAlign.RIGHT);
		
		public Text name;
		public Text value;
		
		public NameAndValue(int index)
		{
			int y = 600 - index * 50;
			this.name = new Text(50, y, ResourceManager.getInstance().small_font, "1. LONGNAME", LEFT, ResourceManager.getInstance().vbom);
			this.value = new Text(430, y, ResourceManager.getInstance().small_font, "0,123,456,789", RIGHT, ResourceManager.getInstance().vbom);
			
			if(index == 9)
				this.name.setPosition(35, y);
			
			this.name.setAnchorCenterX(0);
			this.value.setAnchorCenterX(1);
			
			this.setValues((index + 1) , "---", "---");
		}
		
		public void setValues(int pos, String name, String value)
		{
			this.name.setText((pos + 1) + ". " + name);
			this.value.setText(value);
		}
	}
	
	public int setHighScore(final int score)
	{
		int scoreRank = resourceManager.highScoreDB.getRank(score, HighScoreDB.SCORETABLE);
		
		ScoreInfo info = new ScoreInfo();
		info.name = ScoreInfo.score_name;
		
		if (scoreRank < 11)
		{
			info.value = score;
			info.rank = scoreRank;
			resourceManager.highScoreDB.addValue(info, HighScoreDB.SCORETABLE);
		}
		return 0;
	}
	
	//--------------------------------
		//TODO IOnMenuItemClickListener
		//--------------------------------
		@Override
		public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
		{
			return false;
		}
}