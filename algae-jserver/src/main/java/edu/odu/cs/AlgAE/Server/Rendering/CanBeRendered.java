package edu.odu.cs.AlgAE.Server.Rendering;


public interface CanBeRendered<T> {
   public Renderer<T> getRenderer();
}
