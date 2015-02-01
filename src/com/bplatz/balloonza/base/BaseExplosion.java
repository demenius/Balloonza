package com.bplatz.balloonza.base;

import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntityFactory;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.pool.PoolItem;
import org.andengine.util.math.MathUtils;

import com.bplatz.balloonza.scene.LogicScene;

public abstract class BaseExplosion extends PoolItem
{
	protected TimerHandler explosionHandler;
	protected ParticleSystem<Sprite> particleSystem;
	protected LogicScene logicScene;
	protected AnimatedSprite[] shrapnel;
	private int shrapnelCount = 0;
	protected int maxShrapnel;
	protected int textureCount;
	
	
	public BaseExplosion(LogicScene scene, int maxShrapnelCount, int count)
	{
		logicScene = scene;
		maxShrapnel = maxShrapnelCount;
		textureCount = count;
		createShrapnel();
		
		createParticleSystem();
	}
	
	protected void createParticleSystem()
	{		
		PointParticleEmitter particleEmitter = new PointParticleEmitter(0, 0);
		IEntityFactory<Sprite> spriteFact = new IEntityFactory<Sprite>()
		{
			@Override
			public Sprite create(float pX, float pY)
			{
				Sprite s = shrapnel[shrapnelCount++];
				s.setUserData("SHRAPNEL");
				s.setPosition(pX, pY);
				return s;
			}
		};
		
		particleSystem = new ParticleSystem<Sprite>(spriteFact, particleEmitter, 500, 500, maxShrapnel);
	}
	
	public void activate(final float x, final float y)
	{
		for (int i = 0; i < maxShrapnel; i++)
			shrapnel[i].setCurrentTileIndex(MathUtils.random(textureCount));
		finishActivate(x, y);
	}
	
	public void finishActivate(final float x, final float y)
	{
		particleSystem.reset();
		((PointParticleEmitter) particleSystem.getParticleEmitter()).setCenter(x, y);
		logicScene.attachChild(particleSystem);
		explosionHandler.reset();
		
		logicScene.registerUpdateHandler(explosionHandler);
	}
	
	private void createShrapnel()
	{
		shrapnel = new AnimatedSprite[maxShrapnel];
		for (int i = 0; i < maxShrapnel; i++)
		{
			shrapnel[i] = createExplosionSprite(i);
		}
	}
	
	protected abstract AnimatedSprite createExplosionSprite(final int pIndex);
	
}
