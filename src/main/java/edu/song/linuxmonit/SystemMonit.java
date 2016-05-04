package edu.song.linuxmonit;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import edu.song.es.client.ESConfig;
import edu.song.linuxmonit.monit.CpuMonit;
import edu.song.linuxmonit.monit.DiskMonit;
import edu.song.linuxmonit.monit.MemoryMonit;
import edu.song.linuxmonit.monit.NetMonit;
import edu.song.linuxmonit.monit.OsMonit;

/**
 * http://364434006.iteye.com/blog/1747490
 * 
 * @author stab
 * @creation 2016年3月17日
 *
 */
public class SystemMonit {
	public static final Logger LOG = LogManager.getLogger(SystemMonit.class);
	public void monit() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Sigar sigar = SigarUtil.getInstance();
			String timestamp = timestampToDate(System.currentTimeMillis());
			String ip = NetMonit.address(sigar);
			if (ip != null) {
				map.put("timestamp", timestamp);
				map.put("ip", ip);
				
				CpuMonit.cpuInfo(sigar, map);
				DiskMonit.diskInfo(sigar, map);
				MemoryMonit.memoryInfo(sigar, map);
				NetMonit.netInfo(sigar, map);
				OsMonit.osInfo(sigar, map);
			}
		} catch (SigarException e) {
			LOG.error(e);;
		}
	}

	public static String timestampToDate(long timeLong) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sssZ");// 定义格式，不显示毫秒
		Timestamp timestamp = new Timestamp(timeLong);// 获取系统当前时间
		String dateString = df.format(timestamp);
		return dateString;
	}

	public void process() {
		Timer timer = new Timer();
		timer.schedule(new Monit(), 0, ESConfig.monitInterval);
	}

	private class Monit extends TimerTask {
		@Override
		public void run() {
			monit();
		}
	}

	public static void main(String[] args) {
		LOG.info("Start Monit");
		new SystemMonit().process();
	}
}
