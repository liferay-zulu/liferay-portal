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

package com.liferay.calendar.internal.upgrade.v4_2_1;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalService;
import com.liferay.calendar.util.JCalendarUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * @author István András Dézsi
 */
public class AllDayCalendarBookingUpgradeProcess extends UpgradeProcess {

	public AllDayCalendarBookingUpgradeProcess(
		CalendarBookingLocalService calendarBookingLocalService,
		UserLocalService userLocalService) {

		_calendarBookingLocalService = calendarBookingLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		List<CalendarBooking> allDayCalendarBookings =
			_getAllDayCalendarBookings();

		User user = null;

		for (CalendarBooking calendarBooking : allDayCalendarBookings) {
			user = _userLocalService.fetchUser(calendarBooking.getUserId());

			if (user == null) {
				continue;
			}

			Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
				calendarBooking.getStartTime(), user.getTimeZone());

			Calendar endTimeJCalendar = JCalendarUtil.getJCalendar(
				calendarBooking.getEndTime(), user.getTimeZone());

			if (_isMidnight(startTimeJCalendar) &&
				_isLastHour(endTimeJCalendar)) {

				Calendar startTimeUTCJCalendar = JCalendarUtil.getJCalendar(
					startTimeJCalendar.get(Calendar.YEAR),
					startTimeJCalendar.get(Calendar.MONTH),
					startTimeJCalendar.get(Calendar.DATE),
					startTimeJCalendar.get(Calendar.HOUR_OF_DAY),
					startTimeJCalendar.get(Calendar.MINUTE),
					startTimeJCalendar.get(Calendar.SECOND),
					startTimeJCalendar.get(Calendar.MILLISECOND), _utcTimeZone);

				Calendar endTimeUTCJCalendar = JCalendarUtil.getJCalendar(
					endTimeJCalendar.get(Calendar.YEAR),
					endTimeJCalendar.get(Calendar.MONTH),
					endTimeJCalendar.get(Calendar.DATE),
					endTimeJCalendar.get(Calendar.HOUR_OF_DAY),
					endTimeJCalendar.get(Calendar.MINUTE),
					endTimeJCalendar.get(Calendar.SECOND),
					endTimeJCalendar.get(Calendar.MILLISECOND), _utcTimeZone);

				calendarBooking.setStartTime(
					startTimeUTCJCalendar.getTimeInMillis());
				calendarBooking.setEndTime(
					endTimeUTCJCalendar.getTimeInMillis());

				_calendarBookingLocalService.updateCalendarBooking(
					calendarBooking);
			}
		}
	}

	private List<CalendarBooking> _getAllDayCalendarBookings() {
		DynamicQuery dynamicQuery = _calendarBookingLocalService.dynamicQuery();

		Property allDayProperty = PropertyFactoryUtil.forName("allDay");

		dynamicQuery.add(allDayProperty.eq(Boolean.TRUE));

		return _calendarBookingLocalService.dynamicQuery(dynamicQuery);
	}

	private boolean _isLastHour(Calendar jCalendar) {
		int hour = jCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = jCalendar.get(Calendar.MINUTE);

		if ((hour == 23) && (minute == 59)) {
			return true;
		}

		return false;
	}

	private boolean _isMidnight(Calendar jCalendar) {
		int hour = jCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = jCalendar.get(Calendar.MINUTE);

		if ((hour == 0) && (minute == 0)) {
			return true;
		}

		return false;
	}

	private static final TimeZone _utcTimeZone = TimeZone.getTimeZone(
		StringPool.UTC);

	private final CalendarBookingLocalService _calendarBookingLocalService;
	private final UserLocalService _userLocalService;

}