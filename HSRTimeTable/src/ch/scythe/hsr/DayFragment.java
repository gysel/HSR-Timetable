/* 
 * Copyright (C) 2011 Michi Gysel <michael.gysel@gmail.com>
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
import java.util.Map.Entry;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import ch.scythe.hsr.entity.Day;
import ch.scythe.hsr.entity.Lesson;
import ch.scythe.hsr.entity.TimetableWeek;
import ch.scythe.hsr.enumeration.TimeUnit;
import ch.scythe.hsr.enumeration.WeekDay;

public class DayFragment extends DialogFragment {

	public static final String FRAGMENT_PARAMETER_DATA = "week";
	public static final String FRAGMENT_PARAMETER_WEEKDAY = "position";

	private TimetableWeek week;
	private WeekDay weekDay;
	private LayoutInflater layoutInflater;

	public DayFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		if (arguments != null) {
			week = (TimetableWeek) (arguments.getSerializable(FRAGMENT_PARAMETER_DATA));
			weekDay = (WeekDay) (arguments.getSerializable(FRAGMENT_PARAMETER_WEEKDAY));
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
			week = (TimetableWeek) (savedInstanceState.getSerializable(FRAGMENT_PARAMETER_DATA));
			weekDay = (WeekDay) (savedInstanceState.getSerializable(FRAGMENT_PARAMETER_WEEKDAY));
		}

		layoutInflater = getLayoutInflater(savedInstanceState);
		View v = inflater.inflate(R.layout.timetable_day, container, false);
		TableLayout timeTable = (TableLayout) v.findViewById(R.id.timeTable);

		TextView headerCell = (TextView) v.findViewById(R.id.headerRow);
		headerCell.setText(weekDay.name());

		updateTable(timeTable);

		return v;
	}

	private void updateTable(TableLayout timeTable) {
		Day day = week.getDay(weekDay);
		if (day != null) {
			for (Entry<TimeUnit, List<Lesson>> entry : day.getLessons().entrySet()) {
				TimeUnit timeUnit = entry.getKey();
				List<Lesson> lessonsPerTimeUnit = entry.getValue();

				if (lessonsPerTimeUnit == null || lessonsPerTimeUnit.isEmpty()) {
					// no lessons at the given time slot
					createAndFormatTableRow(null, timeUnit, timeTable, layoutInflater);
				} else {
					for (Lesson lesson : lessonsPerTimeUnit) {
						createAndFormatTableRow(lesson, timeUnit, timeTable, layoutInflater);
					}
				}
			}
		} else {
			String message = "No data available. Please refresh.";
			TableRow row = createTableRow(message, R.layout.timetable_info_row, layoutInflater);
			timeTable.addView(row);
		}
	}

	public void updateDate(TimetableWeek week) {
		this.week = week;
		TableLayout timeTable = (TableLayout) getView().findViewById(R.id.timeTable);
		// remove all existing table rows and add them again
		timeTable.removeAllViews();
		updateTable(timeTable);
	}

	private void createAndFormatTableRow(Lesson lesson, TimeUnit timeUnit, TableLayout timeTable,
			LayoutInflater layoutInflater) {
		// init row
		TableRow row = new TableRow(getActivity());
		formatRowBackground(timeUnit, row);
		timeTable.addView(row);

		TextView timeUnitField = createTableColumn(row, layoutInflater);
		TextView lessonField = createTableColumn(row, layoutInflater);
		TextView roomField = createTableColumn(row, layoutInflater);
		TextView lecturerField = createTableColumn(row, layoutInflater);
		// fill values into row
		timeUnitField.setText(timeUnit.toDurationString(" - "));
		if (lesson != null) {
			lessonField.setText(lesson.getIdentifierShort());
			roomField.setText(lesson.getRoom());
			lecturerField.setText(lesson.getLecturersAsString(", "));

			if (lesson.hasDescription()) {

				String text = lesson.getDescription();
				TableRow descriptionRow = createTableRow(text, R.layout.timetable_description_row, layoutInflater);
				formatRowBackground(timeUnit, descriptionRow);

				timeTable.addView(descriptionRow);

			}

		} else {
			lessonField.setText(getString(R.string.default_novalue));
		}

	}

	private TableRow createTableRow(String text, int layout, LayoutInflater layoutInflater) {
		TableRow descriptionRow = (TableRow) layoutInflater.inflate(layout, null);
		// TODO can we use an id here?
		TextView infoField = (TextView) descriptionRow.getChildAt(0);
		infoField.setText(text);
		return descriptionRow;
	}

	private void formatRowBackground(TimeUnit timeUnit, TableRow row) {
		if (timeUnit.getId() % 2 == 1) { // hightlight every other row
			row.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd));
		}
	}

	private TextView createTableColumn(TableRow row, LayoutInflater layoutInflater) {
		TextView field = (TextView) layoutInflater.inflate(R.layout.timetable_cell, null);
		row.addView(field);
		return field;
	}

}
