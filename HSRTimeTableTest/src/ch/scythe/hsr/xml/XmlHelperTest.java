package ch.scythe.hsr.xml;

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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class XmlHelperTest extends TestCase {

	@Test
	public void testEscapeXml() {
		// Set up fixture
		String original = "\"bread\" & \'butter\' <test>";
		// Exercise sut
		String actual = XmlHelper.escapeXml(original);
		// Verify outcome
		Assert.assertEquals("&quot;bread&quot; &amp; &apos;butter&apos; &lt;test&gt;", actual);
	}

}
