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

import {Card, SidePanelForm} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

import {fetchPickListItems} from '../utils/fetchPickListItems';
import {defaultLanguageId} from '../utils/locale';
import StateDefinition from './StateManager/StateDefinition';

const locales: {label: string; symbol: string}[] = [];
const languageLabels: string[] = [];
const languages = Liferay.Language.available as LocalizedValue<string>;

Object.entries(languages).forEach(([languageId, label]) => {
	locales.push({
		label: languageId,
		symbol: languageId.replace('_', '-').toLocaleLowerCase(),
	});

	languageLabels.push(label);
});

export default function EditObjectStateField({
	objectField: initialValues,
	readOnly,
}: IProps) {
	const [pickListItems, setPickListItems] = useState<PickListItems[]>();

	useEffect(() => {
		async function fetchData() {
			const items = await fetchPickListItems(
				initialValues.listTypeDefinitionId
			);

			setPickListItems(items);
		}

		fetchData();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<SidePanelForm
			className="lfr-objects__edit-object-state-field"
			readOnly={readOnly}
			title={`${
				initialValues.label[defaultLanguageId]
			} ${Liferay.Language.get('settings')}`}
		>
			<Card title={Liferay.Language.get('select-the-state-flow')}>
				{pickListItems?.map(({name}, index) => (
					<StateDefinition
						disabled={readOnly}
						index={index}
						initialValues={pickListItems}
						key={index}
						stateName={name}
					/>
				))}
			</Card>
		</SidePanelForm>
	);
}

interface IProps {
	forbiddenChars: string[];
	forbiddenLastChars: string[];
	forbiddenNames: string[];
	isApproved: boolean;
	objectField: ObjectField;
	objectName: string;
	readOnly: boolean;
}
