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

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectStateFlowLocalService;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectStateTransitionLocalService;
import com.liferay.object.service.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.util.LocalizedMapUtil;
import com.liferay.object.util.ObjectFieldBuilder;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.TransformUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Selton Guedes
 */
@RunWith(Arquillian.class)
public class ObjectStateFlowLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.addObjectDefinition(
			_objectDefinitionLocalService);

		_listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				TestPropsValues.getUserId(),
				LocalizedMapUtil.getLocalizedMap(
					RandomTestUtil.randomString()));

		_listTypeEntries = new ArrayList<>();

		for (String key : Arrays.asList("closed", "open", "review")) {
			_listTypeEntries.add(
				_listTypeEntryLocalService.addListTypeEntry(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), key,
					LocalizedMapUtil.getLocalizedMap(key)));
		}
	}

	@Test
	public void testAddDefaultObjectStateFlow() throws Exception {
		ObjectFieldBuilder objectFieldBuilder = new ObjectFieldBuilder();

		String name = RandomTestUtil.randomString();

		ObjectField objectField = objectFieldBuilder.businessType(
			ObjectFieldConstants.BUSINESS_TYPE_LONG_TEXT
		).dbType(
			ObjectFieldConstants.DB_TYPE_STRING
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(name)
		).name(
			name
		).required(
			true
		).state(
			true
		).objectFieldSettings(
			Collections.emptyList()
		).userId(
			TestPropsValues.getUserId()
		).build();

		ObjectStateFlow objectStateFlow =
			_objectStateFlowLocalService.addDefaultObjectStateFlow(objectField);

		Assert.assertNull(objectStateFlow);

		objectField = _addCustomObjectField(
			objectFieldBuilder.listTypeDefinitionId(
				_listTypeDefinition.getListTypeDefinitionId()
			).state(
				false
			).build());

		objectStateFlow =
			_objectStateFlowLocalService.addDefaultObjectStateFlow(objectField);

		Assert.assertEquals(
			TestPropsValues.getCompanyId(), objectStateFlow.getCompanyId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), objectStateFlow.getUserId());
		Assert.assertEquals(
			objectField.getObjectFieldId(), objectStateFlow.getObjectFieldId());

		long objectStateFlowId = objectStateFlow.getObjectStateFlowId();

		List<ObjectState> objectStates = TransformUtil.transform(
			_listTypeEntries,
			listTypeEntry -> _objectStateLocalService.addObjectState(
				TestPropsValues.getUserId(), listTypeEntry.getListTypeEntryId(),
				objectStateFlowId));

		for (ObjectState sourceObjectState : objectStates) {
			for (ObjectState targetObjectState : objectStates) {
				if (sourceObjectState.equals(targetObjectState)) {
					continue;
				}

				ObjectStateTransition objectStateTransition =
					_objectStateTransitionLocalService.addObjectStateTransition(
						TestPropsValues.getUserId(),
						objectStateFlow.getObjectStateFlowId(),
						sourceObjectState.getObjectStateId(),
						targetObjectState.getObjectStateId());

				sourceObjectState.setObjectStateTransitions(
					Collections.singletonList(objectStateTransition));
			}
		}

		for (int index = 0; index < objectStates.size(); index++) {
			List<ObjectState> objectStateFlowObjectStates =
				objectStateFlow.getObjectStates();

			ObjectState objectState1 = objectStates.get(index);
			ObjectState objectState2 = objectStateFlowObjectStates.get(index);

			Assert.assertEquals(
				objectState1.getObjectStateFlowId(),
				objectState2.getObjectStateFlowId());

			List<ObjectStateTransition> objectStateTransitions =
				objectState1.getObjectStateTransitions();

			ObjectStateTransition objectStateTransition1 =
				objectStateTransitions.get(0);

			objectStateTransitions = objectState2.getObjectStateTransitions();

			ObjectStateTransition objectStateTransition2 =
				objectStateTransitions.get(0);

			Assert.assertEquals(
				objectStateTransition1.getSourceObjectStateId(),
				objectStateTransition2.getSourceObjectStateId());

			Assert.assertEquals(
				objectStateTransition1.getTargetObjectStateId(),
				objectStateTransition2.getTargetObjectStateId());
		}
	}

	@Test
	public void testDeleteObjectFieldObjectStateFlow() {
	}

	@Test
	public void testGetObjectFieldObjectStateFlow() {
	}

	private ObjectField _addCustomObjectField(ObjectField objectField)
		throws Exception {

		return _objectFieldLocalService.addCustomObjectField(
			objectField.getUserId(), objectField.getListTypeDefinitionId(),
			_objectDefinition.getObjectDefinitionId(),
			objectField.getBusinessType(), objectField.getDBType(),
			objectField.getDefaultValue(), objectField.isIndexed(),
			objectField.isIndexedAsKeyword(),
			objectField.getIndexedLanguageId(), objectField.getLabelMap(),
			objectField.getName(), objectField.isRequired(),
			objectField.isState(), objectField.getObjectFieldSettings());
	}

	@DeleteAfterTestRun
	private ListTypeDefinition _listTypeDefinition;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@DeleteAfterTestRun
	private List<ListTypeEntry> _listTypeEntries;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectStateFlowLocalService _objectStateFlowLocalService;

	@Inject
	private ObjectStateLocalService _objectStateLocalService;

	@Inject
	private ObjectStateTransitionLocalService
		_objectStateTransitionLocalService;

}