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

package com.liferay.object.web.internal.object.definitions.display.context.util;

import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(immediate = true, service = {})
public class ObjectCodeEditorUtil {

	public static List<Map<String, Object>> getCodeEditorElements(
		boolean includeDDMExpressionBuilderElements, Locale locale,
		long objectDefinitionId) {

		List<Map<String, Object>> codeEditorElements = new ArrayList<>();

		codeEditorElements.add(
			_createCodeEditorElement(
				ListUtil.toList(
					_objectFieldLocalService.getObjectFields(
						objectDefinitionId),
					objectField -> HashMapBuilder.put(
						"content", objectField.getName()
					).put(
						"label", objectField.getLabel(locale)
					).put(
						"tooltip", StringPool.BLANK
					).build()),
				"fields", locale));

		if (includeDDMExpressionBuilderElements) {
			Collections.addAll(
				codeEditorElements,
				_createCodeEditorElement(
					DDMExpressionOperator.getItems(locale), "operators",
					locale),
				_createCodeEditorElement(
					DDMExpressionFunction.getItems(locale), "functions",
					locale));
		}

		return codeEditorElements;
	}

	private static Map<String, Object> _createCodeEditorElement(
		List<HashMap<String, String>> items, String key, Locale locale) {

		return HashMapBuilder.<String, Object>put(
			"items", items
		).put(
			"label", LanguageUtil.get(locale, key)
		).build();
	}

	@Reference(unbind = "-")
	private void _setObjectFieldLocalService(
		ObjectFieldLocalService objectFieldLocalService) {

		_objectFieldLocalService = objectFieldLocalService;
	}

	private static ObjectFieldLocalService _objectFieldLocalService;

	private enum DDMExpressionFunction {

		COMPARE_DATES(
			"compareDates(field_name, parameter)", "compare-dates",
			"check-if-a-field-has-the-same-date-of-the-value"),
		CONCAT(
			"concat(parameter1, parameter2, parameterN)", "concat",
			"combine-multiple-strings-or-text-fields-and-return-a-single-" +
				"string-that-can-be-used-with-other-validation-functions"),
		CONDITION(
			"condition(condition, parameter1, parameter2)", "condition",
			"provide-for-the-customer-the-possibility-of-condition-for-" +
				"values-or-fields-and-determines-if-expressions-are-true-or-" +
					"false"),
		CONTAINS(
			"contains(field_name, parameter)", "contains",
			"check-if-a-field-contains-a-specific-value-and-return-a-boolean"),
		DOES_NOT_CONTAIN(
			"NOT(contains(field_name, parameter))", "does-not-contain",
			"check-if-a-field-contains-a-specific-value-and-return-a-boolean-" +
				"if-the-field-does-contain-the-value-it-is-invalid"),
		FUTURE_DATES(
			"futureDates(field_name, parameter)", "future-dates",
			"check-if-a-date-fields-value-is-in-the-future-and-return-a-" +
				"boolean"),
		IS_A_URL(
			"isURL(field_name)", "is-a-url",
			"check-if-a-text-field-is-a-URL-and-return-a-boolean"),
		IS_AN_EMAIL(
			"isEmailAddress(field_name)", "is-an-email",
			"check-if-a-text-field-is-an-email-and-return-a-boolean"),
		IS_DECIMAL(
			"isDecimal(parameter)", "is-decimal",
			"check-if-a-numeric-field-is-a-decimal-and-return-a-boolean"),
		IS_EMPTY(
			"isEmpty(parameter)", "is-empty",
			"check-if-a-text-field-is-empty-and-return-a-boolean"),
		IS_EQUAL_TO(
			"field_name == parameter", "is-equal-to",
			"check-if-a-field-is-equal-to-a-specific-value-and-return-a-" +
				"boolean"),
		IS_GREATER_THAN(
			"field_name > parameter", "is-greater-than",
			"check-if-a-numeric-field-is-greater-than-a-specific-numeric-" +
				"value-and-return-a-boolean"),
		IS_GREATER_THAN_OR_EQUAL_TO(
			"field_name >= parameter", "is-greater-than-or-equal-to",
			"check-if-a-numeric-field-is-greater-than-or-equal-to-a-specific-" +
				"numeric-value-and-return-a-boolean"),
		IS_INTEGER(
			"isInteger(parameter)", "is-integer",
			"check-if-a-numeric-field-is-an-integer-and-return-a-boolean"),
		IS_LESS_THAN(
			"field_name < parameter", "is-less-than",
			"check-if-a-numeric-field-is-less-than-a-specific-numeric-value-" +
				"and-return-a-boolean"),
		IS_LESS_THAN_OR_EQUAL_TO(
			"field_name <= parameter", "is-less-than-or-equal-to",
			"check-if-a-numeric-field-is-less-than-or-equal-to-a-specific-" +
				"numeric-value-and-return-a-boolean"),
		IS_NOT_EQUAL_TO(
			"field_name != parameter", "is-not-equal-to",
			"check-if-a-field-is-not-equal-to-a-specific-value-and-return-a-" +
				"boolean"),
		MATCH(
			"match(field_name, parameter)", "match",
			"check-if-a-text-field-matches-a-specific-string-value-or-regex-" +
				"expression-and-return-a-boolean"),
		PAST_DATES(
			"pastDates(field_name, parameter)", "past-dates",
			"check-if-a-date-fields-value-is-in-the-past-and-return-a-boolean"),
		RANGE(
			"futureDates(field_name, parameter) AND pastDates(" +
				"field_name, parameter)",
			"range",
			"check-if-a-date-range-begins-with-a-past-date-and-ends-with-a-" +
				"future-date"),
		SUM(
			"sum(parameter1, parameter2, parameterN)", "sum",
			"add-multiple-numeric-fields-together-and-return-a-single-number-" +
				"that-can-be-used-with-other-validation-functions");

		public static List<HashMap<String, String>> getItems(Locale locale) {
			List<HashMap<String, String>> values = new ArrayList<>();

			for (DDMExpressionFunction ddmExpressionFunction : values()) {
				values.add(
					HashMapBuilder.put(
						"content", ddmExpressionFunction._content
					).put(
						"label",
						LanguageUtil.get(locale, ddmExpressionFunction._key)
					).put(
						"tooltip",
						LanguageUtil.get(
							locale, ddmExpressionFunction._tooltipKey)
					).build());
			}

			return values;
		}

		private DDMExpressionFunction(
			String content, String key, String tooltipKey) {

			_content = content;
			_key = key;
			_tooltipKey = tooltipKey;
		}

		private String _content;
		private String _key;
		private String _tooltipKey;

	}

	private enum DDMExpressionOperator {

		AND(
			"AND", "and",
			"this-is-a-type-of-coordinating-conjunction-that-is-commonly-" +
				"used-to-indicate-a-dependent-relationship"),
		DIVIDED_BY(
			"field_name / field_name2", "divided-by",
			"divide-one-numeric-field-by-another-to-create-an-expression"),
		MINUS(
			"field_name - field_name2", "minus",
			"subtract-numeric-fields-from-one-another-to-create-an-expression"),
		OR(
			"OR", "or",
			"this-is-a-type-of-coordinating-conjunction-that-indicates-an-" +
				"independent-relationship"),
		PLUS(
			"field_name + field_name2", "plus",
			"add-numeric-fields-to-create-an-expression"),
		TIMES(
			"field_name * field_name2", "times",
			"multiply-numeric-fields-to-create-an-expression");

		public static List<HashMap<String, String>> getItems(Locale locale) {
			List<HashMap<String, String>> values = new ArrayList<>();

			for (DDMExpressionOperator ddmExpressionOperator : values()) {
				values.add(
					HashMapBuilder.put(
						"content", ddmExpressionOperator._content
					).put(
						"label",
						LanguageUtil.get(locale, ddmExpressionOperator._key)
					).put(
						"tooltip",
						LanguageUtil.get(
							locale, ddmExpressionOperator._tooltipKey)
					).build());
			}

			return values;
		}

		private DDMExpressionOperator(
			String content, String key, String tooltipKey) {

			_content = content;
			_key = key;
			_tooltipKey = tooltipKey;
		}

		private String _content;
		private String _key;
		private String _tooltipKey;

	}

}