/*
 * Copyright 2011, 2012 Odysseus Software GmbH
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
package de.odysseus.staxon.json.stream.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import de.odysseus.staxon.json.stream.JsonStreamTarget;

/**
 * Target-filter to auto-convert string values to primitive (boolean, number, null) values.
 */
public class AutoPrimitiveTarget extends StreamTargetDelegate {
	private final Pattern number = Pattern.compile("^-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]{1,10})?$");
	private final boolean convertAttributes;
	private final String attributePrefix;
	
	private String lastName;

	public AutoPrimitiveTarget(JsonStreamTarget delegate, boolean convertAttributes, String attributePrefix) {
		super(delegate);
		this.convertAttributes = convertAttributes;
		this.attributePrefix = attributePrefix;
	}

	@Override
	public void name(String name) throws IOException {
		lastName = name;
		super.name(name);
	}
	
	@Override
	public void value(Object value) throws IOException {
		if (value instanceof String && (convertAttributes || !lastName.startsWith(attributePrefix))) {
			if ("true".equals(value)) {
				super.value(Boolean.TRUE);
			} else if ("false".equals(value)) {
				super.value(Boolean.FALSE);
			} else if ("null".equals(value)) {
				super.value(null);
			} else if (number.matcher(value.toString()).matches()) {
				try {
					super.value(new BigDecimal(value.toString()));
				} catch (NumberFormatException e) {
					super.value(value); // fall back to string
				}
			} else {
				super.value(value);
			}
		} else {
			super.value(value);
		}
	}
}
