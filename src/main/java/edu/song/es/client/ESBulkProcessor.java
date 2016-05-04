package edu.song.es.client;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
/** provide a default BulkProcessor.
 * @author stab  2015年 04月 22日 星期三
 */
public class ESBulkProcessor {
	private Client client = ConnectES.instance().getClient();
	private BulkProcessor esBulkProcessor = null;
	private ByteSizeValue bulkSize = new ByteSizeValue(5,ByteSizeUnit.MB);
	private TimeValue bulkTime = TimeValue.timeValueSeconds(1);
	private int correntRequest = 1;
	private int bulkQuantity = 10000;
	
	public BulkProcessor getEsBulkProcessor() {
		if(esBulkProcessor == null)
			ESBulkProcessorMaker();
		return esBulkProcessor;
	}
	
	private void ESBulkProcessorMaker(){
		esBulkProcessor = BulkProcessor.builder(
		        client,  
		        new BulkProcessor.Listener() {
		            public void beforeBulk(long executionId,BulkRequest request) { 
		            	//System.out.println(TimeParser.timestampToDate(System.currentTimeMillis()));
		            	//System.out.println(request.numberOfActions() + " " +
		            	//request.contextSize()); 
		            	} 

		            public void afterBulk(long executionId,BulkRequest request,BulkResponse response) { 
		            	//System.out.println(response.contextSize() + " " + response.getTookInMillis());
		            	//System.out.println(TimeParser.timestampToDate(System.currentTimeMillis()));
		            	//response.hasFailures(); 
		            	} 

		            public void afterBulk(long executionId,BulkRequest request,Throwable failure) { 
		            	//System.out.println(TimeParser.timestampToDate(System.currentTimeMillis()));
		            	failure.printStackTrace(); } 
		            
		        })
		        .setBulkActions(bulkQuantity) 
		        .setBulkSize(bulkSize) 
		        .setFlushInterval(bulkTime) 
		        .setConcurrentRequests(correntRequest) 
		        .build();
	}
}
