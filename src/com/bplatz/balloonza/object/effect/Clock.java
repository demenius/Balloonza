package com.bplatz.balloonza.object.effect;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.particle.Particle;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.BaseDoubleValueSpanParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.math.MathUtils;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.object.IndexSprite;
import com.bplatz.balloonza.scene.LogicScene;

public class Clock extends BaseExplosion
{
	public Clock(LogicScene scene, int maxShrapnelCount, int count)
	{
		super(scene, maxShrapnelCount, count);
	}
	
	@Override
	protected void createParticleSystem()
	{
		super.createParticleSystem();

		final float mTimePart = 2f;
		
		particleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(1.0f));
		
		//particleSystem.addParticleInitializer(new ExplosionVelocityParticleInitializer<Sprite>(-750, 750, -750, 750));
		particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, mTimePart*0.6f, 0.5f, 1.0f));
		
		particleSystem.addParticleModifier(new CosSinVelocityModifier<Sprite>(0, mTimePart, 0, 100, 0, 360));
		
		particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, 0.6f*mTimePart, 1, 0));
		
		particleSystem.addParticleModifier(new RotationParticleModifier<Sprite>(0, mTimePart, 0, 360));
		
		explosionHandler = new TimerHandler(mTimePart, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				logicScene.unregisterUpdateHandler(explosionHandler);
				particleSystem.detachSelf();
				ResourceManager.getInstance().clock_pool.recyclePoolItem(Clock.this);
			}
		});
	}
	
	@Override
	protected AnimatedSprite createExplosionSprite(final int pIndex)
	{
		IndexSprite sprite = new IndexSprite(0, 0, ResourceManager.getInstance().clock_region, ResourceManager.getInstance().vbom, pIndex)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		return sprite;
	}
	
	private class CosSinVelocityModifier<T extends IEntity> extends BaseDoubleValueSpanParticleModifier<T>
	{

		public CosSinVelocityModifier(final float pStart, final float pDuration, final float pFromRadius, final float pToRadius, final float pFromDegree, final float pToDegree)
		{
			super(pStart, pDuration, pFromRadius, pToRadius, MathUtils.degToRad(pFromDegree), MathUtils.degToRad(pToDegree));
		}

		@Override
		protected void onSetInitialValues(Particle<T> pParticle, float pRadius, float pRadians)
		{
			final float baseRadians = MathUtils.degToRad(((IndexSprite)pParticle.getEntity()).index * (360 / Constant.CLOCK_SHRAPNEL));
			final float velocityX = (float) (pRadius * Math.sin(pRadians + baseRadians));
			final float velocityY = (float) (pRadius * Math.cos(pRadians + baseRadians));
			pParticle.getPhysicsHandler().setVelocity(velocityX, velocityY);
		}

		@Override
		protected void onSetValues(Particle<T> pParticle, float pPercentageDone, float pRadius, float pRadians)
		{
			final float baseRadians = MathUtils.degToRad(((IndexSprite)pParticle.getEntity()).index * (360 / Constant.CLOCK_SHRAPNEL));
			final float velocityX = (float) (pRadius * Math.sin(pRadians + baseRadians))*20;
			final float velocityY = (float) (pRadius * Math.cos(pRadians + baseRadians))*20;
			pParticle.getPhysicsHandler().setVelocity(velocityX, velocityY);
		}

	}
}
