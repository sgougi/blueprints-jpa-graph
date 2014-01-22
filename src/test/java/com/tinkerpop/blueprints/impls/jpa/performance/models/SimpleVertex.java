package com.tinkerpop.blueprints.impls.jpa.performance.models;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface SimpleVertex extends VertexFrame {
	
	@Property("strAttr")
	String getStrAttr();

	@Property("strAttr")	
	void setStrAttr(String val);

}
