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
import React, {useState} from 'react';

import BaseActionsInfo from '../../shared-components/BaseActionsInfo';

const ActionTypeAction = () => {
	const [scriptSections, setScriptSections] = useState([
		{identifier: `${Date.now()}-0`},
	]);

	const deleteSection = (identifier) => {
		setScriptSections((prevSections) => {
			const newSections = prevSections.filter(
				(prevSection) => prevSection.identifier !== identifier
			);

			return newSections;
		});
	};

	return scriptSections.map(({identifier}) => {
		return (
			<div key={`section-${identifier}`}>
				<BaseActionsInfo
					templateLabel={Liferay.Language.get('script')}
					templateLabelSecondary={Liferay.Language.get('groovy')}
				/>

				<div className="section-buttons-area">
					<ClayButton
						className="mr-3"
						displayType="secondary"
						onClick={() =>
							setScriptSections((prev) => {
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
						{Liferay.Language.get('new-section')}
					</ClayButton>

					{scriptSections.length > 1 && (
						<ClayButtonWithIcon
							className="delete-button"
							displayType="unstyled"
							onClick={() => deleteSection(identifier)}
							symbol="trash"
						/>
					)}
				</div>
			</div>
		);
	});
};

export default ActionTypeAction;
