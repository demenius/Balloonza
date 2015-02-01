package com.bplatz.balloonza.object.pool;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.MultiPool;

import com.bplatz.balloonza.base.BasePool;
import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.object.Balloon;
import com.bplatz.balloonza.object.Balloon.BalloonType;

public class BonusPool extends MultiPool<Balloon>
{
	private BasePool timePool;
	private BasePool bombPool;
	private BasePool x2Pool;
	
	public void initiatePool(final VertexBufferObjectManager vbo, final BaseScene scene)
	{
		final int startingBalloons = 10;
		timePool =  new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().time_balloon_region, BalloonType.TIME_BALLOON);
			}
		};
		
		bombPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().bomb_balloon_region, BalloonType.BOMB_BALLOON);
			}
		};
		
		x2Pool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene,  
						ResourceManager.getInstance().x2_balloon_region, BalloonType.X2_BALLOON);
			}
		};
		
		this.registerPool(BalloonType.TIME_BALLOON.ordinal(), timePool);
		this.registerPool(BalloonType.BOMB_BALLOON.ordinal(), bombPool);
		this.registerPool(BalloonType.X2_BALLOON.ordinal(), x2Pool);
	}
	
	public void destroyPools()
	{
		timePool = null;
		bombPool = null;
		x2Pool = null;
	}
}
