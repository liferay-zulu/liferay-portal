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

import {ClayButtonWithIcon} from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import React, {useContext, useEffect, useState} from 'react';
import {isNode} from 'react-flow-renderer';

import {DefinitionBuilderContext} from '../../../DefinitionBuilderContext';
import {retrieveDefinitionRequest} from '../../../util/fetchUtil';
import {DiagramBuilderContext} from '../../DiagramBuilderContext';
import SidebarBody from './SidebarBody';
import SidebarHeader from './SidebarHeader';
import sectionComponents from './sections/sectionComponents';

const contents = {
	'actions': {
		backButton: (setContentName, selectedItemType) => () =>
			setContentName(selectedItemType),
		deleteFunction: (setSelectedItem) => () =>
			setSelectedItem((previousValue) => ({
				...previousValue,
				data: {
					...previousValue.data,
					actions: null,
				},
			})),
		sections: ['actions'],
		showDeleteButton: true,
		title: Liferay.Language.get('actions'),
	},
	'assignments': {
		backButton: (setContentName) => () => setContentName('task'),
		deleteFunction: (setSelectedItem) => () =>
			setSelectedItem((previousValue) => ({
				...previousValue,
				data: {
					...previousValue.data,
					assignments: null,
				},
			})),
		sections: ['assignments'],
		showDeleteButton: true,
		title: Liferay.Language.get('assignments'),
	},
	'condition': {
		sections: ['nodeInformation', 'notificationsSummary', 'actionsSummary'],
		showDeleteButton: true,
		title: Liferay.Language.get('condition-node'),
	},
	'end': {
		sections: ['nodeInformation', 'notificationsSummary', 'actionsSummary'],
		showDeleteButton: true,
		title: Liferay.Language.get('end'),
	},
	'fork': {
		sections: ['nodeInformation', 'notificationsSummary', 'actionsSummary'],
		showDeleteButton: true,
		title: Liferay.Language.get('fork-node'),
	},
	'join': {
		sections: ['nodeInformation', 'notificationsSummary', 'actionsSummary'],
		showDeleteButton: true,
		title: Liferay.Language.get('join-node'),
	},
	'join-xor': {
		sections: ['nodeInformation', 'notificationsSummary', 'actionsSummary'],
		showDeleteButton: true,
		title: Liferay.Language.get('join-xor-node'),
	},
	'notifications': {
		backButton: (setContentName, selectedItemType) => () =>
			setContentName(selectedItemType),
		deleteFunction: (setSelectedItem) => () =>
			setSelectedItem((previousValue) => ({
				...previousValue,
				data: {
					...previousValue.data,
					notifications: null,
				},
			})),
		sections: ['notifications'],
		showDeleteButton: true,
		title: Liferay.Language.get('notifications'),
	},
	'scripted-assignment': {
		backButton: (setContentName) => () => setContentName('assignments'),
		sections: ['sourceCode'],
		showDeleteButton: false,
		title: Liferay.Language.get('scripted-assignment'),
	},
	'scripted-reassignment': {
		backButton: (setContentName) => () => setContentName('timers'),
		sections: ['timersSourceCode'],
		showDeleteButton: false,
		title: Liferay.Language.get('scripted-reassignment'),
	},
	'start': {
		sections: ['nodeInformation', 'notificationsSummary', 'actionsSummary'],
		showDeleteButton: true,
		title: Liferay.Language.get('start'),
	},
	'state': {
		sections: ['nodeInformation', 'notificationsSummary', 'actionsSummary'],
		showDeleteButton: true,
		title: Liferay.Language.get('state'),
	},
	'task': {
		sections: [
			'nodeInformation',
			'assignmentsSummary',
			'notificationsSummary',
			'actionsSummary',
			'timersSummary',
		],
		showDeleteButton: true,
		title: Liferay.Language.get('task'),
	},
	'timers': {
		backButton: (setContentName) => () => setContentName('task'),
		deleteFunction: (setSelectedItem) => () =>
			setSelectedItem((previousValue) => ({
				...previousValue,
				data: {
					...previousValue.data,
					taskTimers: null,
				},
			})),
		sections: ['timers'],
		showDeleteButton: true,
		title: Liferay.Language.get('timers'),
	},
	'transition': {
		sections: ['edgeInformation'],
		showDeleteButton: true,
		title: Liferay.Language.get('transition'),
	},
};

const errorsDefaultValues = {
	id: false,
	label: false,
};

export default function Sidebar() {
	const {definitionId, infoVersion, setBlockingErrors, version} = useContext(
		DefinitionBuilderContext
	);

	const {selectedItem, setSelectedItem, setSelectedItemNewId} = useContext(
		DiagramBuilderContext
	);
	const [contentName, setContentName] = useState('');
	const [errors, setErrors] = useState(errorsDefaultValues);

	const clearErrors = () => {
		setErrors(errorsDefaultValues);
	};

	const defaultBackButton = () => {
		setSelectedItem(null);
		setSelectedItemNewId(null);
		clearErrors();
	};

	const [titleDetails, setTitleDetails] = useState('');
	const [dateCreatedDetails, setDateCreatedDetails] = useState('');
	const [dateModifiedDetails, setDateModifiedDetails] = useState('');
	const [versionDetails, setVersionDetails] = useState('');
	const [switchNav, setSwitchNav] = useState('');

	const dateCreatedDetailsFormated = new Date(dateCreatedDetails);
	const dateModifiedDetailsFormated = new Date(dateModifiedDetails);

	useEffect(() => {
		setBlockingErrors((prev) => {
			if (errors.label === true || errors.id.empty === true) {
				return {...prev, errorType: 'emptyField'};
			}
			if (errors.id.duplicated === true) {
				return {...prev, errorType: 'duplicated'};
			} else {
				return {...prev, errorType: ''};
			}
		});
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [errors]);

	useEffect(() => {
		setSelectedItemNewId(null);
		clearErrors();

		let contentKey = '';

		if (selectedItem?.id) {
			contentKey = isNode(selectedItem)
				? selectedItem?.type
				: 'transition';
		}

		setContentName(contentKey);

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedItem?.id, setSelectedItemNewId]);

	useEffect(() => {
		if (definitionId && version !== '0') {
			retrieveDefinitionRequest(definitionId)
				.then((response) => response.json())
				.then(({dateCreated, dateModified, title, version}) => {
					setDateCreatedDetails(dateCreated);
					setDateModifiedDetails(dateModified);
					setTitleDetails(title);
					setVersionDetails(version);
				});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [definitionId, version]);

	useEffect(() => {
		if (version !== '0') {
			retrieveDefinitionRequest(definitionId)
				.then((response) => response.json())
				.then(({dateCreated}) => {
					setDateCreatedDetails(dateCreated);
				});
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [version]);

	const content = contents[contentName];
	const title = content?.title ?? Liferay.Language.get('nodes');

	const versionList = [];
	let versionNumber = 0;

	while (versionNumber < versionDetails) {
		versionList.push(versionNumber);

		versionNumber = versionNumber + 1;
	}

	versionList.sort((a, b) => {
		if (a > b) {
			return -1;
		}

		if (a < b) {
			return 1;
		}

		return 0;
	});

	const setRestore = (item) => {
		retrieveDefinitionRequest(definitionId, item)
			.then((response) => response.json())
			.then(({dateCreated}) => {
				setDateCreatedDetails(dateCreated);
			});
	};

	return (
		<div className="sidebar" sidebar>
			<ClayLayout.ContainerFluid view>
				{!infoVersion && (
					<>
						<SidebarHeader
							backButtonFunction={
								content?.backButton?.(
									setContentName,
									selectedItem?.type
								) || defaultBackButton
							}
							contentName={contentName}
							deleteButtonFunction={
								content?.deleteFunction?.(setSelectedItem) ||
								null
							}
							showBackButton={!!content}
							showDeleteButton={content?.showDeleteButton}
							title={title}
						/>
						<SidebarBody displayDefaultContent={!content}>
							{content?.sections?.map((sectionKey) => {
								const SectionComponent =
									sectionComponents[sectionKey];

								return (
									<SectionComponent
										errors={errors}
										key={sectionKey}
										sections={content?.sections || []}
										setContentName={setContentName}
										setErrors={setErrors}
									/>
								);
							})}
						</SidebarBody>
					</>
				)}

				{infoVersion && versionDetails > 0 && (
					<>
						<ClayLayout.ContainerFluid view>
							<h2>{titleDetails}</h2>
						</ClayLayout.ContainerFluid>

						<ClayNavigationBar triggerLabel="Item 1">
							<ClayNavigationBar.Item
								active={switchNav !== 'revisionHistory'}
							>
								<ClayLink
									className="nav-link"
									displayType="unstyled"
									onClick={() => setSwitchNav('details')}
								>
									Details
								</ClayLink>
							</ClayNavigationBar.Item>

							<ClayNavigationBar.Item
								active={switchNav === 'revisionHistory'}
							>
								<ClayLink
									className="nav-link"
									displayType="unstyled"
									onClick={() =>
										setSwitchNav('revisionHistory')
									}
								>
									Revision History
								</ClayLink>
							</ClayNavigationBar.Item>
						</ClayNavigationBar>
						{/* This will be refactored for Clay */}
						<ClayLayout.ContainerFluid view>
							{switchNav !== 'revisionHistory' && (
								<>
									<h4 className="sheet-title">
										<span className="autofit-float-sm-down autofit-padded-no-gutters autofit-row">
											<span className="autofit-col autofit-col-expand">
												<span className="component-title">
													CREATED
												</span>

												<p className="sheet-text">
													{dateCreatedDetailsFormated.toUTCString()}
												</p>

												<span className="component-title">
													LAST MODIFIED
												</span>

												<p className="sheet-text">
													{dateModifiedDetailsFormated.toUTCString()}
												</p>

												<span className="component-title">
													TOTAL MODIFICATIONS
												</span>

												<p className="sheet-text">
													{versionDetails} Revisions
												</p>
											</span>
										</span>
									</h4>
								</>
							)}

							{switchNav === 'revisionHistory' &&
								versionList.map((item) => (
									<>
										<h4 className="sheet-subtitle">
											<span className="autofit-float-sm-down autofit-padded-no-gutters autofit-row">
												<span className="autofit-col autofit-col-expand">
													{item !==
													versionList.length - 1 ? (
														<span className="component-title text-secondary">
															Value {item}
														</span>
													) : (
														<span className="component-title">
															This Version
														</span>
													)}
												</span>

												<span className="autofit-col">
													{item !==
														versionList.length -
															1 && (
														<ClayButtonWithIcon
															displayType="unstyled"
															onClick={() =>
																setRestore(item)
															}
															symbol="restore"
															title={Liferay.Language.get(
																'restore'
															)}
														/>
													)}
												</span>
											</span>
										</h4>
									</>
								))}
						</ClayLayout.ContainerFluid>
					</>
				)}
			</ClayLayout.ContainerFluid>
		</div>
	);
}
