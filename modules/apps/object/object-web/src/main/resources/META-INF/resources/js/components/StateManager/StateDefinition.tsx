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

import {
	Card,
	CustomItem,
	FormCustomSelect,
} from '@liferay/object-js-components-web';
import React, {useState} from 'react';

import './StateDefinition.scss';

export default function StateDefinition({
	disabled,
	index,
	initialValues,
	stateName,
}: IProps) {
	const [items, setItems] = useState<CustomItem[]>(
		initialValues.map(({id, name}) => {
			return {label: name, value: id};
		})
	);

	return (
		<div className="lfr-objects__state-definition-card-state">
			<div className="lfr-objects__state-definition-card-state-name">
				{index === 0 && (
					<label>{Liferay.Language.get('state-name')}</label>
				)}

				<Card title={stateName} viewMode="no-children" />
			</div>

			<div className="lfr-objects__state-definition-card-state-next-status">
				{index === 0 && (
					<label>{Liferay.Language.get('next-status')}</label>
				)}

				<FormCustomSelect
					disabled={disabled}
					multipleChoice
					options={items}
					selectAllOption
					setOptions={setItems}
				/>
			</div>
		</div>
	);
}

interface IProps {
	disabled: boolean;
	index: number;
	initialValues: PickListItems[];
	stateName: string;
}
