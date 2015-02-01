package com.bplatz.balloonza.object.pool;

import org.andengine.util.adt.pool.Pool;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.object.effect.Explosion;

public class ExplosionPool extends Pool<BaseExplosion>
{
	public ExplosionPool(int capacity)
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
		return new Explosion(SceneManager.getInstance().getLogicScene(), Constant.BOMB_SHRAPNEL, 3);
	}
}