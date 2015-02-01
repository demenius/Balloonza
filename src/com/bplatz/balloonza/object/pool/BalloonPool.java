package com.bplatz.balloonza.object.pool;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.MultiPool;

import com.bplatz.balloonza.base.BasePool;
import com.bplatz.balloonza.base.BaseScene;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.object.Balloon;
import com.bplatz.balloonza.object.Balloon.BalloonType;

public class BalloonPool extends MultiPool<Balloon>
{
	private BasePool blueBalloonPool;
	private BasePool greenBalloonPool;
	private BasePool orangeBalloonPool;
	private BasePool purpleBalloonPool;
	private BasePool redBalloonPool;
	private BasePool yellowBalloonPool;
	
	public void initiatePool(final VertexBufferObjectManager vbo, final BaseScene scene)
	{
		final int startingBalloons = 10;
		blueBalloonPool =  new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().blue_balloon_region, BalloonType.BLUE_BALLOON);
			}
		};
		
		greenBalloonPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().green_balloon_region, BalloonType.GREEN_BALLOON);
			}
		};
		
		orangeBalloonPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().orange_balloon_region, BalloonType.ORANGE_BALLOON);
			}
		};
		
		purpleBalloonPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().purple_balloon_region, BalloonType.PURPLE_BALLOON);
			}
		};
		
		redBalloonPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene, 
						ResourceManager.getInstance().red_balloon_region, BalloonType.RED_BALLOON);
			}
		};
		
		yellowBalloonPool = new BasePool(startingBalloons)
		{
			@Override
			protected Balloon onAllocatePoolItem()
			{
				return new Balloon(0, 0, vbo, scene,  
						ResourceManager.getInstance().yellow_balloon_region, BalloonType.YELLOW_BALLOON);
			}
		};
		
		this.registerPool(BalloonType.BLUE_BALLOON.ordinal(), blueBalloonPool);
		this.registerPool(BalloonType.GREEN_BALLOON.ordinal(), greenBalloonPool);
		this.registerPool(BalloonType.ORANGE_BALLOON.ordinal(), orangeBalloonPool);
		this.registerPool(BalloonType.PURPLE_BALLOON.ordinal(), purpleBalloonPool);
		this.registerPool(BalloonType.RED_BALLOON.ordinal(), redBalloonPool);
		this.registerPool(BalloonType.YELLOW_BALLOON.ordinal(), yellowBalloonPool);
	}
	
	public void destroyPools()
	{
		blueBalloonPool = null;
		greenBalloonPool = null;
		orangeBalloonPool = null;
		purpleBalloonPool = null;
		redBalloonPool = null;
		yellowBalloonPool = null;
	}
}
