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

import com.liferay.account.model.AccountEntry;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.PanelCategoryRegistry;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.scope.ObjectScopeProvider;
import com.liferay.object.scope.ObjectScopeProviderRegistry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.web.internal.constants.ObjectWebKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marco Leo
 */
public class ObjectDefinitionsDetailsDisplayContext
	extends BaseObjectDefinitionsDisplayContext {

	public ObjectDefinitionsDetailsDisplayContext(
		HttpServletRequest httpServletRequest,
		ModelResourcePermission<ObjectDefinition>
			objectDefinitionModelResourcePermission,
		ObjectScopeProviderRegistry objectScopeProviderRegistry,
		PanelCategoryRegistry panelCategoryRegistry) {

		super(httpServletRequest, objectDefinitionModelResourcePermission);

		_objectScopeProviderRegistry = objectScopeProviderRegistry;
		_panelCategoryRegistry = panelCategoryRegistry;
	}

	public List<KeyValuePair> getKeyValuePairs() {
		List<KeyValuePair> keyValuePairs = new ArrayList<>();

		ObjectDefinition objectDefinition = getObjectDefinition();

		String scope = ParamUtil.getString(
			objectRequestHelper.getRequest(), "scope",
			objectDefinition.getScope());

		ObjectScopeProvider objectScopeProvider =
			_objectScopeProviderRegistry.getObjectScopeProvider(scope);

		for (String panelCategoryKey :
				objectScopeProvider.getRootPanelCategoryKeys()) {

			if (panelCategoryKey.equals(PanelCategoryKeys.COMMERCE) &&
				!GetterUtil.getBoolean(
					PropsUtil.get("enterprise.product.commerce.enabled"))) {

				continue;
			}

			PanelCategory panelCategory =
				_panelCategoryRegistry.getPanelCategory(panelCategoryKey);

			List<PanelCategory> childPanelCategories =
				_panelCategoryRegistry.getChildPanelCategories(
					panelCategoryKey);

			for (PanelCategory childPanelCategory : childPanelCategories) {
				keyValuePairs.add(
					new KeyValuePair(
						childPanelCategory.getKey(),
						StringBundler.concat(
							panelCategory.getLabel(
								objectRequestHelper.getLocale()),
							" > ",
							childPanelCategory.getLabel(
								objectRequestHelper.getLocale()))));
			}
		}

		return keyValuePairs;
	}

	public List<ObjectField> getObjectAccountRelationshipFields()
		throws PortalException {

		ObjectDefinition objectDefinition = getObjectDefinition();

		ObjectDefinition accountEntryObjectDefinition =
			ObjectDefinitionLocalServiceUtil.fetchObjectDefinitionByClassName(
				objectRequestHelper.getCompanyId(),
				AccountEntry.class.getName());

		List<Long> objectRelationshipFieldIds = Stream.of(
			ObjectRelationshipLocalServiceUtil.getObjectRelationships(
				accountEntryObjectDefinition.getObjectDefinitionId(),
				objectDefinition.getObjectDefinitionId())
		).flatMap(
			List::stream
		).map(
			ObjectRelationship::getObjectFieldId2
		).collect(
			Collectors.toList()
		);

		Map<Long, ObjectField> objectFieldsMap = Stream.of(
			getObjectFields()
		).flatMap(
			List::stream
		).collect(
			Collectors.toMap(
				ObjectField::getObjectFieldId, objectField -> objectField)
		);

		List<ObjectField> objectFields = new ArrayList<>();

		for (Long objectRelationshipFieldId : objectRelationshipFieldIds) {
			objectFields.add(objectFieldsMap.get(objectRelationshipFieldId));
		}

		return objectFields;
	}

	public ObjectDefinition getObjectDefinition() {
		HttpServletRequest httpServletRequest =
			objectRequestHelper.getRequest();

		return (ObjectDefinition)httpServletRequest.getAttribute(
			ObjectWebKeys.OBJECT_DEFINITION);
	}

	public List<ObjectField> getObjectFields() {
		HttpServletRequest httpServletRequest =
			objectRequestHelper.getRequest();

		return (List<ObjectField>)httpServletRequest.getAttribute(
			ObjectWebKeys.OBJECT_FIELDS);
	}

	public List<ObjectField> getObjectNonrelationshipFields() {
		return ListUtil.filter(
			getObjectFields(),
			objectField -> Validator.isNull(objectField.getRelationshipType()));
	}

	public List<ObjectScopeProvider> getObjectScopeProviders() {
		return _objectScopeProviderRegistry.getObjectScopeProviders();
	}

	public String getScope() {
		ObjectDefinition objectDefinition = getObjectDefinition();

		return ParamUtil.getString(
			objectRequestHelper.getRequest(), "scope",
			objectDefinition.getScope());
	}

	public boolean hasPublishObjectPermission() {
		PortletResourcePermission portletResourcePermission =
			objectDefinitionModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			objectRequestHelper.getPermissionChecker(), null,
			ObjectActionKeys.PUBLISH_OBJECT_DEFINITION);
	}

	private final ObjectScopeProviderRegistry _objectScopeProviderRegistry;
	private final PanelCategoryRegistry _panelCategoryRegistry;

}