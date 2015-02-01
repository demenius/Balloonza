package com.bplatz.balloonza.object.pool;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.pool.MultiPool;
import org.andengine.util.adt.pool.Pool;

import com.bplatz.balloonza.manager.ResourceManager;
import com.bplatz.balloonza.object.Cloud;
import com.bplatz.balloonza.object.Cloud.CloudType;
import com.bplatz.balloonza.scene.LogicScene;

public class CloudPool extends MultiPool<Cloud>
{
	private GenericCloudPool cloud1Pool;
	private GenericCloudPool cloud2Pool;
	private GenericCloudPool cloud3Pool;
	private GenericCloudPool cloud4Pool;
	private GenericCloudPool cloud5Pool;
	private GenericCloudPool cloud6Pool;
	private GenericCloudPool cloud7Pool;
	private GenericCloudPool cloud8Pool;
	
	public void initiatePool(final VertexBufferObjectManager vbo, final LogicScene scene)
	{
		int startingClouds = 10;
		cloud1Pool =  new GenericCloudPool(startingClouds)
		{
			@Override
			protected Cloud onAllocatePoolItem()
			{
				return new Cloud(0, 0, vbo, scene, 
						ResourceManager.getInstance().cloud1_region, CloudType.CLOUD_1);
			}
		};
		
		cloud2Pool = new GenericCloudPool(startingClouds)
		{
			@Override
			protected Cloud onAllocatePoolItem()
			{
				return new Cloud(0, 0, vbo, scene,  
						ResourceManager.getInstance().cloud2_region, CloudType.CLOUD_2);
			}
		};
		
		cloud3Pool = new GenericCloudPool(startingClouds)
		{
			@Override
			protected Cloud onAllocatePoolItem()
			{
				return new Cloud(0, 0, vbo, scene, 
						ResourceManager.getInstance().cloud3_region, CloudType.CLOUD_3);
			}
		};
		
		cloud4Pool = new GenericCloudPool(startingClouds)
		{
			@Override
			protected Cloud onAllocatePoolItem()
			{
				return new Cloud(0, 0, vbo, scene, 
						ResourceManager.getInstance().cloud4_region, CloudType.CLOUD_4);
			}
		};
		
		cloud5Pool = new GenericCloudPool(startingClouds)
		{
			@Override
			protected Cloud onAllocatePoolItem()
			{
				return new Cloud(0, 0, vbo, scene, 
						ResourceManager.getInstance().cloud5_region, CloudType.CLOUD_5);
			}
		};

		cloud6Pool = new GenericCloudPool(startingClouds)
		{
			@Override
			protected Cloud onAllocatePoolItem()
			{
				return new Cloud(0, 0, vbo, scene, 
						ResourceManager.getInstance().cloud6_region, CloudType.CLOUD_6);
			}
		};

		cloud7Pool = new GenericCloudPool(startingClouds)
		{
			@Override
			protected Cloud onAllocatePoolItem()
			{
				return new Cloud(0, 0, vbo, scene, 
						ResourceManager.getInstance().cloud7_region, CloudType.CLOUD_7);
			}
		};

		cloud8Pool = new GenericCloudPool(startingClouds)
		{
			@Override
			protected Cloud onAllocatePoolItem()
			{
				return new Cloud(0, 0, vbo, scene, 
						ResourceManager.getInstance().cloud8_region, CloudType.CLOUD_8);
			}
		};
		
		this.registerPool(CloudType.CLOUD_1.ordinal(), cloud1Pool);
		this.registerPool(CloudType.CLOUD_2.ordinal(), cloud2Pool);
		this.registerPool(CloudType.CLOUD_3.ordinal(), cloud3Pool);
		this.registerPool(CloudType.CLOUD_4.ordinal(), cloud4Pool);
		this.registerPool(CloudType.CLOUD_5.ordinal(), cloud5Pool);
		this.registerPool(CloudType.CLOUD_6.ordinal(), cloud6Pool);
		this.registerPool(CloudType.CLOUD_7.ordinal(), cloud7Pool);
		this.registerPool(CloudType.CLOUD_8.ordinal(), cloud8Pool);
	}
	
	public void destroyPools()
	{
		cloud1Pool = null;
		cloud2Pool = null;
		cloud3Pool = null;
		cloud4Pool = null;
		cloud5Pool = null;
		cloud6Pool = null;
		cloud7Pool = null;
		cloud8Pool = null;
	}
	
	private class GenericCloudPool extends Pool<Cloud>
	{
		public GenericCloudPool(int capacity)
		{
			super(capacity);
		}
		
		@Override
		protected void onHandleObtainItem(Cloud cloud)
		{
			super.onHandleObtainItem(cloud);
			cloud.activate();
		}

		@Override
		protected void onHandleRecycleItem(Cloud cloud)
		{
			super.onHandleRecycleItem(cloud);
		    cloud.deactivate();
		}

		@Override
		protected Cloud onAllocatePoolItem()
		{
			return null;
		}
	}
}
