<?php
/**
 * Copyright (C) 2011 Ulteo SAS
 * http://www.ulteo.com
 * Author Laurent CLOUET <laurent@ulteo.com> 2010-2011
 * Author Julien LANGLOIS <julien@ulteo.com> 2009
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
require_once(dirname(__FILE__).'/../includes/core.inc.php');

class Configuration_mode_ad extends Configuration_mode {

  public function getPrettyName() {
    return _('Microsoft');
  }

  public function careAbout($sessionmanagement_) {
    return 'microsoft' == $sessionmanagement_;
  }

  public function has_change($oldprefs, $newprefs) {
    $old = $oldprefs->get('UserDB', 'activedirectory');
    $new = $newprefs->get('UserDB', 'activedirectory');

    $change_ad = False;
    foreach(array('hosts', 'domain') as $key) {
      if ($old[$key] != $new[$key]) {
	$change_ad = True;
	break;
      }
    }

    $old = $oldprefs->get('UserGroupDB', 'enable');
    $new = $newprefs->get('UserGroupDB', 'enable');
    $g = !($old == 'sql' && $new == 'sql');

    return array($change_ad, $change_ad && $g);
  }

  public function form_valid($form) {
    $fields = array('host', 'domain', 
		    'admin_login', 'admin_password',
		    'user_group', 'sessionmanagement');

    foreach($fields as $field) {
      if (! isset($form[$field])) {
	return False;
      }
    }

    return True;

  }

  public function form_read($form, $prefs) {
    $ad_ar = array();
    $ad_ar['hosts'] = array($form['host'], $form['host2']);
    $ad_ar['domain'] = $form['domain'];
    $ad_ar['login'] = $form['admin_login'];
    $ad_ar['password'] = $form['admin_password'];


    $ad_ar['match'] = array();
    if ($form['user_group'] == 'activedirectory')
      $ad_ar['match']['memberof'] = 'memberOf';

    // Enable modules
    $module_to_enable = array('SessionManagement', 'UserDB', 'UserGroupDB');
    if ($form['sessionmanagement'] == 'internal') {
      $module_to_enable []= 'ProfileDB';
      $module_to_enable []= 'SharedFolderDB';
    }
    $module_enabled = $prefs->get('general', 'module_enable');
    $prefs->set('general', 'module_enable', array_unique(array_merge($module_enabled, $module_to_enable)));

    // Select AD as UserDB
    $prefs->set('UserDB', 'enable', 'activedirectory');

    // Push AD conf
    $prefs->set('UserDB', 'activedirectory', $ad_ar);
    //var_dump($ad_ar);

    // Select Module for UserGroupDB
    $prefs->set('UserGroupDB', 'enable', $form['user_group']);
    
    if ($form['user_group'] == 'activedirectory') { // ugly hack
      $prefs->set('UserGroupDB', 'ldap_memberof', array('match' => array('description' => 'description','name' => 'sAMAccountName', 'member' => 'member')));
    }
    
    // Set the Session Management module
    $prefs->set('SessionManagement', 'enable', $form['sessionmanagement']);

    return True;
  }

  public function config2form($prefs) {
    $form = array();
    $config = $prefs->get('UserDB', 'activedirectory');

    $form['host'] = '';
    if (isset($config['hosts'][0]))
      $form['host'] = $config['hosts'][0];
    $form['host2'] = '';
    if (isset($config['hosts'][1]))
      $form['host2'] = $config['hosts'][1];
    
    $form['domain'] = $config['domain'];

    $form['admin_login'] = $config['login'];
    $form['admin_password'] = $config['password'];

    $buf = $prefs->get('UserGroupDB', 'enable');
    $form['user_group'] = ($buf == 'activedirectory')?'activedirectory':'sql';
    
    $form['sessionmanagement'] = $prefs->get('SessionManagement', 'enable');

    return $form;
  }

  public function display($form) {
    $str= '<h1>'._('Microsoft integration').'</h1>';

    $str.= '<div class="section">';
    $str.= '<h3>Server</h3>';
    $str.= '<table>';
    $str.= '<tr><td>'._('Primary Host:').'</td><td><input type="text" name="host" value="'.$form['host'].'" /></td></tr>';
    $str.= '<tr><td>'._('Secondary Host:').'</td><td><input type="text" name="host2" value="'.$form['host2'].'" /></td>';
    $str.= '<td><span style="font-size: 0.9em; font-style: italic;">('._('optional').')</span></td>';
    $str.= '</tr>';
    $str.= '<tr><td>'._('Domain:').'</td><td><input type="text" name="domain" value="'.$form['domain'].'" /></td></tr>';
    $str.= '</table>';
    $str.= '</div>';

    $str.= '<div class="section">';
    $str.= '<h3>'._('Administrator account').'</h3>';
    $str.= '<table>';
    $str.= '<tr><td>'._('Login:').'</td><td><input type="text" name="admin_login" value="'.$form['admin_login'].'" /></td></tr>';
    $str.= '<tr><td>'._('Password:').'</td><td><input type="password" name="admin_password" value="'.$form['admin_password'].'" /></td></tr>';

    $str.= '</table>';
    $str.= '</div>';

    $str.= '<div class="section">';
    $str.= '<h3>'._('User Groups').'</h3>';
    $str.= '<input class="input_radio" type="radio" name="user_group" value="activedirectory"';
    if ($form['user_group'] == 'activedirectory')
      $str.= ' checked="checked"';
    $str.= ' />'._('Use Active Directory User Groups').'<br/>';

    $str.= '<input class="input_radio" type="radio" name="user_group" value="sql"';
    if ($form['user_group'] == 'sql')
      $str.= ' checked="checked"';
    $str.= '/>'._('Use Internal User Groups');
    $str.= '</div>';

    /*
    $str.= '<div style="display:none" class="section">';
    $str.= '<h3>'._('Windows Applications').'</h3>';
    $str.= _('Allow Windows Application link thanks to TS and AD:');
    $str.= '<input class="input_radio" type="radio" name="ts_link" value="yes" checked="checked"/>';
    $str.= _('yes');
    $str.= '<input class="input_radio" type="radio" name="ts_link" value="no" />'._('no');
    $str.= '</div>';
    */
    
    $str.= '<div class="section">';
    $str.= '<h3>'._('User on Application Server').'</h3>';
    $str.= '<input class="input_radio" type="radio" name="sessionmanagement" value="microsoft"';
    if ($form['sessionmanagement'] == 'microsoft')
      $str.= ' checked="checked"';
    $str.= ' />'._('Use Active Directory users').'<br/>';

    $str.= '<input class="input_radio" type="radio" name="sessionmanagement" value="internal"';
    if ($form['sessionmanagement'] == 'internal')
      $str.= ' checked="checked"';
    $str.= '/>'._('Use temporary generated user');
    $str.= '</div>';

    return $str;
  }

  public function display_sumup($prefs) {
    $form = $this->config2form($prefs);

    $str= '';
    $str.= '<ul>';
    $str.= '<li><strong>'._('Domain:').'</strong> '.$form['domain'].'</li>';

    $str.= '<li><strong>'._('Administrator account:').'</strong> '.$form['admin_login'].'</li>';
    
    $str.= '<li><strong>'._('User Groups:').'</strong> ';
    if ($form['user_group'] == 'activedirectory')
      $str.= _('Active Directory User Groups');
    else
      $str.= _('Use Internal User Groups');
    $str.= '</li>';
  
    $str.= '</ul>';

    return $str;
  }

}
