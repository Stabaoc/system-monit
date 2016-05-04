package edu.song.es.client;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectES{
	private static final Logger logger = LoggerFactory.getLogger(ConnectES.class);
	private static ConnectES instance = null;
	private Client client = null;
	
	private ConnectES(){
        
		if(client==null){
			if("NODE".equals(ESConfig.clientModel)){
				client = NodeBuilder.nodeBuilder().clusterName(ESConfig.clusterName).client(true).node().client();
			} else if("TRANSPORT".equals(ESConfig.clientModel)){
				client= new TransportClient(ImmutableSettings.settingsBuilder().put("cluster.name", ESConfig.clusterName).build())
					.addTransportAddress(new InetSocketTransportAddress(ESConfig.host1, ESConfig.port))
					.addTransportAddress(new InetSocketTransportAddress(ESConfig.host2, ESConfig.port))
					.addTransportAddress(new InetSocketTransportAddress(ESConfig.host3, ESConfig.port));
			} else {
				logger.warn("es clientModel doesn't exist !");
			}
			logger.debug("es client establish !");
		}
	}
	
	public static ConnectES instance(){
		if(instance == null)
			synchronized (ConnectES.class) {
	            if (instance == null) {        // Double checked
	            	instance = new ConnectES();
	            }
	        }
		return instance;
	}
	
	public Client getClient(){
		return this.client;
	}

}
