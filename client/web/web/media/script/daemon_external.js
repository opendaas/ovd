/**
 * Copyright (C) 2009-2012 Ulteo SAS
 * http://www.ulteo.com
 * Author Jeremy DESVAGES <jeremy@ulteo.com> 2009-2010
 * Author Julien LANGLOIS <julien@ulteo.com> 2012
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
 **/

var External = Class.create(Applications, {
	initialize: function(debug_) {
		Daemon.prototype.initialize.apply(this, [debug_]);

		this.applications = new Hash();
		this.running_applications = new Hash();
		this.liaison_runningapplicationtoken_application = new Hash();
		this.waiting_applications_instances = new Array();

		$('applicationsAppletContainer').innerHTML = '';

		this.applicationsPanel = new ApplicationsPanel($('appsContainer'));
	},

	do_started: function() {
		Logger.debug('[external] do_started()');

		Daemon.prototype.do_started.apply(this);

		setTimeout(this.connect_servers.bind(this), 1000);
		setTimeout(this.explorer_loop.bind(this), 2000);
	},

	applicationStatus: function(app_id_, token_, status_) {
		Logger.debug('[external] applicationStatus(token: '+token_+', status: '+status_+')');

		var app_status = 2;

		if (typeof this.running_applications.get(token_) == 'undefined') {
			Logger.info('[external] applicationStatus(token: '+token_+', status: '+status_+') - Creating "running" application "'+token_+'"');

			var app_id = this.liaison_runningapplicationtoken_application.get(token_);
			if (typeof app_id == 'undefined')
				app_id = app_id_;

			var app_object = this.applications.get(app_id);
			if (typeof app_object == 'undefined') {
				Logger.error('[external] applicationStatus(token: '+token_+', status: '+status_+') - Application "'+app_id+'" does not exist');
				return false;
			}

			var instance = new Running_Application(app_object.id, app_object.name, app_object.server, token_, app_status, this.getContext());
			this.running_applications.set(instance.pid, instance);

			if (status_ == 'started')
				Logger.info('[external] applicationStatus(token: '+token_+', status: '+status_+') - Adding "running" application "'+token_+'" to running applications list');
		} else {
			Logger.info('[external] applicationStatus(token: '+token_+', status: '+status_+') - Updating "running" application "'+token_+'" status: "'+app_status+'"');

			var instance = this.running_applications.get(token_);
			instance.update(app_status);

			if (status_ == 'stopped') {
				Logger.info('[external] applicationStatus(token: '+token_+', status: '+status_+') - Deleting "running" application "'+token_+'" from running applications list');

				var app_id = this.liaison_runningapplicationtoken_application.get(token_);
				if (typeof app_id == 'undefined')
					return false;
			}
		}

		return true;
	}
});
