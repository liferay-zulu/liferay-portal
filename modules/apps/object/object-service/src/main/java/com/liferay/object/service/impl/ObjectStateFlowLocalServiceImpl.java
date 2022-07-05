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

package com.liferay.object.service.impl;

import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.exception.NoSuchObjectStateFlowException;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectState;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.service.ObjectStateLocalService;
import com.liferay.object.service.ObjectStateTransitionLocalService;
import com.liferay.object.service.base.ObjectStateFlowLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectStateFlow",
	service = AopService.class
)
public class ObjectStateFlowLocalServiceImpl
	extends ObjectStateFlowLocalServiceBaseImpl {

	@Override
	public ObjectStateFlow addDefaultObjectStateFlow(ObjectField objectField)
		throws PortalException {

		if (!objectField.isState()) {
			return null;
		}

		ObjectStateFlow objectStateFlow = addObjectStateFlow(
			objectField.getObjectFieldId(), objectField.getUserId());

		List<ListTypeEntry> listTypeEntries =
			_listTypeEntryLocalService.getListTypeEntries(
				objectField.getListTypeDefinitionId());

		List<ObjectState> objectStates = new ArrayList<>();

		for (ListTypeEntry listTypeEntry: listTypeEntries) {
			objectStates.add(
				_objectStateLocalService.addObjectState(
					listTypeEntry.getListTypeEntryId(),
					objectStateFlow.getObjectStateFlowId(),
					objectField.getUserId()));
		}

		for (ObjectState sourceObjectState : objectStates) {
			for (ObjectState targetObjectState : objectStates) {
				if (sourceObjectState.equals(targetObjectState)) {
					continue;
				}

				_objectStateTransitionLocalService.addObjectStateTransition(
					objectStateFlow.getObjectStateFlowId(),
					sourceObjectState.getObjectStateId(),
					targetObjectState.getObjectStateId(),
					objectField.getUserId());
			}
		}

		return objectStateFlow;
	}

	@Override
		public ObjectStateFlow addObjectStateFlow(long userId, long objectFieldId)
		throws PortalException {

		ObjectStateFlow objectStateFlow = createObjectStateFlow(
			counterLocalService.increment());

		User user = _userLocalService.getUser(userId);

		objectStateFlow.setCompanyId(user.getCompanyId());
		objectStateFlow.setUserId(user.getUserId());
		objectStateFlow.setUserName(user.getFullName());

		objectStateFlow.setObjectFieldId(objectFieldId);

		return addObjectStateFlow(objectStateFlow);
	}

	@Override
	public void deleteByObjectFieldId(long objectFieldId)
		throws NoSuchObjectStateFlowException {

		ObjectStateFlow objectStateFlow = fetchByObjectFieldId(objectFieldId);

		objectStateFlowPersistence.remove(
			objectStateFlow.getObjectStateFlowId());

		_objectStateLocalService.deleteByObjectStateFlowId(
			objectStateFlow.getObjectStateFlowId());

		_objectStateTransitionLocalService.deleteByObjectStateFlowId(
			objectStateFlow.getObjectStateFlowId());
	}

	@Override
	public ObjectStateFlow fetchByObjectFieldId(long objectFieldId) {
		return objectStateFlowPersistence.fetchByObjectFieldId(objectFieldId);
	}

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Reference
	private ObjectStateLocalService _objectStateLocalService;

	@Reference
	private ObjectStateTransitionLocalService
		_objectStateTransitionLocalService;

}