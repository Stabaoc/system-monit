package edu.song.linuxmonit.deploy.bean;

import java.util.ArrayList;
import java.util.List;

public class ServerInfo {
	public static List<ServerInfo> serverInfos = new ArrayList<>();
	
	public String host;
	public String user;
	public String passwd;
	
	public ServerInfo(String host, String user, String passwd) {
		this.host = host;
		this.user = user;
		this.passwd = passwd;
	}
	
	public static void init(){
		/*serverInfos.add(new ServerInfo("10.10.101.33", "root", "123456"));
		serverInfos.add(new ServerInfo("10.10.101.34", "root", "123456"));
		//serverInfos.add(new ServerInfo("10.10.101.103", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.101.105", "vcap", "password"));
		//serverInfos.add(new ServerInfo("10.10.101.106", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.101.107", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.101.110", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.101.114", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.102.101", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.102.104", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.103.102", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.103.112", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.103.180", "vcap", "password"));
		serverInfos.add(new ServerInfo("10.10.103.181", "vcap", "password"));*/
		serverInfos.add(new ServerInfo("10.10.103.177", "root", "123456"));
		//serverInfos.add(new ServerInfo("10.10.103.178", "root", "123456"));
		//serverInfos.add(new ServerInfo("10.10.103.179", "root", "123456"));
	}
}
