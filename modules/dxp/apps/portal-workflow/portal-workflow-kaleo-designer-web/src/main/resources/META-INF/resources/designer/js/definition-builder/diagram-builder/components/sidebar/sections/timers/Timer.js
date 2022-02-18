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

import ClayButton from '@clayui/button';
import PropTypes from 'prop-types';
import React, {useContext, useState} from 'react';

import {DiagramBuilderContext} from '../../../../DiagramBuilderContext';
import TimerAction from './TimerAction';
import TimerDuration from './TimerDuration';
import TimerInfo from './TimerInfo';

const Timer = ({setSections}) => {
	const {setSelectedItem} = useContext(DiagramBuilderContext);
	const [subSections, setSubSections] = useState([
		{identifier: `${Date.now()}-0`},
	]);

	const updateSelectedItem = (values) => {
		setSelectedItem((previousItem) => ({
			...previousItem,
			data: {
				...previousItem.data,
				taskTimers: {
					blocking: [],
					delay: [{duration: [], scale: []}],
					description: [values.timerDescription],
					name: [values.timerName],
					reassignments: [],
					timerActions: [],
					timerNotifications: [],
				},
			},
		}));
	};

	return (
		<div className="panel">
			<TimerInfo updateSelectedItem={updateSelectedItem} />

			<TimerDuration updateSelectedItem={updateSelectedItem} />

			{subSections.map(({identifier}, index) => (
				<TimerAction
					identifier={identifier}
					index={index}
					key={`section-${identifier}`}
					sectionsLength={subSections?.length}
					updateSelectedItem={updateSelectedItem}
				/>
			))}

			<div className="sheet-subtitle" />

			<div className="autofit-float autofit-padded-no-gutters-x autofit-row autofit-row-center mb-3">
				<div className="autofit-col">
					<ClayButton
						className="mr-3"
						displayType="secondary"
						onClick={() =>
							setSubSections((prev) => {
								return [
									...prev,
									{
										identifier: `${Date.now()}-${
											prev.length
										}`,
									},
								];
							})
						}
					>
						{Liferay.Language.get('add-action')}
					</ClayButton>
				</div>

				<div className="autofit-col autofit-col-end">
					<ClayButton
						displayType="secondary"
						onClick={() =>
							setSections((prev) => {
								return [
									...prev,
									{
										identifier: `${Date.now()}-${
											prev.length
										}`,
									},
								];
							})
						}
					>
						{Liferay.Language.get('new-timer')}
					</ClayButton>
				</div>
			</div>
		</div>
	);
};

Timer.propTypes = {
	setContentName: PropTypes.func.isRequired,
};

export default Timer;
