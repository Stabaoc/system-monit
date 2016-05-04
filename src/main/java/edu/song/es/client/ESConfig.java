package edu.song.es.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESConfig {
    private static final Logger LOG = LoggerFactory.getLogger(ESConfig.class);
    
    private final static String ESCONNECT_PROPERTIES = "esconnect.properties";
    
    public static String clusterName = "CFLog";
    public static String clientModel = "TRANSPORT";
    public static String host1 = "10.10.103.102";
    public static String host2 = "10.10.103.112";
    public static String host3 = "10.10.103.102";
    public static int port = 9300;
    public static String apmindex = "system-monit-";
    public static String type = "standard";
    public static String zeromqAddress = "tcp://10.10.105.140:6669";
    public static String zeromqRespAddress = "tcp://10.10.105.140:6670";
    public static int monitInterval = 5000;
    public static boolean useCache = true;
    
    static{
        LOG.info("Reading configuration:" + ESCONNECT_PROPERTIES);
        InputStream in = ESConfig.class.getClassLoader().getResourceAsStream(ESCONNECT_PROPERTIES);
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
        	LOG.error("Init Config failed.", e);
		} finally {
            try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        clusterName = props.getProperty("clusterName");
        clientModel = props.getProperty("clientModel");
        host1 = props.getProperty("host1");
        host2 = props.getProperty("host2");
        host3 = props.getProperty("host3");
        port = Integer.parseInt(props.getProperty("port"));
        apmindex = props.getProperty("apmIndex");
        monitInterval= Integer.parseInt(props.getProperty("monitInterval"));
        
        LOG.info("elasticsearch-client: { clusterName: " + clusterName + " ,clusterModel: " + clientModel + " ,apmindex: " + apmindex + 
        		" ,host: [" + host1 + "," +host2 + "," +host3 + " : " + port + "]  ,zeromqAddress:" + zeromqAddress + ",monitInterval" + 
        		monitInterval + " ,useCache: "  + useCache + "}");
    }
    
    public static void main(String[] args) {
		System.out.println(ESConfig.apmindex);
	}
}