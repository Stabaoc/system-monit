package edu.song.linuxmonit.monit;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import edu.song.es.client.ESClient;
import edu.song.linuxmonit.SystemMonit;

public class OsMonit {
	public static void osInfo(Sigar sigar, Map<String, Object> headMap) throws SigarException{
		Map<String, Object> map = new HashMap<>();
		String hostname = "";  
		try {  
		    hostname = InetAddress.getLocalHost().getHostName();  
		} catch (Exception exc) {  
		    try {  
		        hostname = sigar.getNetInfo().getHostName();  
		    } catch (SigarException e) {  
		        hostname = "localhost.unknown";  
		    } finally {  
		        sigar.close();  
		    }  
		}
		// 取当前操作系统的信息  
		OperatingSystem OS = OperatingSystem.getInstance();
		map.put("hostname", hostname);
		// 操作系统内核类型如： 386、486、586等x86  
		map.put("arch", OS.getArch());  
		map.put("cpuEndian", OS.getCpuEndian());
		map.put("dataModel", OS.getDataModel()); 
		// 系统描述  
		map.put("description", OS.getDescription());  
		// 操作系统类型  
		map.put("osname", OS.getName());  
		// 操作系统的卖主  
		map.put("vendor", OS.getVendor());  
		// 卖主名称  
		map.put("vendorCodeName", OS.getVendorCodeName());  
		// 操作系统名称  
		map.put("vendorName", OS.getVendorName());  
		// 操作系统卖主类型  
		map.put("vendorVersion", OS.getVendorVersion());  
		// 操作系统的版本号  
		map.put("version", OS.getVersion());  
		map.putAll(headMap);
		ESClient.index("OS", map);
		SystemMonit.LOG.debug("[OS]"+map);
	}
}
