package org.anddev.andengine.entity.sprite;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.entity.shape.RectangularShape;
import org.anddev.andengine.opengl.mesh.HighPerformanceMesh;
import org.anddev.andengine.opengl.mesh.Mesh;
import org.anddev.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.anddev.andengine.opengl.shader.util.constants.ShaderProgramConstants;
import org.anddev.andengine.opengl.texture.region.ITextureRegion;
import org.anddev.andengine.opengl.vbo.HighPerformanceVertexBufferObject;
import org.anddev.andengine.opengl.vbo.VertexBufferObject.DrawType;
import org.anddev.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute;
import org.anddev.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.anddev.andengine.opengl.vbo.attribute.VertexBufferObjectAttributesBuilder;

import android.opengl.GLES20;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 19:22:38 - 09.03.2010
 */
public class Sprite extends RectangularShape<HighPerformanceVertexBufferObject, HighPerformanceMesh> {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int VERTEX_INDEX_X = 0;
	public static final int VERTEX_INDEX_Y = Sprite.VERTEX_INDEX_X + 1;
	public static final int COLOR_INDEX = Sprite.VERTEX_INDEX_Y + 1;
	public static final int TEXTURECOORDINATES_INDEX_U = Sprite.COLOR_INDEX + 1;
	public static final int TEXTURECOORDINATES_INDEX_V = Sprite.TEXTURECOORDINATES_INDEX_U + 1;

	public static final int VERTEX_SIZE = 2 + 1 + 2;
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int SPRITE_SIZE = Sprite.VERTEX_SIZE * Sprite.VERTICES_PER_SPRITE;

	public static final VertexBufferObjectAttributes VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT = new VertexBufferObjectAttributesBuilder(3)
		.add(ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION, 2, GLES20.GL_FLOAT, false)
		.add(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION, ShaderProgramConstants.ATTRIBUTE_COLOR, 4, GLES20.GL_UNSIGNED_BYTE, true)
		.add(ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES, 2, GLES20.GL_FLOAT, false)
		.build();

	// ===========================================================
	// Fields
	// ===========================================================

	protected final ITextureRegion mTextureRegion;

	protected boolean mFlippedVertical;
	protected boolean mFlippedHorizontal;

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * Uses a default {@link Mesh} in {@link DrawType#STATIC} with the {@link VertexBufferObjectAttribute}s: {@link Sprite#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Sprite(final float pX, final float pY, final ITextureRegion pTextureRegion) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, DrawType.STATIC);
	}

	/**
	 * Uses a default {@link Mesh} with the {@link VertexBufferObjectAttribute}s: {@link Sprite#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Sprite(final float pX, final float pY, final ITextureRegion pTextureRegion, final DrawType pDrawType) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, pDrawType);
	}

	/**
	 * Uses a default {@link Mesh} in {@link DrawType#STATIC} with the {@link VertexBufferObjectAttribute}s: {@link Sprite#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Sprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITextureRegion pTextureRegion) {
		this(pX, pY, pWidth, pHeight, pTextureRegion, DrawType.STATIC);
	}

	/**
	 * Uses a default {@link Mesh} with the {@link VertexBufferObjectAttribute}s: {@link Sprite#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public Sprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITextureRegion pTextureRegion, final DrawType pDrawType) {
		this(pX, pY, pWidth, pHeight, pTextureRegion, new HighPerformanceMesh(Sprite.SPRITE_SIZE, pDrawType, true, Sprite.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT));
	}

	public Sprite(final float pX, final float pY, final ITextureRegion pTextureRegion, final HighPerformanceMesh pMesh) {
		this(pX, pY, pTextureRegion.getWidth(), pTextureRegion.getHeight(), pTextureRegion, pMesh);
	}

	public Sprite(final float pX, final float pY, final float pWidth, final float pHeight, final ITextureRegion pTextureRegion, final HighPerformanceMesh pMesh) {
		super(pX, pY, pWidth, pHeight, pMesh, PositionColorTextureCoordinatesShaderProgram.getInstance());

		this.mTextureRegion = pTextureRegion;

		this.setBlendingEnabled(true);
		this.initBlendFunction(pTextureRegion);

		this.onUpdateVertices();
		this.onUpdateColor();
		this.onUpdateTextureCoordinates();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public ITextureRegion getTextureRegion() {
		return this.mTextureRegion;
	}

	public void setFlippedHorizontal(final boolean pFlippedHorizontal) {
		this.mFlippedHorizontal = pFlippedHorizontal;
		this.onUpdateTextureCoordinates();
	}

	public void setFlippedVertical(final boolean pFlippedVertical) {
		this.mFlippedVertical = pFlippedVertical;
		this.onUpdateTextureCoordinates();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void reset() {
		super.reset();

		this.initBlendFunction(this.mTextureRegion);
	}

	@Override
	protected void preDraw(final Camera pCamera) {
		super.preDraw(pCamera);

		this.mTextureRegion.getTexture().bind();

		this.mMesh.preDraw(this.mShaderProgram);
	}

	@Override
	protected void draw(final Camera pCamera) {
		this.mMesh.draw(GLES20.GL_TRIANGLE_STRIP, Sprite.VERTICES_PER_SPRITE);
	}

	@Override
	protected void postDraw(final Camera pCamera) {
		this.mMesh.postDraw(this.mShaderProgram);

		super.postDraw(pCamera);
	}

	@Override
	protected void onUpdateVertices() {
		final HighPerformanceVertexBufferObject vertexBufferObject = this.mMesh.getVertexBufferObject();
		final float[] bufferData = vertexBufferObject.getBufferData();

		final float x = 0;
		final float y = 0;
		final float x2 = this.mWidth;
		final float y2 = this.mHeight;

		bufferData[0 * Sprite.VERTEX_SIZE + Sprite.VERTEX_INDEX_X] = x;
		bufferData[0 * Sprite.VERTEX_SIZE + Sprite.VERTEX_INDEX_Y] = y;

		bufferData[1 * Sprite.VERTEX_SIZE + Sprite.VERTEX_INDEX_X] = x;
		bufferData[1 * Sprite.VERTEX_SIZE + Sprite.VERTEX_INDEX_Y] = y2;

		bufferData[2 * Sprite.VERTEX_SIZE + Sprite.VERTEX_INDEX_X] = x2;
		bufferData[2 * Sprite.VERTEX_SIZE + Sprite.VERTEX_INDEX_Y] = y;

		bufferData[3 * Sprite.VERTEX_SIZE + Sprite.VERTEX_INDEX_X] = x2;
		bufferData[3 * Sprite.VERTEX_SIZE + Sprite.VERTEX_INDEX_Y] = y2;

		vertexBufferObject.setDirtyOnHardware();
	}

	@Override
	protected void onUpdateColor() {
		final HighPerformanceVertexBufferObject vertexBufferObject = this.mMesh.getVertexBufferObject();
		final float[] bufferData = vertexBufferObject.getBufferData();

		final float packedColor = this.mColor.getPacked();

		bufferData[0 * Sprite.VERTEX_SIZE + Sprite.COLOR_INDEX] = packedColor;
		bufferData[1 * Sprite.VERTEX_SIZE + Sprite.COLOR_INDEX] = packedColor;
		bufferData[2 * Sprite.VERTEX_SIZE + Sprite.COLOR_INDEX] = packedColor;
		bufferData[3 * Sprite.VERTEX_SIZE + Sprite.COLOR_INDEX] = packedColor;

		vertexBufferObject.setDirtyOnHardware();
	}

	protected void onUpdateTextureCoordinates() {
		final HighPerformanceVertexBufferObject vertexBufferObject = this.mMesh.getVertexBufferObject();
		final float[] bufferData = vertexBufferObject.getBufferData();

		final ITextureRegion textureRegion = this.mTextureRegion;

		final float u;
		final float v;
		final float u2;
		final float v2;

		if(this.mFlippedVertical) {
			if(this.mFlippedHorizontal) {
				u = textureRegion.getU2();
				u2 = textureRegion.getU();
				v = textureRegion.getV2();
				v2 = textureRegion.getV();
			} else {
				u = textureRegion.getU();
				u2 = textureRegion.getU2();
				v = textureRegion.getV2();
				v2 = textureRegion.getV();
			}
		} else {
			if(this.mFlippedHorizontal) {
				u = textureRegion.getU2();
				u2 = textureRegion.getU();
				v = textureRegion.getV();
				v2 = textureRegion.getV2();
			} else {
				u = textureRegion.getU();
				u2 = textureRegion.getU2();
				v = textureRegion.getV();
				v2 = textureRegion.getV2();
			}
		}

		if(textureRegion.isRotated()) {
			bufferData[0 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_U] = u2;
			bufferData[0 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_V] = v;
	
			bufferData[1 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_U] = u;
			bufferData[1 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_V] = v;
	
			bufferData[2 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_U] = u2;
			bufferData[2 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_V] = v2;
	
			bufferData[3 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_U] = u;
			bufferData[3 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_V] = v2;
		} else {
			bufferData[0 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_U] = u;
			bufferData[0 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_V] = v;
			
			bufferData[1 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_U] = u;
			bufferData[1 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_V] = v2;
			
			bufferData[2 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_U] = u2;
			bufferData[2 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_V] = v;
			
			bufferData[3 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_U] = u2;
			bufferData[3 * Sprite.VERTEX_SIZE + Sprite.TEXTURECOORDINATES_INDEX_V] = v2;
			
		}

		vertexBufferObject.setDirtyOnHardware();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
