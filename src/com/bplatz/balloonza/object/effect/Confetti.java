package com.bplatz.balloonza.object.effect;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.math.MathUtils;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.object.IndexSprite;
import com.bplatz.balloonza.object.Balloon.BalloonType;
import com.bplatz.balloonza.scene.LogicScene;

public class Confetti extends BaseExplosion
{
	
	public Confetti(LogicScene scene, int maxShrapnelCount, int count)
	{
		super(scene, maxShrapnelCount, count);
	}
	
	@Override
	protected void createParticleSystem()
	{
		super.createParticleSystem();

		final float mTimePart = 1f;
		
		particleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(1.0f));

		particleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-250, 250, -500, -250));
		
		particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, mTimePart, 1, 0));
		
		particleSystem.addParticleModifier(new RotationParticleModifier<Sprite>(0, mTimePart, 0, 360));
		
		explosionHandler = new TimerHandler(mTimePart, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				logicScene.unregisterUpdateHandler(explosionHandler);
				particleSystem.detachSelf();
				ResourceManager.getInstance().confetti_pool.recyclePoolItem(Confetti.this);
			}
		});
	}
	
	public void activateConfetti(final float x, final float y, final BalloonType balloonType)
	{
		for (int i = 0; i < maxShrapnel; i++)
		{
			shrapnel[i].setCurrentTileIndex(balloonType.ordinal() + (6*MathUtils.random(3)));
		}
		finishActivate(x, y);
	}

	@Override
	protected AnimatedSprite createExplosionSprite(final int pIndex)
	{
		return new IndexSprite(0, 0, ResourceManager.getInstance().confetti_region, ResourceManager.getInstance().vbom, pIndex)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
	}
}
