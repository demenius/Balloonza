package com.bplatz.balloonza.object.effect;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.object.IndexSprite;
import com.bplatz.balloonza.scene.LogicScene;

public class Skull extends BaseExplosion
{
	
	public Skull(LogicScene scene, int maxShrapnelCount, int count)
	{
		super(scene, maxShrapnelCount, count);
	}
	
	@Override
	protected void createParticleSystem()
	{
		super.createParticleSystem();

		final float mTimePart = 1f;

		particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, mTimePart, 0.6f, 0.0f));
		particleSystem.addParticleModifier(new ScaleParticleModifier<Sprite>(0, mTimePart, 0.4f, 4.0f));
		
		explosionHandler = new TimerHandler(mTimePart, new ITimerCallback()
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				logicScene.unregisterUpdateHandler(explosionHandler);
				particleSystem.detachSelf();
				ResourceManager.getInstance().skull_effect_pool.recyclePoolItem(Skull.this);
			}
		});
	}

	@Override
	protected AnimatedSprite createExplosionSprite(final int pIndex)
	{
		return new IndexSprite(0, 0, ResourceManager.getInstance().skull_region, ResourceManager.getInstance().vbom, pIndex)
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
