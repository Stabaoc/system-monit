package edu.song.linuxmonit.monit;

import java.util.HashMap;
import java.util.Map;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

import edu.song.es.client.ESClient;
import edu.song.linuxmonit.SystemMonit;

public class MemoryMonit {
	public static void memoryInfo(Sigar sigar, Map<String, Object> headMap) throws SigarException{
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> memMap = new HashMap<>();
		Map<String, Object> swapMap = new HashMap<>();
		Mem mem = sigar.getMem();
		Swap swap = sigar.getSwap();
		memMap.put("total", mem.getTotal());
		memMap.put("free", mem.getFree());
		memMap.put("used", mem.getUsed());
		memMap.put("freePre", mem.getFreePercent());
		memMap.put("usedPre", mem.getUsedPercent());
		
		swapMap.put("total", swap.getTotal());
		swapMap.put("free", swap.getFree());
		swapMap.put("used", swap.getUsed());
		map.put("mem", memMap);
		map.put("swap", swapMap);
		map.putAll(headMap);
		ESClient.index("Memory", map);
		SystemMonit.LOG.debug("[Memory]"+map);
	}
}
