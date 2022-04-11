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

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {fetch} from 'frontend-js-web';
import React, {FormEvent, useEffect, useState} from 'react';

import CustomSelect from './Form/CustomSelect/CustomSelect';
import Input from './Form/Input';

const defaultLanguageId = Liferay.ThemeDisplay.getDefaultLanguageId() as Liferay.Language.Locale;
const headers = new Headers({
	'Accept': 'application/json',
	'Content-Type': 'application/json',
});
const requiredLabel = Liferay.Language.get('required');

function ModalAddObjectValidation({
	apiURL,
	objectValidationTypes,
	observer,
	onClose,
}: IModal) {
	objectValidationTypes = objectValidationTypes.filter(
		(type) => type.validationType === 'groovy'
	);

	const [typeSelection, setTypeSelection] = useState<ObjectValidationType>({
		label: '',
		validationType: '',
	});
	const [labelInput, setLabelInput] = useState<IObjectValidationLabel>({
		[defaultLanguageId]: '',
	});
	const [error, setError] = useState<string>('');
	const [fieldErrors, setFieldErrors] = useState<IObjectValidationErrors>({
		labelError: '',
		typeError: '',
	});
	const [showError, setShowError] = useState<boolean>(false);

	useEffect(() => {
		setFieldErrors((currentErrors) => {
			const updatedErrors = {...currentErrors};
			updatedErrors.labelError =
				labelInput[defaultLanguageId] === '' ? requiredLabel : '';
			updatedErrors.typeError =
				typeSelection.label === '' ? requiredLabel : '';

			return updatedErrors;
		});
	}, [labelInput, typeSelection]);

	const handleSubmit = async (event: FormEvent) => {
		event.preventDefault();
		if (Object.values(fieldErrors).some((error) => error !== '')) {
			setShowError(true);
		}
		else {
			setShowError(false);
			const response = await fetch(apiURL, {
				body: JSON.stringify({
					active: false,
					engine: typeSelection.validationType,
					name: {
						[defaultLanguageId]: labelInput[defaultLanguageId],
					},
					script:
						'<#-- Insert a Groovy Script to define your validation. -->',
				}),
				headers,
				method: 'POST',
			});

			if (response.status === 401) {
				window.location.reload();
			}
			else if (response.ok) {
				onClose();

				window.location.reload();
			}
			else {
				const {
					title = Liferay.Language.get('an-error-occurred'),
				} = (await response.json()) as {title?: string};

				setError(title);
			}
		}
	};

	const handleTypeChange = (option: ObjectValidationType) => {
		setTypeSelection({
			label: option.label,
			validationType: option.validationType,
		});
	};

	const handleLabelChange = (label: IObjectValidationLabel) => {
		setLabelInput(label);
	};

	return (
		<ClayModal observer={observer}>
			<ClayForm onSubmit={handleSubmit}>
				<ClayModal.Header>
					{Liferay.Language.get('new-validation')}
				</ClayModal.Header>

				<ClayModal.Body>
					{error && (
						<ClayAlert displayType="danger">{error}</ClayAlert>
					)}

					<Input
						autoComplete="off"
						error={
							showError && fieldErrors.labelError !== ''
								? fieldErrors.labelError
								: ''
						}
						id="objectFieldLabel"
						label={Liferay.Language.get('label')}
						name="label"
						onChange={({target: {value}}) => {
							handleLabelChange({[defaultLanguageId]: value});
						}}
						required
						value={labelInput[defaultLanguageId]}
					/>

					<CustomSelect<ObjectValidationType>
						error={
							showError && fieldErrors.typeError !== ''
								? fieldErrors.typeError
								: ''
						}
						label={Liferay.Language.get('type')}
						onChange={handleTypeChange}
						options={objectValidationTypes}
						required
						value={typeSelection.label}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={() => onClose()}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton type="submit">
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
}

export default function ModalWithProvider({
	apiURL,
	objectValidationTypes,
}: IProps) {
	const [isVisible, setVisibility] = useState<boolean>(false);
	const {observer, onClose} = useModal({onClose: () => setVisibility(false)});

	useEffect(() => {
		Liferay.on('addObjectValidation', () => setVisibility(true));

		return () => Liferay.detach('addObjectValidation');

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	return (
		<ClayModalProvider>
			{isVisible && (
				<ModalAddObjectValidation
					apiURL={apiURL}
					objectValidationTypes={objectValidationTypes}
					observer={observer}
					onClose={onClose}
				/>
			)}
		</ClayModalProvider>
	);
}

type ObjectValidationType = {
	label: string;
	validationType: string;
};

interface IModal extends IProps {
	observer: any;
	onClose: () => void;
}

interface IProps {
	apiURL: string;
	objectValidationTypes: ObjectValidationType[];
}

interface IObjectValidationLabel {
	[key: string]: string;
}

interface IObjectValidationErrors {
	labelError: string;
	typeError: string;
}
