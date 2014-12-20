package edu.odu.cs.AlgAE.Server.Rendering;


public interface ObjectRenderer<T> extends Renderer<T> {
	public T appliesTo();
}
