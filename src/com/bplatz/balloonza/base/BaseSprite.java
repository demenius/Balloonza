package com.bplatz.balloonza.base;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class BaseSprite extends Sprite
{

	public BaseSprite(float pX, float pY, ITextureRegion pTextureRegion, VertexBufferObjectManager vbom)
	{
		super(pX, pY, pTextureRegion, vbom);
	}
	
	@Override
	protected void preDraw(GLState pGLState, Camera pCamera)
	{
		super.preDraw(pGLState, pCamera);
		pGLState.enableDither();
	}
}
