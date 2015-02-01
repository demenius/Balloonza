package com.bplatz.balloonza.object;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.PoolItem;
import org.andengine.util.math.MathUtils;
import org.andengine.util.modifier.IModifier;

import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.ResourceManager.BalloonPoolType;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.scene.LogicScene;

public class Balloon extends PoolItem
{
	// ----------------------------
	// VARIABLES
	// ----------------------------
	
	private final long[] Balloon_ANIMATE = new long[] { 50, 50, 50, 50 };
	private final IAnimationListener animListener = new IAnimationListener()
	{
		public void onAnimationStarted(AnimatedSprite pAS, int pILC)
		{
			if(poppedByFatKid)
			{
				ResourceManager.getInstance().balloon_pop_sounds[MathUtils.RANDOM.nextInt(4)].play();
			} else if (BalloonPoolType.getPoolType(balloonType) == BalloonPoolType.BALLOON)
			{
				ResourceManager.getInstance().balloon_pop_sounds[MathUtils.RANDOM.nextInt(4)].play();
				if (SceneManager.getInstance().getLogicScene().x2Active())
					SceneManager.getInstance().getLogicScene().activateConfetti(sprite.getX(), sprite.getY(), Balloon.this.balloonType);
			} else if (BalloonPoolType.getPoolType(balloonType) == BalloonPoolType.SKULL)
			{
				ResourceManager.getInstance().skull_pop.play();
				SceneManager.getInstance().getLogicScene().activateSkull(skullX(), skullY());
			} else if (balloonType == BalloonType.BOMB_BALLOON)
			{
				ResourceManager.getInstance().bomb_pop.play();
				SceneManager.getInstance().getLogicScene().activateBomb(sprite.getX(), sprite.getY());
			} else if (balloonType == BalloonType.TIME_BALLOON)
			{
				SceneManager.getInstance().activateFreeze();
				SceneManager.getInstance().getLogicScene().activateClock(sprite.getX(), sprite.getY());
			} else if (balloonType == BalloonType.X2_BALLOON)
			{
				ResourceManager.getInstance().balloon_pop_sounds[MathUtils.RANDOM.nextInt(4)].play();
				SceneManager.getInstance().activateX2();
			}
		}
		
		public void onAnimationFrameChanged(AnimatedSprite pAS, int pFI, int pNFI)
		{
		}
		
		public void onAnimationLoopFinished(AnimatedSprite pAS, int pRLC, int pILC)
		{
		}
		
		@Override
		public void onAnimationFinished(AnimatedSprite pAnimatedSprite)
		{
			ResourceManager.getInstance().recycleBalloon(balloonType, Balloon.this);
		}
	};
	
	private final Runnable REMOVER = new Runnable()
	{
		@Override
		public void run()
		{
			sprite.detachSelf();
		}
	};
	
	private float speed;
	
	private BalloonSprite sprite;
	private PhysicsHandler physicsHandler;
	private boolean active = false;
	private boolean popped = false;
	private boolean poppedByFatKid = false;
	
	public enum BalloonType
	{
		BLUE_BALLOON, GREEN_BALLOON, ORANGE_BALLOON, PURPLE_BALLOON, RED_BALLOON, YELLOW_BALLOON, BLUE_SKULL, GREEN_SKULL, ORANGE_SKULL, PURPLE_SKULL, RED_SKULL, YELLOW_SKULL, BOMB_BALLOON, TIME_BALLOON, X2_BALLOON
	}
	
	private BalloonType balloonType;
	
	// ----------------------------
	// CONSTRUCTOR
	// ----------------------------
	
	public Balloon(float pX, float pY, VertexBufferObjectManager vbo, BaseScene scene, ITiledTextureRegion region, BalloonType type)
	{
		super();
		balloonType = type;
		sprite = new BalloonSprite(pX, pY, vbo, region);
		sprite.balloon = this;
		sprite.setUserData(this);
		physicsHandler = new PhysicsHandler(sprite);
		physicsHandler.setEnabled(false);
		scene.registerUpdateHandler(physicsHandler);
	}
	
	public void activate()
	{
		if (BalloonPoolType.getPoolType(balloonType) == BalloonPoolType.SKULL)
		{
			sprite.skull.setAlpha(1.0f);
			sprite.skull.setScale(0.40f);
			sprite.skull.setVisible(true);
			sprite.skullMod.reset();
			if(ResourceManager.getInstance().gameVariable.skull_mod_active)
				sprite.skull.registerEntityModifier(sprite.skullMod);
		}
		sprite.setVisible(true);
		sprite.setIgnoreUpdate(false);
		active = true;
		popped = false;
		poppedByFatKid = false;
		sprite.setCurrentTileIndex(0);
		int x = (int) MathUtils.random(sprite.getWidth() / 2, 480 - sprite.getWidth() / 2);
		speed = MathUtils.random(Constant.BALLOON_MIN_BASE_SPEED, Constant.BALLOON_MAX_BASE_SPEED);
		
		switch (BalloonPoolType.getPoolType(balloonType))
		{
			case BALLOON:
				speed += ResourceManager.getInstance().gameVariable.balloon_extra_speed;
				break;
			case BONUS:
				speed += ResourceManager.getInstance().gameVariable.bonus_extra_speed;
				break;
			case SKULL:
				speed += ResourceManager.getInstance().gameVariable.skull_extra_speed;
				break;
			default:
				break;
		
		}
		
		sprite.setPosition(x, -sprite.getHeight()/2);
		physicsHandler.setVelocityY(speed);
		
		physicsHandler.setEnabled(true);
	}
	
	public void deactivate()
	{
		active = false;
		sprite.setVisible(false);
		sprite.setIgnoreUpdate(true);
		physicsHandler.setEnabled(false);
		if (BalloonPoolType.getPoolType(balloonType) == BalloonPoolType.SKULL)
			sprite.skull.unregisterEntityModifier(sprite.skullMod);
		SceneManager.getInstance().getLogicScene().unregisterTouchArea(sprite.touchArea);
		ResourceManager.getInstance().engine.runOnUpdateThread(REMOVER);
	}
	
	public boolean pop(boolean poppedByBomb, boolean pPoppedByFatKid)
	{
		if (!active || popped)
			return false;
		
		poppedByFatKid = pPoppedByFatKid;
		popped = true;
		if(!poppedByFatKid)
			((LogicScene) SceneManager.getInstance().getCurrentScene()).addToScore(this.balloonType, poppedByBomb);
		
		if (BalloonPoolType.getPoolType(balloonType) == BalloonPoolType.SKULL)
		{
			sprite.skull.setVisible(false);
		}
		sprite.animate(Balloon_ANIMATE, 0, 3, false, animListener);
		return true;
	}
	
	
	public AnimatedSprite getSprite()
	{
		return sprite;
	}
	
	public void pauseBalloon()
	{
		physicsHandler.setEnabled(false);
	}
	
	public void resumeBalloon()
	{
		physicsHandler.setEnabled(true);
	}
	
	private float skullX()
	{
		return sprite.getX();
	}
	
	private float skullY()
	{
		return sprite.getY() + sprite.skull.getY()/4;
	}
	
	public class BalloonSprite extends AnimatedSprite
	{
		public Balloon balloon;
		public Rectangle touchArea;
		public Sprite skull;
		
		public SequenceEntityModifier skullMod;
		
		public BalloonSprite(float pX, float pY, VertexBufferObjectManager vbo, ITiledTextureRegion region)
		{
			super(pX, pY, region, vbo);
			touchArea = new Rectangle(getWidth() / 2, getHeight() / 2 - getHeight() / 12f, this.getWidth() * 0.75f, this.getHeight(), vbo)
			{
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
				{
					final LogicScene scene = SceneManager.getInstance().getLogicScene();
					if (!scene.started())
						return false;
					
					if (pSceneTouchEvent.isActionDown())
					{
						if (popped)
							return false;
						pop(false, false);
						return true;
					}
					return false;
				}
			};
			touchArea.setAlpha(0);
			attachChild(touchArea);
			
			if (BalloonPoolType.getPoolType(balloonType) == BalloonPoolType.SKULL)
			{
				
				skull = new BaseSprite(getWidth() / 2, getHeight() / 1.75f, ResourceManager.getInstance().skull_region,
						ResourceManager.getInstance().vbom);
				skull.setScale(0.40f);
				attachChild(skull);

				skullMod = new SequenceEntityModifier(new IEntityModifierListener()
				{
					
					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
					{
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
					{
						if (skull.isVisible())
							skullMod.reset();
					}
					
				}, 	new AlphaModifier(Constant.SKULL_FADE_MODIFIER, 1.0f, 0.0f), new DelayModifier(Constant.SKULL_INVISIBLE_DURATION), 
					new AlphaModifier(Constant.SKULL_FADE_MODIFIER, 0.0f, 1.0f), new DelayModifier(Constant.SKULL_VISIBLE_DURATION));
				skullMod.setAutoUnregisterWhenFinished(false);
			}
		}
		
		@Override
		protected void preDraw(GLState pGLState, Camera pCamera)
		{
			super.preDraw(pGLState, pCamera);
			pGLState.enableDither();
		}
		
		@Override
		public void onManagedUpdate(float pSecondsElapsed)
		{
			super.onManagedUpdate(pSecondsElapsed);
			if (active)
			{
				if (sprite.getY() >= 800 + sprite.getHeight())
					ResourceManager.getInstance().recycleBalloon(balloonType, Balloon.this);
			}
		}
		
	}
}
