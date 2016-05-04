package edu.song.linuxmonit.monit;

import java.util.HashMap;
import java.util.Map;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import edu.song.es.client.ESClient;
import edu.song.linuxmonit.SystemMonit;

public class DiskMonit {
	public static void diskInfo(Sigar sigar, Map<String, Object> headMap) throws SigarException {
		FileSystem fslist[] = sigar.getFileSystemList();
		for (int i = 0; i < fslist.length; i++) {
			FileSystem fs = fslist[i];
			if (fs.getType() == 2) {
				Map<String, Object> map = new HashMap<>();
				map.put("devName", fs.getDevName());
				map.put("dirName", fs.getDirName());
				map.put("sysType", fs.getSysTypeName());
				map.put("typeName", fs.getTypeName());
				map.put("dirName", fs.getDirName());
				FileSystemUsage usage = null;
				
				usage = sigar.getFileSystemUsage(fs.getDirName());
				Map<String, Object> usageMap = new HashMap<>();
				usageMap.put("total", usage.getTotal());
				usageMap.put("free", usage.getFree());
				usageMap.put("avail", usage.getAvail());
				usageMap.put("used", usage.getUsed());
				usageMap.put("usagePer", usage.getUsePercent() * 100D);
				map.put("usage", usageMap);
			
				map.putAll(headMap);
				ESClient.index("Disk", map);
				SystemMonit.LOG.debug("[Disk]"+map);
			}
		}
	}
}
