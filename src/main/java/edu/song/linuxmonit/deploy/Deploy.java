package edu.song.linuxmonit.deploy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import edu.song.linuxmonit.deploy.bean.ServerInfo;

public class Deploy {
	private static ExecutorService pools = Executors.newScheduledThreadPool(4);
	private static final Logger LOGGER = LogManager.getLogger(Deploy.class);

	private static final String TARFILE = "system-monit-monit.tar.gz";
	private static final String DIRNAME = "system-monit";
	private static final String SCRIPTNAME = "runMonit.sh";
	private static final String LOCAL_FILE = "/home/stab/workspace/linuxmonit/target/" + TARFILE;
	private static final String REMOTE_FILE = "~";

	public void process() {
		for (ServerInfo serverInfo : ServerInfo.serverInfos) {
			pools.submit(new RemoteTask(serverInfo));
			System.out.println(pools);
			 
			singleOp(serverInfo);
		}
	}

	public void singleOp(ServerInfo serverInfo) {
		Connection conn = new Connection(serverInfo.host);
		try {
			conn.connect();
			boolean isAuthed = conn.authenticateWithPassword(serverInfo.user, serverInfo.passwd);
			if (!isAuthed)
				return;
			// remove the old file & get process's pid
			Session sess = conn.openSession();
			sess.execCommand("rm -r " + DIRNAME + " & rm " + TARFILE + " & ps -x | grep " + DIRNAME + " & ls");
			InputStream stdout = new StreamGobbler(sess.getStdout());
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			
			String pid = null;
			getpid: {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.contains("java")) {
						String[] aa = line.split("\\?");
						pid = aa[0];
						break getpid;
					}
					System.out.println(line);
				}
			}
			sess.close();

			// kill the old process if(pid != null){
			// System.out.println(pid);
			
			 sess = conn.openSession(); sess.execCommand("kill " + pid);
			 sess.close();
			 

			// scp the new dir
			SCPClient scpClient = conn.createSCPClient();
			scpClient.put(LOCAL_FILE, REMOTE_FILE);

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOGGER.warn("sleep failed");
			}

			sess = conn.openSession();
			sess.execCommand("tar -xzvf " + TARFILE);
			stdout = new StreamGobbler(sess.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			sess.close();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOGGER.warn("sleep failed");
			}
			
			//can't run
			sess = conn.openSession();
			sess.execCommand(" cd "+DIRNAME +"/ ;cd bin/  ; pwd;chmod 741 " + SCRIPTNAME + " ; ./" + SCRIPTNAME );
			stdout = new StreamGobbler(sess.getStdout());
			br = new BufferedReader(new InputStreamReader(stdout));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			sess.close();

		} catch (IOException e) {
			LOGGER.error("Connect fail");
		} finally {
			conn.close();
		}
	}

	class RemoteTask implements Runnable {
		private ServerInfo serverInfo;

		public RemoteTask(ServerInfo serverInfo) {
			this.serverInfo = serverInfo;
		}

		@Override
		public void run() {
			singleOp(serverInfo);
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		ServerInfo.init();
		new Deploy().process();

		/*
		 * while(true){ Thread.sleep(1000); System.out.println(pools); }
		 */
		/*
		 * ServerInfo serverInfo = new ServerInfo("10.10.103.177", "root",
		 * "123456"); Connection conn = new Connection(serverInfo.host);
		 * conn.connect(); boolean isAuthed =
		 * conn.authenticateWithPassword(serverInfo.user, serverInfo.passwd); if
		 * (!isAuthed) return; // remove the old file & get process's pid
		 * Session sess = conn.openSession(); sess.execCommand("rm -r " +
		 * DIRNAME + " & rm " + TARFILE + " & ps -x | grep " + DIRNAME + " & ls"
		 * );
		 * 
		 * sess.close(); conn.close();
		 */
	}
}
