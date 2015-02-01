package com.bplatz.balloonza.scene;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;

import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.manager.SceneManager.SceneType;

public class SplashScene extends BaseScene
{
	private Sprite splash;
	private Sprite badge;
	
	@Override
	public void createScene()
	{
		splash = new BaseSprite(0, 0, resourceManager.splash_region, vbom);
		badge = new BaseSprite(0, 0, resourceManager.badge_region, vbom);
		
		splash.setScale(0.5f);
		splash.setPosition(240, 400);
		badge.setPosition(240, 400);
		
		splash.setAlpha(0.0f);
		badge.setAlpha(0.0f);
		
		final SequenceEntityModifier entityModifier = new SequenceEntityModifier(
				new IEntityModifierListener()
				{
					@Override
					public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem)
					{
						// Sequence Started
					}
					
					@Override
					public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity)
					{
						if(pEntity.equals(splash))
							badge.registerEntityModifier((IEntityModifier) pEntityModifier.deepCopy());
					}
				}, new AlphaModifier(0.5f, 0, 1), new DelayModifier(0.75f), new AlphaModifier(0.5f, 1, 0));
		
		splash.registerEntityModifier(entityModifier);

		attachChild(splash);
		attachChild(badge);
	}
	
	@Override
	public void onBackKeyPressed()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_SPLASH;
	}
	
	@Override
	public void disponeScene()
	{
		splash.detachSelf();
		splash.dispose();
		splash = null;
		
		badge.detachSelf();
		badge.dispose();
		badge = null;
		
		this.detachSelf();
		this.dispose();
	}
	
}
