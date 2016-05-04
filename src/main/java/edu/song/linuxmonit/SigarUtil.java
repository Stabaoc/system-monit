package edu.song.linuxmonit;

import java.io.File;

import org.hyperic.sigar.Sigar;

public class SigarUtil {
	private static class SigarUtilHolder {
		private static final SigarUtil INSTANCE = new SigarUtil();
		private static final Sigar Sigar = new Sigar();
	}

	private SigarUtil() {
		try {
			String file = SigarUtil.class.getClassLoader().getResource("").getPath()+"sigar/.sigar_shellrc";
			File classPath = new File(file).getParentFile();
			String path = System.getProperty("java.library.path");
			if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Windows) {
				path += ";" + classPath.getCanonicalPath();
			} else {
				path += ":" + classPath.getCanonicalPath();
			}
			System.setProperty("java.library.path", path);
		} catch (Exception e) {
		}
	}

	public static final Sigar getInstance() {
		return SigarUtilHolder.Sigar;
	}

	public static final SigarUtil getSigarUtilInstance() {
		return SigarUtilHolder.INSTANCE;
	}
}
