package com.bplatz.balloonza.custom;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.IModifier;

import com.bplatz.balloonza.base.Constant;
import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.manager.SceneManager;

public class LifeBar extends Entity
{
	private Rectangle progress;
	private Rectangle progressBorder;
	private boolean depleted = false;
	
	private final ScaleModifier SCALE_UP = new ScaleModifier(0.25f, 0, 1, 1, 1, new IEntityModifierListener()
	{
		@Override
		public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem)
		{
			ResourceManager.getInstance().clock_slow.play();
		}
		public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem){}
		
	});
	private final DelayModifier DELAY = new DelayModifier(Constant.FREEZE_TIME, new IEntityModifierListener()
	{
		public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem){}

		@Override
		public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem)
		{
			ResourceManager.getInstance().clock_fast.play();
		}
		
	});
	private final ScaleModifier SCALE_DOWN = new ScaleModifier(0.25f, 1, 0, 1, 1);
	
	private final LifeController lifeController = new LifeController();
	
	private FreezeModifier freezeModifier;
	private Rectangle freezeBar;
	
	public LifeBar(final float pX, final float pY, final float pWidth, final float pHeight)
	{
		super(pX, pY, pWidth + 6, pHeight + 6);
		createBar();
	}
	
	private void createBar()
	{
		final float bHeight = super.mHeight / 2;
		final float bWidth = super.mWidth / 2;
		Rectangle border = new Rectangle(bWidth, bHeight, mWidth, mHeight, ResourceManager.getInstance().vbom);
		border.setColor(Color.BLACK);
		attachChild(border);
		
		Rectangle background = new Rectangle(bWidth, bHeight, mWidth - 6, mHeight - 6, ResourceManager.getInstance().vbom);
		background.setColor(0.84f, 0.84f, 0.84f);
		attachChild(background);
		
		progressBorder = new Rectangle(mWidth - 6, bHeight, 6, mHeight - 6, ResourceManager.getInstance().vbom);
		progressBorder.setColor(Color.BLACK);
		attachChild(progressBorder);
		
		progress = new Rectangle(3, bHeight, mWidth - 6, mHeight - 6, ResourceManager.getInstance().vbom)
		{
			@Override
			public void setScale(final float pScaleX, final float pScaleY)
			{
				super.setScale(pScaleX, pScaleY);
				final float pX = super.getX() + (super.getWidth() * super.getScaleX());
				progressBorder.setPosition(pX, progressBorder.getY());
			}
		};
		
		progress.setAnchorCenterX(0.0f);
		attachChild(progress);
		
		createFreezeModifier();
	}
	
	public void registerScaler()
	{
		progress.registerEntityModifier(lifeController.scaler);
		if(freezeBar.isVisible())
		{
			freezeBar.registerEntityModifier(freezeModifier);
		}
	}
	
	public void unregisterScaler()
	{
		progress.unregisterEntityModifier(lifeController.scaler);
		if(freezeBar.isVisible())
		{
			freezeBar.unregisterEntityModifier(freezeModifier);
		}
	}
	
	private void createFreezeModifier()
	{
		freezeBar = new Rectangle(mWidth / 2, mHeight / 2, mWidth, mHeight, ResourceManager.getInstance().vbom);
		freezeBar.setAlpha(0.6f);
		freezeBar.setColor(1f, 0.87f, 0f);
		freezeBar.setScale(0);
		freezeBar.setVisible(false);
		attachChild(freezeBar);
		
		freezeModifier = new FreezeModifier(new IEntityModifierListener()
		{
			@Override
			public void onModifierStarted(final IModifier<IEntity> pModifier, final IEntity pItem)
			{
				freezeBar.setVisible(true);
				lifeController.negateNegativeEffects();
			}
			
			@Override
			public void onModifierFinished(final IModifier<IEntity> pEntityModifier, final IEntity pEntity)
			{
				freezeBar.setVisible(false);
				SCALE_UP.reset(0.25f, 0, 1, 1, 1);
			}
		});
		
	}
	
	/*
	 * Increase Progress By A Certain Percentage IE. If The Width Of The
	 * Progress Bar Is 500, And You Want To Increase By 1%. Add 5 To Width
	 */
	public void lifeGain(final float percent)
	{
		if (percent < 0 && freezeBar.isVisible())
			return;
		if (depleted)
			return;
		lifeController.scaleLife(percent);
	}
	
	public boolean isDepleted()
	{
		return depleted;
	}
	
	public void setColor(Color color)
	{
		progress.setColor(color);
	}
	
	public void setColor(final float r, final float g, final float b)
	{
		progress.setColor(r, g, b);
	}
	
	public void timeFreeze()
	{
		if (!SceneManager.getInstance().gameStarted())
			return;
		if (freezeModifier.finished())
		{
			freezeModifier.reset();
			freezeBar.registerEntityModifier(freezeModifier);
		} else
		{
			freezeModifier.extend();
		}
	}
	
	@Override
	public void reset()
	{
		super.reset();
		depleted = false;
		progress.setScale(1.0f);
		progress.setAnchorCenterX(0);
		freezeBar.setVisible(false);
		lifeController.scaler.reset(Constant.TIME_TO_DIE, 1, 0, 1, 1);
		unregisterScaler();
	}
	
	private class FreezeModifier extends SequenceEntityModifier
	{
		
		public FreezeModifier(IEntityModifierListener listener)
		{
			super(listener, SCALE_UP, DELAY, SCALE_DOWN);
		}
		
		public boolean finished()
		{
			return !freezeBar.isVisible();
		}
		
		public void extend()
		{
			if (DELAY.isFinished())
			{
				final float fromScale = freezeBar.getScaleX();
				SCALE_UP.reset(0.25f * (1 - fromScale), fromScale, 1, 1, 1);
				reset();
			} else
			{
				DELAY.reset();
			}
		}
	}
	
	public LifeController getController()
	{
		return this.lifeController;
	}
	
	private class LifeController implements ITimerCallback
	{
		public final ScaleModifier scaler = new ScaleModifier(Constant.TIME_TO_DIE, 1, 0, 1, 1);
		
		public LifeController()
		{
			scaler.setAutoUnregisterWhenFinished(false);
		}
		
		public void scaleLife(final float percent)
		{
			final float fromScale = progress.getScaleX();
			float toScale = fromScale + percent;
			
			if (scaler.getToValueA() > 0)
			{
				final float scaleLeft = scaler.getToValueA() - fromScale;
				toScale += scaleLeft;
			}
			
			if (toScale > 1)
				toScale = 1;
			if (toScale < 0)
				toScale = 0;
			
			final float duration = Math.abs(toScale - fromScale) / Constant.PERCENT_PER_SECOND;
			if (duration > 0)
				scaler.reset(duration, fromScale, toScale, 1, 1);
		}
		
		public void negateNegativeEffects()
		{
			if (scaler.getToValueA() < scaler.getFromValueA())
			{
				final float scale = progress.getScaleX();
				scaler.reset(0.01f, scale, scale, 1, 1); // Stop Negative
															// Effects
			}
		}
		
		@Override
		public void onTimePassed(TimerHandler pTimerHandler)
		{
			if (scaler.isFinished() && !freezeBar.isVisible())
			{
				final float fromScale = progress.getScaleX();
				final float toScale = 0.0f;
				
				final float duration = fromScale / (Constant.LIFEBAR_PERCENT_DECREASE_PER_SECOND + ResourceManager.getInstance().gameVariable.progress_extra_speed);
				if (duration > 0)
					scaler.reset(duration, fromScale, toScale, 1, 1);
			}
			if (progress.getScaleX() <= 0)
			{
				SceneManager.getInstance().displayGameOverText();
			}
		}
	}
}