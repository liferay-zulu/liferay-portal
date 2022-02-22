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

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import PropTypes from 'prop-types';
import React, {useContext, useState} from 'react';

import {DiagramBuilderContext} from '../../../../../DiagramBuilderContext';
import BaseNotificationsInfo from '../../shared-components/BaseNotificationsInfo';

const ActionTypeNotification = ({index, updateSelectedItem}) => {
	const [notificationSections, setNotificationSections] = useState([
		{identifier: `${Date.now()}-0`},
	]);
	const {selectedItem} = useContext(DiagramBuilderContext);

	const updateTimerNotification = (values) => {
		updateSelectedItem({
			timerNotifications: {
				...selectedItem.data.taskTimers.timerNotifications[index],
				description: values.map(({description}) => description),
				executionType: values.map(({executionType}) => executionType),
				name: values.map(({name}) => name),
				notificationType: values.map(
					({notificationType}) => notificationType
				),
				recipients: [
					{
						...selectedItem.data.taskTimers.timerNotifications
							?.recipients,
						receptionType: values.map(
							({recipientType}) => recipientType
						),
					},
				],
				template: values.map(({template}) => template),
				templateLanguage: values.map(
					({templateLanguage}) => templateLanguage
				),
			},
		});
	};

	const deleteSection = (identifier) => {
		setNotificationSections((prevSections) => {
			const newSections = prevSections.filter(
				(prevSection) => prevSection.identifier !== identifier
			);

			return newSections;
		});
	};

	return notificationSections.map(({identifier}, index) => (
		<div key={`section-${identifier}`}>
			<BaseNotificationsInfo
				identifier={identifier}
				index={index}
				sectionsLength={notificationSections?.length}
				setSections={setNotificationSections}
				updateSelectedItem={updateTimerNotification}
			/>

			<div className="mb-4 mt-4 section-buttons-area">
				<ClayButton
					className="mr-3"
					displayType="secondary"
					onClick={() =>
						setNotificationSections((prev) => {
							return [
								...prev,
								{
									identifier: `${Date.now()}-${prev.length}`,
								},
							];
						})
					}
				>
					{Liferay.Language.get('add-notification')}
				</ClayButton>

				{notificationSections.length > 1 && (
					<ClayButtonWithIcon
						className="delete-button"
						displayType="unstyled"
						onClick={() => deleteSection(identifier)}
						symbol="trash"
					/>
				)}
			</div>
		</div>
	));
};

ActionTypeNotification.propTypes = {
	index: PropTypes.number.isRequired,
	updateSelectedItem: PropTypes.func.isRequired,
};

export default ActionTypeNotification;
