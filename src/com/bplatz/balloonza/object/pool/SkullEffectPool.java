package com.bplatz.balloonza.object.pool;

import org.andengine.util.adt.pool.Pool;

import com.bplatz.balloonza.base.BaseExplosion;
import com.bplatz.balloonza.manager.SceneManager;
import com.bplatz.balloonza.object.effect.Skull;

public class SkullEffectPool extends Pool<BaseExplosion>
{
	
	public SkullEffectPool(int capacity)
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
	protected Skull onAllocatePoolItem()
	{
		return new Skull(SceneManager.getInstance().getLogicScene(), 1, 1);
	}
}