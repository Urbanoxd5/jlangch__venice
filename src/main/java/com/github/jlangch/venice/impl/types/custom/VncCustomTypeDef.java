/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2020 Venice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jlangch.venice.impl.types.custom;

import java.util.List;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncMap;


public class VncCustomTypeDef {

	public VncCustomTypeDef(
			final VncKeyword type,
			final List<VncCustomTypeFieldDef> fieldDefs,
			final VncFunction validationFn
	) {
		this.type = type;
		this.fieldDefs = fieldDefs;
		this.validationFn = validationFn;
	}


    public VncKeyword getType() {
		return type;
	}
 
    public VncCustomTypeFieldDef getFieldDef(final int index) {
    	if (index >= 0 && index < fieldDefs.size()) {
    		return fieldDefs.get(index);
    	}
    	else {
			throw new VncException(String.format(
					"deftype: field def index %d out of bounds.", index)); 
    	}
	}

	public List<VncCustomTypeFieldDef> getFieldDefs() {
		return fieldDefs;
	}

	public VncFunction getValidationFn() {
		return validationFn;
	}
	
    public int count() {
		return fieldDefs.size();
	}

    public void validate(final VncMap data) {
		if (validationFn != null) {
			validationFn.apply(VncList.of(data));
		}
    }
    

	private final VncKeyword type;
    private final List<VncCustomTypeFieldDef> fieldDefs;
    private final VncFunction validationFn;
}
