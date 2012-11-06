/* 
 * Copyright (C) 2011 - 2012 Michi Gysel <michael.gysel@gmail.com>
 *
 * This file is part of the HSR Timetable.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.scythe.hsr;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import ch.scythe.hsr.api.ui.UiDay;
import ch.scythe.hsr.api.ui.UiLesson;
import ch.scythe.hsr.api.ui.UiWeek;
import ch.scythe.hsr.enumeration.Weekday;

public class DayFragment extends DialogFragment {

	public static final String FRAGMENT_PARAMETER_DATA = "week";
	public static final String FRAGMENT_PARAMETER_WEEKDAY = "position";

	private UiWeek week;
	private Weekday weekDay;
	private LayoutInflater layoutInflater;

	public DayFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		if (arguments != null) {
			week = (UiWeek) (arguments.getSerializable(FRAGMENT_PARAMETER_DATA));
			weekDay = (Weekday) (arguments.getSerializable(FRAGMENT_PARAMETER_WEEKDAY));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle instanceToSave) {
		super.onSaveInstanceState(instanceToSave);
		instanceToSave.putSerializable(FRAGMENT_PARAMETER_DATA, week);
		instanceToSave.putSerializable(FRAGMENT_PARAMETER_WEEKDAY, weekDay);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_PARAMETER_DATA)) {
			week = (UiWeek) (savedInstanceState.getSerializable(FRAGMENT_PARAMETER_DATA));
			weekDay = (Weekday) (savedInstanceState.getSerializable(FRAGMENT_PARAMETER_WEEKDAY));
		}

		layoutInflater = getLayoutInflater(savedInstanceState);
		View v = inflater.inflate(R.layout.timetable_day, container, false);
		TableLayout timeTable = (TableLayout) v.findViewById(R.id.timeTable);

		updateTable(timeTable);

		return v;
	}

	private void updateTable(TableLayout timeTable) {
		UiDay day = week.getDay(weekDay);
		if (day != null) {
			List<UiLesson> lessons = day.getLessons();
			if (lessons.size() > 0) {
				createAndFormatTableRows(lessons, timeTable, layoutInflater);
			} else {
				String message = getString(R.string.message_no_lessons);
				showInfoRow(timeTable, message);
			}
		} else {
			String message = getString(R.string.message_no_data);
			showInfoRow(timeTable, message);
		}
	}

	private void showInfoRow(TableLayout timeTable, String message) {
		View descriptionRow = (View) layoutInflater.inflate(R.layout.timetable_info_row, null);
		TextView infoField = (TextView) descriptionRow.findViewById(R.id.infoRow);
		infoField.setText(message);
		timeTable.addView(descriptionRow);
	}

	public void updateDate(UiWeek week) {
		this.week = week;
		View view = getView();
		if (view != null) {
			TableLayout timeTable = (TableLayout) view.findViewById(R.id.timeTable);
			// remove all existing table rows and add them again
			timeTable.removeAllViews();
			updateTable(timeTable);
		} else {
			Log.w("DayFragment", "Not possible to update data as the view is not available.");
		}
	}

	private void createAndFormatTableRows(List<UiLesson> lessons, TableLayout timeTable, LayoutInflater layoutInflater) {

		String lastLessonTimeslot = "";
		boolean firstRow = true;

		for (UiLesson lesson : lessons) {

			View row = layoutInflater.inflate(R.layout.timetable_row, null);
			timeTable.addView(row);

			View horizontalLine = row.findViewById(R.id.horizontalLine);
			if (firstRow) {
				horizontalLine.setVisibility(View.GONE);
				firstRow = false;
			}

			TextView timeUnitField = (TextView) row.findViewById(R.id.rowTimeunit);
			TextView lessonField = (TextView) row.findViewById(R.id.rowLesson);
			TextView lecturerFieldShort = (TextView) row.findViewById(R.id.rowLecturerShort);
			TextView lecturerFieldLong = (TextView) row.findViewById(R.id.rowLecturerLong);
			TextView roomField = (TextView) row.findViewById(R.id.rowRoom);
			TextView descriptionField = (TextView) row.findViewById(R.id.rowDescription);
			TextView typeField = (TextView) row.findViewById(R.id.rowType);

			// fill values into row

			String newTimeslot = lesson.getTimeSlot();
			if (!lastLessonTimeslot.equals(newTimeslot)) {
				timeUnitField.setText(newTimeslot);
			} else {
				timeUnitField.setText("");
				horizontalLine.setVisibility(View.GONE);
			}
			lastLessonTimeslot = newTimeslot;

			lessonField.setText(lesson.getName());
			roomField.setText(lesson.getRoom());

			if (lecturerFieldShort != null)
				lecturerFieldShort.setText(lesson.getLecturerShort());
			if (lecturerFieldLong != null)
				lecturerFieldLong.setText(lesson.getLecturerLong());

			typeField.setText(lesson.getType());

			if (lesson.hasDescription()) {
				descriptionField.setText(lesson.getDescription());
			} else {
				descriptionField.setVisibility(View.GONE);
			}
		}

	}
}
