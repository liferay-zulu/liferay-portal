/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import React, {useState} from 'react';

import SidebarPanel from '../../SidebarPanel';
import ActionTypeAction from './select-action/ActionTypeAction';
import ActionTypeNotification from './select-action/ActionTypeNotification';
import ActionTypeReassignment from './select-action/ActionTypeReassignment';
import SelectActionType from './select-action/SelectActionType';

const actionSectionComponents = {
	actions: ActionTypeAction,
	notifications: ActionTypeNotification,
	reassignments: ActionTypeReassignment,
};

const TimerAction = ({
	actionsIndex,
	actionsSectionsLength,
	identifier,
	setActionsSections,
	setContentName,
	timerIndex,
	updateSelectedItem,
}) => {
	const [actionType, setActionType] = useState('actions');

	const ActionSectionComponent = actionSectionComponents[actionType];

	return (
		<SidebarPanel panelTitle={Liferay.Language.get('action')}>
			<SelectActionType
				actionType={actionType}
				setActionType={setActionType}
			/>

			{ActionSectionComponent && (
				<ActionSectionComponent
					actionsIndex={actionsIndex}
					actionsSectionsLength={actionsSectionsLength}
					identifier={identifier}
					key={`section-${identifier}`}
					setActionsSections={setActionsSections}
					setContentName={setContentName}
					subSectionsIndex={actionsIndex}
					timerIndex={timerIndex}
					updateSelectedItem={updateSelectedItem}
				/>
			)}
		</SidebarPanel>
	);
};

export default TimerAction;
