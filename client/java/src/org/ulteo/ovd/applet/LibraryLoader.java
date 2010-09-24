package org.ulteo.ovd.applet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LibraryLoader {
	public static final String RESOURCE_LIBRARY_DIRECTORY_WINDOWS = "/ressources/WindowsLibs";
	public static final String LIB_WINDOW_PATH_NAME = "libWindowsPaths.dll";
	public static final String RESOURCE_LIBRARY_DIRECTORY_LINUX = "/ressources/LinuxLibs";
	public static final String LIB_X_CLIENT_AREA = "libXClientArea.so";
	
	//This method is called from an applet
	public static void LoadLibrary(String ressourceDirectory, String DLLName) {
		InputStream dllResource = LibraryLoader.class.getResourceAsStream(ressourceDirectory+"/"+DLLName);
		String fileSeparator= System.getProperty("file.separator");
		//test the resource in order to know if client is started in applet mode
		if (dllResource != null) {
			String destFile = System.getProperty("java.io.tmpdir") + fileSeparator + DLLName;
			try {
				int c = 0;
				File outputFile = new File(destFile);
				FileOutputStream fos = new FileOutputStream(outputFile);

				while ((c = dllResource.read()) != -1) {
					fos.write(c);
				}
				fos.close();
			} catch (FileNotFoundException e) {
				org.ulteo.Logger.error("Unable to find "+destFile+ e.getMessage());
			} catch (IOException e) {
				org.ulteo.Logger.error("Error while creating "+destFile);
			}
			try {
				System.load(destFile);
			} catch (SecurityException e) {
				org.ulteo.Logger.error("Library loading generate an security exception: "+e.getMessage());
			} catch (UnsatisfiedLinkError e) {
				org.ulteo.Logger.error("Error while loading library: "+e.getMessage());
			} catch (NullPointerException e) {
				org.ulteo.Logger.error("Unable to load an empty library: "+e.getMessage());
			}
		}
	}
	
	//This method is called from an non applet client
	public static void LoadLibrary(String LibName) throws FileNotFoundException {
		String fileSeparator= System.getProperty("file.separator");
		String libraryPaths = System.getProperty("java.library.path");

		List<String> paths = new ArrayList<String>();
		paths.add(System.getProperty("user.dir"));
		for (String each : libraryPaths.split(":"))
			paths.add(each);

		for (String each : paths) {
			int len = each.length();
			if (each.substring((len - 1), len).equals(fileSeparator))
				each += fileSeparator;
			each += LibName;

			if (new File(each).exists()) {
				System.load(each);
				return;
			}
		}

		throw new FileNotFoundException("Unable to find required library: "+LibName);
	}
	
}
