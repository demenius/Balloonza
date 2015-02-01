package com.bplatz.balloonza.base;

import org.andengine.util.adt.pool.Pool;

import com.bplatz.balloonza.object.Balloon;

public abstract class BasePool extends Pool<Balloon>
{
	
	public BasePool(int capacity)
	{
		super(capacity);
	}	
	
	@Override
	protected void onHandleObtainItem(Balloon balloon)
	{
		super.onHandleObtainItem(balloon);
		balloon.activate();
	}
	
	@Override
	protected void onHandleRecycleItem(Balloon balloon)
	{
		super.onHandleRecycleItem(balloon);
		balloon.deactivate();
	}
	
	@Override
	protected Balloon onAllocatePoolItem()
	{
		return null;
	}
}