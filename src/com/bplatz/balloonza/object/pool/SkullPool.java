package com.bplatz.balloonza.object.pool;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.MultiPool;

import com.bplatz.balloonza.base.BasePool;
import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.object.Balloon;
import com.bplatz.balloonza.object.Balloon.BalloonType;

public class SkullPool extends MultiPool<Balloon>
{
	private BasePool blueSkullPool;
	private BasePool greenSkullPool;
	private BasePool orangeSkullPool;
	private BasePool purpleSkullPool;
	private BasePool redSkullPool;
	private BasePool yellowSkullPool;
	
	public void initiatePool(final VertexBufferObjectManager vbo, final BaseScene scene)
	{
		final int startingBalloons = 10;
		blueSkullPool =  new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().blue_balloon_region, BalloonType.BLUE_SKULL);
			}
		};
		
		greenSkullPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().green_balloon_region, BalloonType.GREEN_SKULL);
			}
		};
		
		orangeSkullPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().orange_balloon_region, BalloonType.ORANGE_SKULL);
			}
		};
		
		purpleSkullPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().purple_balloon_region, BalloonType.PURPLE_SKULL);
			}
		};
		
		redSkullPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().red_balloon_region, BalloonType.RED_SKULL);
			}
		};
		
		yellowSkullPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().yellow_balloon_region, BalloonType.YELLOW_SKULL);
			}
		};
		
		this.registerPool(BalloonType.BLUE_SKULL.ordinal(), blueSkullPool);
		this.registerPool(BalloonType.GREEN_SKULL.ordinal(), greenSkullPool);
		this.registerPool(BalloonType.ORANGE_SKULL.ordinal(), orangeSkullPool);
		this.registerPool(BalloonType.PURPLE_SKULL.ordinal(), purpleSkullPool);
		this.registerPool(BalloonType.RED_SKULL.ordinal(), redSkullPool);
		this.registerPool(BalloonType.YELLOW_SKULL.ordinal(), yellowSkullPool);
	}
	
	public void destroyPools()
	{
		blueSkullPool = null;
		greenSkullPool = null;
		orangeSkullPool = null;
		purpleSkullPool = null;
		redSkullPool = null;
		yellowSkullPool = null;
	}
}
