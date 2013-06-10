/*
 * Copyright (C) 2010-2012 Ulteo SAS
 * http://www.ulteo.com
 * Author Julien LANGLOIS <julien@ulteo.com> 2013
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.ulteo.ovd.applet;


import java.applet.Applet;
import java.awt.BorderLayout;

public class DesktopContainer extends Applet {
	@Override
	public final void init() {
		System.out.println(this.getClass().toString() + " init");
		this.setLayout(new BorderLayout());
	}
	
	@Override
	public final void start() {
		System.out.println(this.getClass().toString() +" start");
	}
	
	@Override
	public final void stop() {
		System.out.println(this.getClass().toString() +" stopped");
	}
	
	@Override
	public final void destroy() {
	}
}
