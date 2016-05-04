package edu.song.linuxmonit.monit;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import edu.song.es.client.ESClient;
import edu.song.linuxmonit.SystemMonit;

public class NetMonit {
	private static Map<String, NetInterfaceStat> lastNet = new HashMap<>();

	public static String address(Sigar sigar) {
		String address = null;
		try {
			address = InetAddress.getLocalHost().getHostAddress();
			// 没有出现异常而正常当取到的IP时，如果取到的不是网卡循回地址时就返回
			// 否则再通过Sigar工具包中的方法来获取
			if (!NetFlags.LOOPBACK_ADDRESS.equals(address)  && address != null && !address.startsWith("127")) {
				SystemMonit.LOG.info("Get Ip: {" + address + "}, Loop Back Add : {" + NetFlags.LOOPBACK_ADDRESS + "}");
				return address;
			}
		} catch (UnknownHostException e) {
			SystemMonit.LOG.error(e);
		}
		try {
			address = sigar.getNetInterfaceConfig().getAddress();
		} catch (SigarException e) {
			address = NetFlags.LOOPBACK_ADDRESS;
		} finally {
			SystemMonit.LOG.info("Get Ip: {" + address + "}, Loop Back Add : {" + NetFlags.LOOPBACK_ADDRESS + "}");
		}
		return address;
	}

	public static void netInfo(Sigar sigar, Map<String, Object> headMap) throws SigarException {
		// 取到当前机器的MAC地址
		String[] ifaces = sigar.getNetInterfaceList();
		String hwaddr = null;
		for (int i = 0; i < ifaces.length; i++) {
			NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
			if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
					|| NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
				continue;
			}
			hwaddr = cfg.getHwaddr();
			// break;
		}

		// 获取网络流量等信息
		String ifNames[] = sigar.getNetInterfaceList();
		List<Map<String, Object>> net = new ArrayList<>();
		for (int i = 0; i < ifNames.length; i++) {
			Map<String, Object> netMap = new HashMap<>();

			String name = ifNames[i];
			NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);

			netMap.put("name", ifNames[i]);
			netMap.put("address", ifconfig.getAddress());
			netMap.put("netmask", ifconfig.getNetmask());
			if ((ifconfig.getFlags() & 1L) <= 0L) {
				SystemMonit.LOG.warn("!IFF_UP...skipping getNetInterfaceStat");
				continue;
			}

			NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
			if (lastNet.get(ifNames[i]) != null) {
				NetInterfaceStat laststat = lastNet.get(ifNames[i]);
				netMap.put("RxPackets", ifstat.getRxPackets() - laststat.getRxPackets());// 接收的总包裹数
				netMap.put("TxPackets", ifstat.getTxPackets() - laststat.getTxPackets());// 发送的总包裹数
				netMap.put("RxBytes", ifstat.getRxBytes() - laststat.getRxBytes());// 接收到的总字节数
				netMap.put("TxBytes", ifstat.getTxBytes() - laststat.getTxBytes());// 发送的总字节数
				netMap.put("RxErrors", ifstat.getRxErrors() - laststat.getRxErrors());// 接收到的错误包数
				netMap.put("TxErrors", ifstat.getTxErrors() - laststat.getTxErrors());// 发送数据包时的错误数
				netMap.put("RxDropped", ifstat.getRxDropped() - laststat.getRxDropped());// 接收时丢弃的包数
				netMap.put("TxDropped ", ifstat.getTxDropped() - laststat.getTxDropped());// 发送时丢弃的包数
			} else {
				netMap.put("RxPackets", ifstat.getRxPackets());// 接收的总包裹数
				netMap.put("TxPackets", ifstat.getTxPackets());// 发送的总包裹数
				netMap.put("RxBytes", ifstat.getRxBytes());// 接收到的总字节数
				netMap.put("TxBytes", ifstat.getTxBytes());// 发送的总字节数
				netMap.put("RxErrors", ifstat.getRxErrors());// 接收到的错误包数
				netMap.put("TxErrors", ifstat.getTxErrors());// 发送数据包时的错误数
				netMap.put("RxDropped", ifstat.getRxDropped());// 接收时丢弃的包数
				netMap.put("TxDropped ", ifstat.getTxDropped());// 发送时丢弃的包数
			}
			lastNet.put(ifNames[i], sigar.getNetInterfaceStat(name));

			netMap.put("mac", hwaddr);
			netMap.put("net", net);
			netMap.putAll(headMap);
			ESClient.index("Network", netMap);
			SystemMonit.LOG.debug("[Network]" + netMap);
		}
	}

	public static void main(String[] args) throws UnknownHostException {
		switch (args[0]) {
		case "1":
			StringBuilder IFCONFIG = new StringBuilder();
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
						.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
								&& inetAddress.isSiteLocalAddress()) {
							IFCONFIG.append(inetAddress.getHostAddress().toString() + "\n");
						}
					}
				}
			} catch (SocketException ex) {
			}
			System.out.println(IFCONFIG);
			break;
		case "2":
			InetAddress host = InetAddress.getLocalHost();
			System.out.println(host);
			break;
		case "3":
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		default:
			break;
		}
	}
}
