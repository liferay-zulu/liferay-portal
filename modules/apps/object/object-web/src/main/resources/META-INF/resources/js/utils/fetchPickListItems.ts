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

import {fetch} from 'frontend-js-web';

import {HEADERS} from './constants';

export async function fetchPickListItems(listTypeDefinitionId: number) {
	const result = await fetch(
		`/o/headless-admin-list-type/v1.0/list-type-definitions/${listTypeDefinitionId}/list-type-entries`,
		{
			headers: HEADERS,
			method: 'GET',
		}
	);

	const {items = []} = (await result.json()) as {
		items: PickListItems[] | undefined;
	};

	return items.map(({id, name}) => ({id, name}));
}
