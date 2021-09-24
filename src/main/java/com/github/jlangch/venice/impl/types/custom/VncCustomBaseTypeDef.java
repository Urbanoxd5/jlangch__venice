package com.github.jlangch.venice.impl.types.custom;

import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.TypeRank;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncMap;
import com.github.jlangch.venice.impl.util.MetaUtil;


public abstract class VncCustomBaseTypeDef extends VncVal {

	public VncCustomBaseTypeDef(final VncKeyword type) {	
		super(Constants.Nil);
		
		this.type = type.withMeta(MetaUtil.typeMeta());
	}

	public abstract VncMap toMap();

	@Override
	public VncVal withMeta(final VncVal meta) {
		return this; // not supported
	}

	@Override
	public VncKeyword getType() {
		return type.withMeta(
				MetaUtil.typeMeta(new VncKeyword(VncVal.TYPE)));
	}

	@Override 
	public TypeRank typeRank() {
		return TypeRank.CUSTOM_TYPE_DEF;
	}
	
	@Override
	public Object convertToJavaObject() {
		return null; // not supported
	}

	@Override 
	public int compareTo(final VncVal o) {
		if (o == Constants.Nil) {
			return 1;
		}
		else if (o instanceof VncCustomBaseTypeDef) {
			return type.getValue().compareTo(((VncCustomBaseTypeDef)o).type.getValue());
		}

		return super.compareTo(o);
	}

	@Override 
	public String toString() {
		return ":" + type.getValue();
	}
	
	public String toString(final boolean print_readably) {
		return toString();
	}
	
	
	private static final long serialVersionUID = -1639883423759533879L;
	
	private final VncKeyword type;
}
