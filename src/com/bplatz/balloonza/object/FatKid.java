package com.bplatz.balloonza.object;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.math.MathUtils;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.object.effect.Candy;
import com.bplatz.balloonza.scene.LogicScene;

public class FatKid extends AnimatedSprite
{
	private long[] ANIMATION = {250, 250, 250, 250};
	
	private int life;
	
	private PhysicsHandler physicsHandler;
	private float velocityX, velocityY;
	private boolean active = false;
	private final BaseExplosion candyExplosion;
	private TimerHandler speedControl = new TimerHandler(1f, true, new ITimerCallback()
	{

		@Override
		public void onTimePassed(TimerHandler pTimerHandler)
		{
			if(!active)
			{
				if(FatKid.this.mX > 240)
					velocityX = Constant.FAT_KID_MAX_SPEED;
				else
					velocityX = -Constant.FAT_KID_MAX_SPEED;
				if(FatKid.this.mY > 400)
					velocityY = Constant.FAT_KID_MAX_SPEED;
				else
					velocityY = -Constant.FAT_KID_MAX_SPEED;
					
				changeVelocity(velocityX, velocityY, 1, 1);
				SceneManager.getInstance().getLogicScene().unregisterUpdateHandler(speedControl);
				return;
			}
			FatKid.this.changeVelocity(0, 0, 0,0);
		}
		
	});
	
	//Fat Kid Is Active For 30s. Then Fades Out And Registers Activator
	private final TimerHandler RUNNER = new TimerHandler(MathUtils.random(Constant.FAT_KID_MIN_ACTIVE_TIME, Constant.FAT_KID_MAX_ACTIVE_TIME), false, 
	new ITimerCallback()
	{
		@Override
		public void onTimePassed(TimerHandler pTimerHandler)
		{
			SceneManager.getInstance().getLogicScene().unregisterUpdateHandler(RUNNER);
			active = false;
			SceneManager.getInstance().getLogicScene().resetFatKid();
		}
	});
	
	private final TimerHandler ACTIVATOR = new TimerHandler(0.1f, false, new ITimerCallback()
	{
		@Override
		public void onTimePassed(TimerHandler pTimerHandler)
		{
			SceneManager.getInstance().getLogicScene().unregisterUpdateHandler(ACTIVATOR);
			RUNNER.setTimerSeconds(MathUtils.random(Constant.FAT_KID_MIN_ACTIVE_TIME, Constant.FAT_KID_MAX_ACTIVE_TIME));
			RUNNER.reset();
			active = true;
			FatKid.this.physicsHandler.setEnabled(true);
			FatKid.this.animate(ANIMATION, (int)(RUNNER.getTimerSeconds()+1.5));
			FatKid.this.setVisible(true);
			FatKid.this.setIgnoreUpdate(false);
			SceneManager.getInstance().getLogicScene().registerUpdateHandler(RUNNER);
			SceneManager.getInstance().getLogicScene().registerUpdateHandler(speedControl);
		}
	});
	

	public FatKid(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, LogicScene scene)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		setVisible(false);
		setIgnoreUpdate(true);
		setUserData("FATKID");
		physicsHandler = new PhysicsHandler(this);
		physicsHandler.setEnabled(false);
		scene.registerUpdateHandler(physicsHandler);
		candyExplosion = new Candy(scene, Constant.CANDY_SHRAPNEL, 18);
	}
	
	@Override
	protected void preDraw(GLState pGLState, Camera pCamera)
	{
		super.preDraw(pGLState, pCamera);
		pGLState.enableDither();
	}
	
	public void remove()
	{
		SceneManager.getInstance().getLogicScene().unregisterUpdateHandler(speedControl);
		SceneManager.getInstance().getLogicScene().unregisterUpdateHandler(physicsHandler);
		detachSelf();
		dispose();
	}
	
	public void pause()
	{
		if(!isVisible())
			return;
		physicsHandler.setEnabled(false);
		SceneManager.getInstance().getLogicScene().unregisterUpdateHandler(speedControl);
		SceneManager.getInstance().getLogicScene().unregisterUpdateHandler(RUNNER);
		this.stopAnimation();
	}
	
	public void resume()
	{
		if(!isVisible())
			return;
		physicsHandler.setEnabled(true);
		SceneManager.getInstance().getLogicScene().registerUpdateHandler(speedControl);
		SceneManager.getInstance().getLogicScene().registerUpdateHandler(RUNNER);
		
		this.animate(ANIMATION, (int)(RUNNER.getTimerSeconds()+1.5-RUNNER.getTimerSecondsElapsed()));
	}
	
	private void changeVelocity(float vX, float vY, int xSign, int ySign)
	{
		// Every Life Lost He Gains a Speed Boost
		float lifeSpeed = (Constant.FAT_KID_MAX_LIFE - life) * Constant.FAT_KID_SPEED_SCALE;
		lifeSpeed = (lifeSpeed == 0 ? 1 : lifeSpeed);
		
		if(xSign == 0)
			xSign = MathUtils.randomSign();
		if(ySign == 0)
			ySign = MathUtils.randomSign();
		if(vX == 0)
			velocityX = MathUtils.random(Constant.FAT_KID_MIN_SPEED * lifeSpeed, Constant.FAT_KID_MAX_SPEED * lifeSpeed) * xSign;
		else
			velocityX = vX;
		if(vY == 0)
			velocityY = MathUtils.random(Constant.FAT_KID_MIN_SPEED * lifeSpeed, Constant.FAT_KID_MAX_SPEED * lifeSpeed) * ySign;
		else
			velocityY = vY;
		
		physicsHandler.setVelocity(velocityX, velocityY);
		//float angle = MathUtils.radToDeg((float) Math.atan(velocityY/velocityX));
		//if(velocityY < 0)
		//	angle += 180;
		//this.setRotation(angle);
		/*if(velocityY < 0)
			this.setRotation(0);
		else
		{
			if(velocityX > 0)
				this.setRotation(10);
			else
				this.setRotation(-10);
		}*/
	}
	
	public boolean activate()
	{
		if(isVisible())
			return false;
		life = Constant.FAT_KID_MAX_LIFE;
		setScale(1.0f);
		float x = 0, y = 0;
		int xSign = MathUtils.randomSign(), ySign = MathUtils.randomSign();
		switch(MathUtils.random(4)) // Random Side And Position
		{
			case 0: // Left
				x = -mWidth;
				y = MathUtils.random(mHeight, 800-mHeight);
				xSign = 1;
				break;
			case 1: // Right
				x = 480 + mWidth;
				y = MathUtils.random(mHeight, 800-mHeight);
				xSign = -1;
				break;
			case 2: // Top
				x = MathUtils.random(mWidth, 480-mWidth);
				y = 800+mHeight;
				ySign = -1;
				break;
			case 3: //Bottom
				x = MathUtils.random(mWidth, 480-mWidth);
				y = -mHeight;
				ySign = 1;
				break;
		}

		this.setPosition(x, y);

		this.changeVelocity(0, 0, xSign, ySign);
		
		ACTIVATOR.reset();
		SceneManager.getInstance().getLogicScene().registerUpdateHandler(ACTIVATOR);
		return true;
	}
	
	private boolean leftHit(final float extra)
	{
		return this.mX - this.mWidth/2 + extra < 0 && this.velocityX < 0;
	}
	
	private boolean rightHit(final float extra)
	{
		return this.mX + this.mWidth/2 - extra > 480 && this.velocityX > 0;
	}
	
	private boolean topHit(final float extra)
	{
		return this.mY + this.mHeight/2 - extra > 800 && this.velocityY > 0;
	}
	
	private boolean bottomHit(final float extra)
	{
		return this.mY - this.mHeight/2 + extra < 0 && this.velocityY < 0;
	}
	
	@Override
	public void onManagedUpdate(float pSecondsElapsed)
	{
		super.onManagedUpdate(pSecondsElapsed);
		SceneManager.getInstance().getLogicScene().collisionCheck(this);
		if(active)
		{
			if(leftHit(0) || rightHit(0))
			{
				velocityX = -velocityX;
				//this.setRotation(-super.mRotation);
				physicsHandler.setVelocityX(velocityX);
			}
			
			if(topHit(0) || bottomHit(0))
			{
				velocityY = -velocityY;
				/*if(velocityY < 0)
					this.setRotation(0);
				else
				{
					if(velocityX > 0)
						this.setRotation(10);
					else
						this.setRotation(-10);
				}*/
				physicsHandler.setVelocityY(velocityY);
			}

			//float angle = MathUtils.radToDeg((float) Math.atan(velocityY/velocityX));
			//if(velocityY < 0)
			//	angle += 180;
			//this.setRotation(angle);
		} else if(leftHit(mWidth) || rightHit(mWidth) || topHit(mHeight) || bottomHit(mHeight))
		{
			this.setVisible(false);
			this.setIgnoreUpdate(true);
			physicsHandler.setEnabled(false);
		}
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
	{
		final LogicScene scene = SceneManager.getInstance().getLogicScene();
		if (!scene.started())
			return false;
		
		if (pSceneTouchEvent.isActionDown())
		{
			if (life <= 0)
				return false;
			
			life--;
			setScale(getScaleX() * Constant.FAT_KID_SIZE_SCALE);
			if(life <= 0)
			{
				scene.unregisterUpdateHandler(RUNNER);
				active = false;
				scene.resetFatKid();
				this.setVisible(false);
				this.setIgnoreUpdate(true);
				physicsHandler.setEnabled(false);
				scene.addToScore(null, false);
				candyExplosion.activate(mX, mY);
			}
			this.changeVelocity(0, 0, 0, 0);
			return true;
		}
		return false;
	}
	
	
}
