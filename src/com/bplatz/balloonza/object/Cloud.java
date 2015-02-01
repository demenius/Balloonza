package com.bplatz.balloonza.object;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.PoolItem;
import org.andengine.util.math.MathUtils;

import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.base.BaseSprite;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.ResourceManager;

public class Cloud extends PoolItem
{
	//----------------------------
	// VARIABLES
	//----------------------------
	
	private PhysicsHandler physicsHandler;
		
	private Sprite sprite;
	private boolean active = false;
	
	public enum CloudType
	{
		CLOUD_1, CLOUD_2, CLOUD_3, CLOUD_4, 
		CLOUD_5, CLOUD_6, CLOUD_7, CLOUD_8
	}
	
	private CloudType cloudType;
	
	
	//----------------------------
	// CONSTRUCTOR
	//----------------------------
	
	public Cloud(float pX, float pY, VertexBufferObjectManager vbo,
			BaseScene scene, ITextureRegion region, CloudType type)
	{
		super();
		sprite = new BaseSprite(pX, pY, region, vbo)
		{
			@Override
			public void onManagedUpdate(float pSecondsElapsed)
			{
				super.onManagedUpdate(pSecondsElapsed);
				if(active)
				{
					if(sprite.getX() >= 480 + sprite.getWidth()/2)
						removeCloud();
				
				}
			}
		};
		cloudType = type;
		
		physicsHandler = new PhysicsHandler(sprite);
		physicsHandler.setEnabled(false);
		scene.registerUpdateHandler(physicsHandler);
	}
	
	public void activate()
	{
		sprite.setVisible(true);
		sprite.setIgnoreUpdate(false);
	    active = true;
	    
	    int y = MathUtils.random(200, 800);
	    sprite.setPosition(-sprite.getWidth(), y);
	    
	    float s = MathUtils.random(Constant.CLOUD_MIN_SPEED, Constant.CLOUD_MAX_SPEED);
	    physicsHandler.setVelocityX(s);
	    
	    physicsHandler.setEnabled(true);
	}
	
	public void deactivate()
	{
		sprite.reset();
		sprite.setVisible(false);
		sprite.setIgnoreUpdate(true);
		physicsHandler.setEnabled(false);
	}
	
	
	private void removeCloud()
	{
		sprite.setIgnoreUpdate(true);
		active = false;
		ResourceManager.getInstance().cloud_pool.recyclePoolItem(cloudType.ordinal(), this);
	}
	
	public Sprite getSprite()
	{
		return sprite;
	}
}
