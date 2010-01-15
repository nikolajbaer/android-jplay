package com.nikolajbaer.render;

public interface Renderable {
    // CONSIDER do i need scale for this?

    /*  getWorldTransform 
     *  @returns 3 floats, {x,y,a} (a is the world angle from [0,1] ccw) 
     */
    public abstract float[] getWorldTransform();

    /*  getRenderKey 
     *  @returns the current render key
     *  this indicates which renderer should be used
     */
    public abstract String getRenderKey();

    /*  getRenderObject
     *   @returns  the current RendererUsed by this object
     */
    public abstract RenderObject getRenderObject();

    /* setRenderObject */
    public abstract void setRenderObject(RenderObject ro);

    public abstract void clearRenderObject();
}
