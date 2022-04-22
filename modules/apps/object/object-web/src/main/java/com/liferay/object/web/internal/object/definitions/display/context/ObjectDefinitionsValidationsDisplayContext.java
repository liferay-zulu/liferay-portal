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

package com.liferay.object.web.internal.object.definitions.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectValidationRule;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.object.validation.rule.ObjectValidationRuleEngineServicesTracker;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Selton Guedes
 */
public class ObjectDefinitionsValidationsDisplayContext
	extends BaseObjectDefinitionsDisplayContext {

	public ObjectDefinitionsValidationsDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectValidationRuleEngineServicesTracker
			objectValidationRuleEngineServicesTracker) {

		super(httpServletRequest, objectDefinitionModelResourcePermission);

		_objectValidationRuleEngineServicesTracker =
			objectValidationRuleEngineServicesTracker;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws Exception {

		return Arrays.asList(
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					getPortletURL()
				).setMVCRenderCommandName(
					"/object_definitions/edit_object_validation_rule"
				).setParameter(
					"objectValidationRuleId", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"view", "view",
				LanguageUtil.get(objectRequestHelper.getRequest(), "view"),
				"get", null, "sidePanel"),
			new FDSActionDropdownItem(
				"/o/object-admin/v1.0/object-validation-rules/{id}", "trash",
				"delete",
				LanguageUtil.get(objectRequestHelper.getRequest(), "delete"),
				"delete", "delete", "async"));
	}

	public List<HashMap<String, Object>> getObjectValidationElements(
		String engine) {

		List<HashMap<String, Object>> elements = new ArrayList<>();

		Collections.addAll(
			elements,
			HashMapBuilder.<String, Object>put(
				"items",
				Stream.of(
					ObjectFieldLocalServiceUtil.getObjectFields(
						getObjectDefinitionId())
				).flatMap(
					List::stream
				).map(
					field -> HashMapBuilder.put(
						"content", field.getName()
					).put(
						"label", field.getLabel(objectRequestHelper.getLocale())
					).put(
						"tooltip", "placeholder"
					).build()
				).collect(
					Collectors.toList()
				)
			).put(
				"label", "Fields"
			).build());

		if (engine.equals("ddm")) {
			Collections.addAll(
				elements,
				HashMapBuilder.<String, Object>put(
					"items",
					Arrays.asList(
						_createObjectValidationElementItem("AND", "and"),
						_createObjectValidationElementItem("OR", "or"),
						_createObjectValidationElementItem(
							"field_reference + field_reference2", "plus"),
						_createObjectValidationElementItem(
							"field_reference - field_reference2", "minus"),
						_createObjectValidationElementItem(
							"field_reference / field_reference2", "divided-by"),
						_createObjectValidationElementItem(
							"field_reference * field_reference2", "times"),
						_createObjectValidationElementItem(
							"field_reference = field_reference - " +
								"field_reference2",
							"equals-or-atribute"))
				).put(
					"label", "Operators"
				).build(),
				HashMapBuilder.<String, Object>put(
					"items",
					Arrays.asList(
						_createObjectValidationElementItem(
							"concat(parameters)", "concat"),
						_createObjectValidationElementItem(
							"contains(field_reference, parameter)", "contains"),
						_createObjectValidationElementItem(
							"NOT(contains(field_reference, parameter))",
							"does-not-contain"),
						_createObjectValidationElementItem(
							"futureDates(field_reference, parameter)",
							"future-dates"),
						_createObjectValidationElementItem(
							"isDecimal(parameter)", "is-decimal"),
						_createObjectValidationElementItem(
							"isEmpty(parameter)", "is-empty"),
						_createObjectValidationElementItem(
							"field_reference == parameter", "is-equal-to"),
						_createObjectValidationElementItem(
							"field_reference > parameter", "is-greater-than"),
						_createObjectValidationElementItem(
							"field_reference >= parameter",
							"is-greater-than-or-equal-to"),
						_createObjectValidationElementItem(
							"isInteger(parameter)", "is-integer"),
						_createObjectValidationElementItem(
							"field_reference < parameter", "is-less-than"),
						_createObjectValidationElementItem(
							"field_reference <= parameter",
							"is-less-than-or-equal-to"),
						_createObjectValidationElementItem(
							"field_reference != parameter", "is-not-equal-to"),
						_createObjectValidationElementItem(
							"isURL(field_reference)", "is-a-url"),
						_createObjectValidationElementItem(
							"isEmailAddress(field_reference)", "is-an-email"),
						_createObjectValidationElementItem(
							"match(field_reference, parameter)", "matches"),
						_createObjectValidationElementItem(
							"pastDates(field_reference, parameter)",
							"past-dates"),
						_createObjectValidationElementItem(
							"futureDates(name, startsFrom, date, unit, " +
								"quantity, endsOn, date, unit, quantity)",
							"range"),
						_createObjectValidationElementItem(
							"sum(parameter)", "sum"))
				).put(
					"label", "Functions"
				).build());
		}

		return elements;
	}

	public List<Map<String, String>> getObjectValidationRuleEngines() {
		return Stream.of(
			_objectValidationRuleEngineServicesTracker.
				getObjectValidationRuleEngines()
		).flatMap(
			List::stream
		).map(
			objectValidationRuleEngine -> HashMapBuilder.put(
				"label",
				LanguageUtil.get(
					objectRequestHelper.getLocale(),
					objectValidationRuleEngine.getName())
			).put(
				"name", objectValidationRuleEngine.getName()
			).build()
		).sorted(
			Comparator.comparing(p -> p.get("label"))
		).collect(
			Collectors.toList()
		);
	}

	public HashMap<String, Object> getProps(
			ObjectValidationRule objectValidationRule)
		throws PortalException {

		return HashMapBuilder.<String, Object>put(
			"objectValidationRule",
			HashMapBuilder.<String, Object>put(
				"active", objectValidationRule.isActive()
			).put(
				"engine", objectValidationRule.getEngine()
			).put(
				"engineLabel",
				LanguageUtil.get(
					objectRequestHelper.getLocale(),
					objectValidationRule.getEngine())
			).put(
				"errorLabel", objectValidationRule.getErrorLabel()
			).put(
				"id", objectValidationRule.getObjectValidationRuleId()
			).put(
				"name", objectValidationRule.getName()
			).put(
				"script", objectValidationRule.getScript()
			).build()
		).put(
			"objectValidationRuleElements",
			getObjectValidationElements(objectValidationRule.getEngine())
		).put(
			"objectValidationRuleEngines", getObjectValidationRuleEngines()
		).put(
			"readOnly", !hasUpdateObjectDefinitionPermission()
		).build();
	}

	@Override
	protected String getAPIURI() {
		return "/object-validation-rules";
	}

	@Override
	protected UnsafeConsumer<DropdownItem, Exception>
		getCreationMenuDropdownItemUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref("addObjectValidation");
			dropdownItem.setLabel(
				LanguageUtil.get(
					objectRequestHelper.getRequest(), "add-object-validation"));
			dropdownItem.setTarget("event");
		};
	}

	private HashMap<String, String> _createObjectValidationElementItem(
		String content, String key) {

		return HashMapBuilder.put(
			"content", content
		).put(
			"label", LanguageUtil.get(objectRequestHelper.getLocale(), key)
		).put(
			"tooltip", "placeholder"
		).build();
	}

	private final ObjectValidationRuleEngineServicesTracker
		_objectValidationRuleEngineServicesTracker;

}