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
package ch.scythe.hsr.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import ch.scythe.hsr.error.ResponseParseException;

import com.google.gson.Gson;

public class GsonParser {

	public JsonTimetableWeek parse(InputStream json) throws ResponseParseException {

		JsonTimetableWeek result = null;
		try {
			Reader r = new InputStreamReader(json, "UTF-8");
			result = new Gson().fromJson(r, JsonTimetableWeek.class);
		} catch (UnsupportedEncodingException e) {
			throw new ResponseParseException(e);
		}

		return result;

	}
}
