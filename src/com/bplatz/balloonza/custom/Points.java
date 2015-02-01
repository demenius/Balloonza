package com.bplatz.balloonza.custom;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SingleValueSpanEntityModifier;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.IFont;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.bplatz.balloonza.manager.ResourceManager;

public class Points extends Entity
{
	private final float POINTS_ADD_TIME = 0.5f;
	private Text pointsText;
	private int points;
	private final ScoreModifier scoreModifier = new ScoreModifier(POINTS_ADD_TIME, 0, 0);
	private final ScaleModifier scaleModifier = new ScaleModifier(POINTS_ADD_TIME, 1.5f, 1.0f);

	public Points(final float pX, final float pY, final IFont pFont, final CharSequence pText, final TextOptions pTextOptions, final VertexBufferObjectManager pVertexBufferObjectManager) 
	{
		super(pX, pY);
		final Text scoreText1 = new Text(0, 0, pFont, pText, pTextOptions, pVertexBufferObjectManager);
		pointsText = new Text(75, 0, pFont, "0,123,456,789", pTextOptions, pVertexBufferObjectManager);
		points = 0;
		pointsText.setText("0");
		pointsText.setAnchorCenterX(0f);
		scoreModifier.setAutoUnregisterWhenFinished(false);
		scaleModifier.setAutoUnregisterWhenFinished(false);
		pointsText.registerEntityModifier(scoreModifier);
		pointsText.registerEntityModifier(scaleModifier);
		this.attachChild(scoreText1);
		this.attachChild(pointsText);
	}
	
	public void addToScore(int s)
	{
		if(!scoreModifier.isFinished())
		{
			s += scoreModifier.getToValue() - points; // Add What Is Left From The Previous Score
		}
		int toValue = points + s;
		if(toValue < 0)
			toValue = 0;
			
		scoreModifier.reset(POINTS_ADD_TIME, points, toValue);
		scaleModifier.reset();
	}
	
	public void remove()
	{
		pointsText.detachSelf();
		pointsText.dispose();
		pointsText = null;
		
		detachSelf();
		dispose();
	}
	
	@Override
	public void reset()
	{
		super.reset();
		points = 0;
		pointsText.setText("0");
		scoreModifier.reset(0.01f, 0, 0);
	}
	
	public int getPoints()
	{
		if(scoreModifier.isFinished())
			return points;
		else
			return (int) scoreModifier.getToValue();
	}
	
	private class ScoreModifier extends SingleValueSpanEntityModifier
	{

		public ScoreModifier(float pDuration, float pFromValue, float pToValue)
		{
			super(pDuration, pFromValue, pToValue);
		}

		@Override
		protected void onSetInitialValue(IEntity pItem, float pValue)
		{
			// Do Nothing
		}

		@Override
		protected void onSetValue(IEntity pItem, float pPercentageDone, float pValue)
		{
			Points.this.points = (int) pValue;
			Points.this.pointsText.setText(ResourceManager.FORMATTER.format(Points.this.points));
		}

		@Override
		public IEntityModifier deepCopy() throws DeepCopyNotSupportedException
		{
			return new ScoreModifier(mDuration, super.getFromValue(), super.getToValue());
		}
	}
}
