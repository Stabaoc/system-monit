package edu.song.linuxmonit.monit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import edu.song.es.client.ESClient;
import edu.song.linuxmonit.SystemMonit;

public class CpuMonit {
	public static void cpuInfo(Sigar sigar, Map<String, Object> headMap) throws SigarException{
		Map<String, Object> cpuMap = new HashMap<String, Object>();
		cpuMap.putAll(headMap);
		int cpuLength = sigar.getCpuInfoList().length;  
		// CPU的总量（单位：HZ）及CPU的相关信息  
		CpuInfo[] infos = sigar.getCpuInfoList();
		
		cpuMap.put("cpusInfo", infos);
		cpuMap.put("core", cpuLength);
		      
		CpuPerc cpuList[] = null;  
		cpuList = sigar.getCpuPercList();
		BigDecimal b = new BigDecimal(cpuList[0].getCombined()*100D); 
		cpuMap.put("cpuUsage", b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());  
		cpuMap.put("cpusDetail", cpuList);
		
		ESClient.index("Cpu", cpuMap);
		SystemMonit.LOG.debug("[Cpu]"+cpuMap);
	}
}
