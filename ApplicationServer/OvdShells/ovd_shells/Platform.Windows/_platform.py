# -*- coding: utf-8 -*-

# Copyright (C) 2010 Ulteo SAS
# http://www.ulteo.com
# Author Laurent CLOUET <laurent@ulteo.com> 2010
# Author Julien LANGLOIS <julien@ulteo.com> 2010
#
# This program is free software; you can redistribute it and/or 
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; version 2
# of the License
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

import os
import sys

import pythoncom
import win32api
import win32com.client
from win32com.shell import shell, shellcon
import win32con
import win32event
import win32file
import win32netcon
import win32process
import win32wnet


def findProcessWithEnviron(pattern):
	return None


def launch(cmd, wait=False):
	(hProcess, hThread, dwProcessId, dwThreadId) = win32process.CreateProcess(None, cmd, None , None, False, 0 , None, None, win32process.STARTUPINFO())
	
	if wait:
		win32event.WaitForSingleObject(hProcess, win32event.INFINITE)
	
	win32file.CloseHandle(hProcess)
	win32file.CloseHandle(hThread)
	
	return dwProcessId

def kill(pid):
	hProcess = win32api.OpenProcess(win32con.PROCESS_TERMINATE, False, pid)
	if hProcess is None:
		 print "doesn't exist pid"
		 return False
	
	ret = win32process.TerminateProcess(hProcess, 0)
	
	win32file.CloseHandle(hProcess);
	return ret

def getUserSessionDir():
	d = shell.SHGetSpecialFolderPath(None, shellcon.CSIDL_APPDATA)
	return os.path.join(d, "ulteo", "ovd")


def launchIntegratedClient(configuration_file_):
	if os.path.exists(configuration_file_) == False:                                                                            
		return False
	
	java_cmd = detectJavaw()
	if java_cmd is None:
		print "No java installed on the system"
		return False
	
	
	jar_location = r"OVDExternalAppsClient.jar"
	folder = None
	
	dirs = os.environ["PATH"].split(";")
	dirs.insert(0, os.path.abspath(os.path.curdir))
	
	for d in dirs:
		path = os.path.join(d, jar_location)
		if os.path.exists(path):
			folder = d
			break
	
	if folder is None:
		print "No OVD integrated client installed on the system"
		return None
	
	java_cmd = java_cmd.replace("%1", jar_location)
	java_cmd = java_cmd.replace("%*", '-c "%s" -o "%s"'%(configuration_file_, os.path.join(os.path.expanduser('~'), "ovd-externalapps-dump.txt")))
	
	(hProcess, hThread, dwProcessId, dwThreadId) = win32process.CreateProcess(None, java_cmd, None , None, False, 0 , None, folder, win32process.STARTUPINFO())
	win32file.CloseHandle(hProcess)
	win32file.CloseHandle(hThread)
	  
	return True 


def detectJavaw():
	key = None
	
	try:
		key = win32api.RegOpenKey(win32con.HKEY_CLASSES_ROOT, r"Applications\javaw.exe\shell\open\command", 0, win32con.KEY_READ)
		data = win32api.RegQueryValue(key, None)
	except Exception, err:
		return None
	
	finally:
		if key is not None:
			win32api.RegCloseKey(key)
	
	return data


def startDesktop():
	launch("explorer", True)

def startSeamless():
	launch("seamlessrdpshell")
	
def transformCommand(cmd_, args_):
		if "%1" in cmd_ and len(args_)>0:
			return cmd_.replace("%1", args[0])
		
		return cmd_

def getSubProcess(ppid):
	pythoncom.CoInitialize()
	WMI = win32com.client.GetObject('winmgmts:')
	processes = WMI.InstancesOf('Win32_Process')
	
	pids = []
	
	for process in processes:
		pid = process.Properties_('ProcessID').Value
		parent = process.Properties_('ParentProcessId').Value
		
		if parent == ppid:
			pids.append(pid)
	
	return pids


def mountShares():
	key = None
	profile = {}
	try:
		key = win32api.RegOpenKey(win32con.HKEY_CURRENT_USER, r"Software\ulteo\ovd\profile", 0, win32con.KEY_READ)
		
		for item in ["host", "directory", "login", "password"]: 
			(profile[item], type_) = win32api.RegQueryValueEx(key, item)
			if type_ is not win32con.REG_SZ:
				raise Exception()
		
	except Exception, err:
		print "No profile to mount"
		profile = None
	
	finally:
		if key is not None:
			win32api.RegCloseKey(key)
	
	if profile is not None:
		try:
			win32wnet.WNetAddConnection2(win32netcon.RESOURCETYPE_DISK, "U:", r"\\%s\%s"%(share["host"], share["directory"]), None, share["login"], share["password"])
		  
		except Exception, err:
			cmd = "net use U: \\\\%s\\%s %s /user:%s"%(profile["host"], profile["directory"], profile["password"], profile["login"])
			print "Unable to mount share: ",err
			print "Try with this command: ",cmd
	
	
	shareNum = 0
	while True:
		key = None
		share = {}
		try:
			key = win32api.RegOpenKey(win32con.HKEY_CURRENT_USER, r"Software\ulteo\ovd\share_%d"%(shareNum), 0, win32con.KEY_READ)
			
			for item in ["host", "directory", "login", "password"]: 
				(share[item], type_) = win32api.RegQueryValueEx(key, item)
				if type_ is not win32con.REG_SZ:
					raise Exception()
		
		except Exception, err:
			print "No share_%d defined"%(shareNum)
			share = None
		finally:
			if key is not None:
				win32api.RegCloseKey(key)
		
		if share is None:
			break
		
		try:
			win32wnet.WNetAddConnection2(win32netcon.RESOURCETYPE_DISK, None, r"\\%s\%s"%(share["host"], share["directory"]), None, share["login"], share["password"])
		
		except Exception, err:
			cmd = "net use * \\\\%s\\%s %s /user:%s"%(share["host"], share["directory"], share["password"], share["login"])
			print "Unable to mount share: ",err
			print "Try with this command: ",cmd
		
		shareNum+= 1
