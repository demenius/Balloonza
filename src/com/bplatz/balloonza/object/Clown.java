package com.bplatz.balloonza.object;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.SceneManager;

public class Clown extends Sprite
{
	private final float CLOWN_MOVE_DURATION = 0.5f;
	private final static float CLOWN_OUT_X = 525;
	private final static float CLOWN_OUT_Y = -75;
	private final float CLOWN_IN_X;
	private final float CLOWN_IN_Y;
	
	private MoveModifier moveIn;
	private MoveModifier moveOut;
	
	private boolean active = false;
	
	private TiledSprite dialogs;
	
	//Fat Kid Is Active For 30s. Then Fades Out And Registers Activator
	private final TimerHandler RUNNER = new TimerHandler(Constant.CLOWN_ACTIVE_TIME, false, 
	new ITimerCallback()
	{
		@Override
		public void onTimePassed(TimerHandler pTimerHandler)
		{
			SceneManager.getInstance().getLogicScene().unregisterUpdateHandler(RUNNER);
			active = false;
			moveOut.reset();
			Clown.this.registerEntityModifier(moveOut);
		}
	});
	

	public Clown(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(CLOWN_OUT_X, CLOWN_OUT_Y, pTextureRegion, pVertexBufferObjectManager);
		dialogs = new TiledSprite(-70, 175, ResourceManager.getInstance().clown_dialog_region, ResourceManager.getInstance().vbom);
		attachChild(dialogs);
		setVisible(false);
		CLOWN_IN_X = pX;
		CLOWN_IN_Y = pY;
		moveIn = new MoveModifier(CLOWN_MOVE_DURATION, CLOWN_OUT_X, CLOWN_OUT_Y, CLOWN_IN_X, CLOWN_IN_Y, new IEntityModifierListener()
		{

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				setVisible(true);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				dialogs.setVisible(true);
				SceneManager.getInstance().getLogicScene().registerUpdateHandler(RUNNER);
			}
			
		});
		
		moveOut = new MoveModifier(CLOWN_MOVE_DURATION, CLOWN_IN_X, CLOWN_IN_Y, CLOWN_OUT_X, CLOWN_OUT_Y, new IEntityModifierListener()
		{

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
			{
				dialogs.setVisible(false);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
			{
				setVisible(false);
			}
			
		});
		dialogs.setVisible(false);
	}
	
	@Override
	protected void preDraw(GLState pGLState, Camera pCamera)
	{
		super.preDraw(pGLState, pCamera);
		pGLState.enableDither();
	}
	
	public void remove()
	{
		dialogs.detachSelf();
		dialogs.dispose();
		dialogs = null;
		
		detachSelf();
		dispose();
	}
	
	public boolean activate(int dialog)
	{
		dialogs.setCurrentTileIndex(dialog);
		RUNNER.reset();
		if(active)
		{
			return true;
		}
		active = true;
		if(!moveOut.isFinished())
		{
			this.unregisterEntityModifier(moveOut);
			float dur = moveOut.getSecondsElapsed();
			dur = (dur == 0 ? CLOWN_MOVE_DURATION : dur);
			Log.d("GAME", " DUR: " + dur);
			final float fromX = this.getX();
			final float fromY = this.getY();
			moveIn.reset(dur, fromX, CLOWN_IN_X, fromY, CLOWN_IN_Y);
		} else
			moveIn.reset(CLOWN_MOVE_DURATION, CLOWN_OUT_X, CLOWN_IN_X, CLOWN_OUT_Y, CLOWN_IN_Y);
		this.registerEntityModifier(moveIn);
		
		return true;
	}
}
