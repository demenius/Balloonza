package com.bplatz.balloonza.object.effect;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.particle.Particle;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.object.IndexSprite;
import com.bplatz.balloonza.scene.LogicScene;

public class Explosion extends BaseExplosion
{
	public Explosion(LogicScene scene, int maxShrapnelCount, int count)
	{
		super(scene, maxShrapnelCount, count);
	}
	
	@Override
	protected void createParticleSystem()
	{
		super.createParticleSystem();
		
		final float mTimePart = 0.5f;
		
		particleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(1.0f));
		
		particleSystem.addParticleInitializer(new ExplosionVelocityParticleInitializer<Sprite>(-750, 750, -750, 750));
		
		particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, 0.6f * mTimePart, 1, 0));
		
		particleSystem.addParticleModifier(new RotationParticleModifier<Sprite>(0, mTimePart, 0, 360));
		
		explosionHandler = new TimerHandler(mTimePart, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				logicScene.unregisterUpdateHandler(explosionHandler);
				particleSystem.detachSelf();
				ResourceManager.getInstance().explosion_pool.recyclePoolItem(Explosion.this);
			}
		});
	}
	
	@Override
	protected AnimatedSprite createExplosionSprite(final int pIndex)
	{
		return new IndexSprite(0, 0, ResourceManager.getInstance().shrapnel_region, ResourceManager.getInstance().vbom, pIndex)
		{
			@Override
			public void onManagedUpdate(float pSecondsElapsed)
			{
				if (logicScene.collisionCheck(this))
				{
					this.setIgnoreUpdate(true);
					this.setVisible(false);
				}
				
			}
			
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
	}
	
	private class ExplosionVelocityParticleInitializer<T extends IEntity> extends VelocityParticleInitializer<T>
	{
		
		public ExplosionVelocityParticleInitializer(float pMinVelocityX, float pMaxVelocityX, float pMinVelocityY, float pMaxVelocityY)
		{
			super(pMinVelocityX, pMaxVelocityX, pMinVelocityY, pMaxVelocityY);
		}
		
		@Override
		public void onInitializeParticle(final Particle<T> pParticle, final float pVelocityX, final float pVelocityY)
		{
			pParticle.getPhysicsHandler().setVelocity(pVelocityX, pVelocityY);
			final float pAccelerationX = -pVelocityX * 2f;
			final float pAccelerationY = -pVelocityY * 2f;
			pParticle.getPhysicsHandler().accelerate(pAccelerationX, pAccelerationY);
		}
		
	}
}
