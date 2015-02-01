package com.bplatz.balloonza.object.pool;

import org.andengine.util.adt.pool.Pool;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.object.effect.Confetti;

public class ConfettiPool extends Pool<BaseExplosion>
{
	
	public ConfettiPool(int capacity)
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
		return new Confetti(SceneManager.getInstance().getLogicScene(), Constant.CONFETTI_SHRAPNEL, 18);
	}
}