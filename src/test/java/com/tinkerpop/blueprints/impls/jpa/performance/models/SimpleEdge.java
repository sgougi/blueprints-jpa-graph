package com.tinkerpop.blueprints.impls.jpa.performance.models;

import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.Property;

public interface SimpleEdge extends EdgeFrame {

	@Property("strAttr")
	String getStrAttr();

	@Property("strAttr")	
	void setStrAttr(String val);
	
}
