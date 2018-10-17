/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2018 Venice
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
package com.github.jlangch.venice.impl.types;

import java.math.BigDecimal;

import com.github.jlangch.venice.VncException;


public class VncLong extends VncVal {

	public VncLong(final Long v) { 
		value = v; 
	}
	
	public VncLong(final Integer v) { 
		value = v.longValue(); 
	}
	
	public VncLong copy() { 
		final VncLong v = new VncLong(value);
		v.setMeta(getMeta());
		return v;
	}

	public Long getValue() { 
		return value; 
	}
	
	public Integer getIntValue() { 
		return value.intValue(); 
	}
		
	public VncLong inc() {
		return new VncLong(value + 1);
	}
	
	public VncLong dec() {
		return new VncLong(value - 1);
	}

	public VncDouble toDouble() {
		return new VncDouble(Double.valueOf(value.longValue()));
	}

	public VncBigDecimal toDecimal() {
		return new VncBigDecimal(new BigDecimal(value.longValue()));
	}

	@Override 
	public int compareTo(final VncVal o) {
		if (o instanceof VncLong) {
			return value.compareTo(((VncLong)o).getValue());
		}
		else if (o instanceof VncDouble) {
			return new Double(value.doubleValue()).compareTo(((VncDouble)o).getValue());
		}
		else if (o instanceof VncBigDecimal) {
			return new BigDecimal(value.doubleValue()).compareTo(((VncBigDecimal)o).getValue());
		}
		else {
			throw new VncException(String.format(
									"Function 'compareTo' with operand 1 of type %s does not allow %s as operand 2", 
									this.getClass().getSimpleName(),
									o.getClass().getSimpleName()));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VncLong other = (VncLong) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return value.toString();
	}


	private final Long value;
}