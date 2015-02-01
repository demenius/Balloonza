package com.bplatz.balloonza.object.pool;

import org.andengine.util.adt.pool.Pool;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.object.effect.Clock;

public class ClockPool extends Pool<BaseExplosion>
{
	public ClockPool(int capacity)
	{
		super(capacity);
	}
	
	@Override
	protected void onHandleObtainItem(BaseExplosion explosion)
	{
		super.onHandleObtainItem(explosion);
	}

	@Override
	protected void onHandleRecycleItem(BaseExplosion explosion)
	{
		super.onHandleRecycleItem(explosion);
	}

	@Override
	protected BaseExplosion onAllocatePoolItem()
	{
		return new Clock(SceneManager.getInstance().getLogicScene(), Constant.CLOCK_SHRAPNEL, 1);
	}
}