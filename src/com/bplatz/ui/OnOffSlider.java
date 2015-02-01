package com.bplatz.ui;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.Color;
import android.view.MotionEvent;

import com.bplatz.balloonza.manager.ResourceManager;

public class OnOffSlider extends Rectangle
{
	private TiledSprite selector;
	private float onWidth;
	private boolean inBounds = false;
	private float startX;
	private boolean sliding;
	private final float SLIDING_SPACE = 20;
	private final static int ON_INDEX = 0;
	private final static int OFF_INDEX = 1;
	
	private final static float PADDING = 250;
	
	public enum OnOffState
	{
		ON, OFF;
		
		public static OnOffState opposite(OnOffState state)
		{
			if(state == ON)
				return OFF;
			else 
				return ON;
		}
	}
	
	private OnOffState currentState = OnOffState.OFF;
	private Callback callback;

	public OnOffSlider(final float pX, final float pY, VertexBufferObjectManager pVertextBufferObjectManager, Callback pCallback)
	{
		this(pX, pY, 100, 50, pVertextBufferObjectManager, pCallback);
	}
	
	private OnOffSlider(final float pX, final float pY, final float pWidth, final float pHeight, VertexBufferObjectManager pVertexBufferObjectManager, Callback pCallback)
	{
		super(pX-PADDING/4, pY, pWidth+PADDING, pHeight+PADDING, pVertexBufferObjectManager);
		onWidth = pWidth;
		callback = pCallback;
		createSelector();
	}
	
	private void createSelector()
	{
		super.setAnchorCenterX(0.0f);
		super.setColor(Color.TRANSPARENT);
		Sprite slider = new Sprite(mWidth/2, mHeight/2, ResourceManager.getInstance().on_off_slider_region, ResourceManager.getInstance().vbom);
		attachChild(slider);
		selector = new TiledSprite(onWidth/4+(PADDING/2), mHeight/2, ResourceManager.getInstance().on_off_button_region, ResourceManager.getInstance().vbom);
		selector.setCurrentTileIndex(OFF_INDEX);
		attachChild(selector);
	}
	
	public void changeState(final OnOffState pNewState)
	{
		if(pNewState == OnOffState.ON)
		{
			selector.setCurrentTileIndex(ON_INDEX);
			selector.setX(onWidth - onWidth/4 + (PADDING/2));
		} else
		{
			selector.setCurrentTileIndex(OFF_INDEX);
			selector.setX(onWidth/4+(PADDING/2));
		}
		
		if(pNewState == currentState)
			return;
		sliding = true;
		ResourceManager.getInstance().button_click.play();
		currentState = pNewState;
		
		if(callback != null)
			callback.onStateChange(pNewState);
	}
	
	public void setState(final OnOffState pNewState)
	{
		if(pNewState == OnOffState.ON)
		{
			selector.setCurrentTileIndex(ON_INDEX);
			selector.setX(onWidth - onWidth/4 + (PADDING/2));
		} else
		{
			selector.setCurrentTileIndex(OFF_INDEX);
			selector.setX(onWidth/4+(PADDING/2));
		}
		currentState = pNewState;
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY)
	{
		int myEventAction = pSceneTouchEvent.getAction();
		
		final float X = pTouchAreaLocalX;
		final float leftX = onWidth/4 + (PADDING/2);
		final float rightX = onWidth - onWidth/4 + (PADDING/2);
		switch (myEventAction)
		{
			case MotionEvent.ACTION_DOWN:
				if(pTouchAreaLocalY > (PADDING/2) && pTouchAreaLocalY < mHeight - (PADDING/2))
					inBounds = true;
				startX = X;
				sliding = false;
				break;
			case MotionEvent.ACTION_MOVE:
			{
				if(!inBounds)
					return false;
				if(X < leftX)
				{
					changeState(OnOffState.OFF);
				} else if(X > rightX)
				{
					changeState(OnOffState.ON);
				} else if(sliding || Math.abs(X - startX) >= SLIDING_SPACE)
				{
					sliding = true;
					selector.setX(X);
				}
				if(pTouchAreaLocalY > (PADDING/2) && pTouchAreaLocalY < mHeight - (PADDING/2))
					break;
			}
			case MotionEvent.ACTION_UP:
				if(!inBounds)
					return false;
				if(!sliding)
				{
					changeState(OnOffState.opposite(currentState));
				} else if(X > OnOffSlider.this.mWidth/2)
				{
					changeState(OnOffState.ON);
				} else
				{
					changeState(OnOffState.OFF);
				}
				inBounds = false;
				break;
		}
		return false;
	}
	
	public interface Callback
	{
		public void onStateChange(OnOffState pState);
	}
}
