/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.object.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Marco Leo
 */
public class ObjectValidationRuleEngineException extends PortalException {

	public ObjectValidationRuleEngineException() {
	}

	public ObjectValidationRuleEngineException(String msg) {
		super(msg);
	}

	public ObjectValidationRuleEngineException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public ObjectValidationRuleEngineException(Throwable throwable) {
		super(throwable);
	}

	public static class InvalidScript
		extends ObjectValidationRuleEngineException {

		public InvalidScript() {
			super(
				"There was an unexpected error in fields validation. Please " +
					"contact support.");
		}

	}

	public static class RequiredBusinessRule
		extends ObjectValidationRuleEngineException {

		public RequiredBusinessRule(String errorLabel) {
			super(errorLabel);
		}

	}

}