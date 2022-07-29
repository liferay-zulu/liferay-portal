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

package com.liferay.object.internal.validation.rule;

import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.exception.ObjectValidationRuleScriptException;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.object.validation.rule.ObjectValidationRuleEngine;
import com.liferay.object.validation.rule.ObjectValidationRuleEngineTracker;
import com.liferay.object.validation.rule.ObjectValidationRuleExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Feliphe Marinho
 */
@Component(service = ObjectValidationRuleExecutor.class)
public class ObjectValidationRuleExecutorImpl
	implements ObjectValidationRuleExecutor {

	@Override
	public void executeObjectValidations(
			BaseModel<?> baseModel, long objectDefinitionId)
		throws PortalException {

		if (baseModel == null) {
			return;
		}

		HashMapBuilder.HashMapWrapper<String, Object> hashMapWrapper =
			HashMapBuilder.<String, Object>putAll(
				baseModel.getModelAttributes());

		if (baseModel instanceof ObjectEntry) {
			Map<String, Serializable> values =
				_objectEntryLocalService.getValues((ObjectEntry)baseModel);

			if (values != null) {
				hashMapWrapper.putAll(values);
			}
		}

		List<ObjectValidationRule> objectValidationRules =
			_objectValidationRuleLocalService.getObjectValidationRules(
				objectDefinitionId, true);

		for (ObjectValidationRule objectValidationRule :
				objectValidationRules) {

			ObjectValidationRuleEngine objectValidationRuleEngine =
				_objectValidationRuleEngineTracker.
					getObjectValidationRuleEngine(
						objectValidationRule.getEngine());

			Map<String, Object> results = objectValidationRuleEngine.execute(
				hashMapWrapper.build(), objectValidationRule.getScript());

			if (GetterUtil.getBoolean(results.get("invalidScript"))) {
				throw new ObjectValidationRuleScriptException(
					"Script is invalid");
			}

			if (GetterUtil.getBoolean(results.get("invalidFields"))) {
				throw new ObjectValidationRuleEngineException(
					objectValidationRule.getErrorLabel(
						LocaleUtil.getMostRelevantLocale()));
			}
		}
	}

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private ObjectValidationRuleEngineTracker
		_objectValidationRuleEngineTracker;

	@Reference
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

}