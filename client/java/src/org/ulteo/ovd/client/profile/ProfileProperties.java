/*
 * Copyright (C) 2010 Ulteo SAS
 * http://www.ulteo.com
 * Author Thomas MOUTON <thomas@ulteo.com> 2010
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

package org.ulteo.ovd.client.profile;

public class ProfileProperties {
	public static final int SCREENSIZE_800X600 = 0;
	public static final int SCREENSIZE_1024X768 = 1;
	public static final int SCREENSIZE_1280X678 = 2;
	public static final int MAXIMIZED = 3;
	public static final int FULLSCREEN = 4;
	
	public static final int MODE_AUTO = 0;
	public static final int MODE_DESKTOP = 1;
	public static final int MODE_APPLICATIONS = 2;

	private String login = System.getProperty("user.name");
	private String host = null;
	private int sessionMode = 0;
	private boolean autoPublish = false;
	private boolean useLocalCredentials = false;
	private int screensize = 0;
	private String lang = null;
	private String keymap = null;
	
	public ProfileProperties() {}

	public ProfileProperties(String login_, String host_, int sessionMode_, boolean autoPublish_, boolean useLocalCredentials_, int screensize_, String lang, String keymap) {
		this.login = login_;
		this.host = host_;
		this.sessionMode = sessionMode_;
		this.autoPublish = autoPublish_;
		this.useLocalCredentials = useLocalCredentials_;
		this.screensize = screensize_;
		this.lang = lang;
		this.keymap = keymap;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login_) {
		this.login = login_;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host_) {
		this.host = host_;
	}

	public int getSessionMode() {
		return this.sessionMode;
	}

	public void setSessionMode(int sessionMode_) {
		this.sessionMode = sessionMode_;
	}

	public boolean getAutoPublish() {
		return this.autoPublish;
	}

	public void setUseLocalCredentials(boolean useLocalCredentials_) {
		this.useLocalCredentials = useLocalCredentials_;
	}
	
	public boolean getUseLocalCredentials() {
		return this.useLocalCredentials;
	}

	public void setAutoPublish(boolean autoPublish_) {
		this.autoPublish = autoPublish_;
	}

	public int getScreenSize() {
		return this.screensize;
	}

	public void setScreenSize(int screenSize_) {
		switch (screenSize_) {
			case SCREENSIZE_800X600:
			case SCREENSIZE_1024X768:
			case SCREENSIZE_1280X678:
			case MAXIMIZED:
			case FULLSCREEN:
				break;
			default:
				screenSize_ = FULLSCREEN;
		}
		this.screensize = screenSize_;
	}
	
	public String getLang() {
		return this.lang;
	}
	
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getKeymap() {
		return this.keymap;
	}
	
	public void setKeymap(String keymap) {
		this.keymap = keymap;
	}
}
