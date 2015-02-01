package com.bplatz.balloonza.object;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class IndexSprite extends AnimatedSprite
{
	public final int index;
	public float explosionTime = 0f;
	
	public IndexSprite(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion,
			final VertexBufferObjectManager pVertexBufferObjectManager, final int pIndex)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		index = pIndex;
	}
	
	@Override
	public void reset()
	{
		super.reset();
		explosionTime = 0f;
	}
}
