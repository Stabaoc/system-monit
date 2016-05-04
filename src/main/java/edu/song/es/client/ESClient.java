package edu.song.es.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import edu.song.linuxmonit.util.TimeUtil;

public class ESClient {
	public static final Logger LOG = LoggerFactory.getLogger(ESClient.class);
	
	private static Client client = ConnectES.instance().getClient();
    private static BulkProcessor esBulkProcessor = new ESBulkProcessor().getEsBulkProcessor();
    private static String timestamp = "2016-05-04";
    
    public static final String TYPE_JAVA = "JavaAppData";
    public static final String TYPE_RELATIONSHIP = "RelationShip";
    public static final String TYPE_RELATIONSHIPMONIT = "RelationShipMonit";
    public static final String TYPE_DB = "DBType";
    public static final String TYPE_RESPONSE = "Response";
    public static final String TYPE_BASELINE = "BaseLine";
    public static final String TYPE_ARIMAMODEL = "ArimaModel";
    public static final String TYPE_SLOWMETHOD = "SlowMethod";
    public static final String TYPE_MONITMETHOD = "MonitMethod";
   
    static{
    	init();
    	process();
    }
    public static void init(){
    	timestamp = TimeUtil.timestampToDay(System.currentTimeMillis());
    }
	private static class hourTimer extends TimerTask {
		@Override
		public void run() {
			timestamp = TimeUtil.timestampToDay(System.currentTimeMillis());
			LOG.info(timestamp);
		}
	}
    public static void process() {
		
		Timer hourTimer = new Timer();
		Calendar hourCalendar = Calendar.getInstance();
		// *** 定制整点执行方法 ***//
		hourCalendar.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY+1);
		hourCalendar.set(Calendar.MINUTE, 0);
		hourCalendar.set(Calendar.SECOND, 0);

		Date dateHour = hourCalendar.getTime(); // 第一次执行定时任务的时间
		hourTimer.schedule(new hourTimer(), dateHour, 60 * 60 * 1000);
	}
    /**
     * index a doc into apm related index and type with a readFlag.Actually the TYPE_JAVA and TYPE_RELATIONSHIPMONIT.
     * 把数据索引到APM相关的索引中去。
     * @param ip
     * @param type
     * @param jsonLog
     */
    public static void uploadApmData(String ip, String type, Map<String, Object> jsonLog){
        jsonLog.put("readFlag", 0);
        index(ip, type, jsonLog);
    }
    public static void uploadApmData(String ip, String type, String jsonLog){
    	if (TYPE_JAVA.equals(type) || TYPE_RELATIONSHIPMONIT.equals(type) ) {
    		String readFlagStr = "{ \"readFlag\": 0 ,";
    		jsonLog = readFlagStr + jsonLog.substring(1);
    	}
    	index(ip, type, jsonLog);
    }
    
    public static void index(Map<String, Object> jsonLog){
        IndexRequest indexRequest = new IndexRequest(ESConfig.apmindex + timestamp, ESConfig.type).source(jsonLog);
        esBulkProcessor.add(indexRequest); 
    }
    public static void index(String type,Map<String, Object> jsonLog){
        IndexRequest indexRequest = new IndexRequest(ESConfig.apmindex + timestamp, type).source(jsonLog);
        esBulkProcessor.add(indexRequest); 
    }
    public static void index(String ip, String type,Map<String, Object> jsonLog){
        IndexRequest indexRequest = new IndexRequest(ESConfig.apmindex + ip, type).source(jsonLog);
        esBulkProcessor.add(indexRequest); 
    }
    public static void index(String ip, String type,String jsonLog){
        IndexRequest indexRequest = new IndexRequest(ESConfig.apmindex + ip, type).source(jsonLog);
        esBulkProcessor.add(indexRequest); 
    }
    public static void index(String index, String type, String id, Map<String, Object> jsonLog){
    	IndexRequest indexRequest = new IndexRequest(index, type, id).source(jsonLog);
    	client.index(indexRequest).actionGet();
    }
    public static void indexWithId(String ip, String type,String id ,Map<String, Object> jsonLog){
    	IndexRequest indexRequest = new IndexRequest(ESConfig.apmindex + ip, type, id).source(jsonLog);
    	esBulkProcessor.add(indexRequest); 
    }
    /**
     *  a simple update , can update only one field. If you need to update multi field ,please use upsert.
     */
    public static void update(String	ip ,String type ,String id ,String key ,Object value){
    	UpdateRequest upsert = new UpdateRequest(ESConfig.apmindex + ip, type, id).doc(key, value);
    	esBulkProcessor.add(upsert);
    }
    /**
     * update a designated doc.If the doc is exist, then update,else index.
     * @param updateDoc the map be used to update
     * @param defDoc the map be used to index 
     */
    public static void upsert(String ip,String type ,String id , Map<String, Object> updateDoc ,Map<String, Object> defDoc){
    	UpdateRequest upsert = new UpdateRequest(ESConfig.apmindex + ip, type, id).doc(updateDoc).upsert(defDoc);
   		esBulkProcessor.add(upsert);
    }
    public static void upsert(String ip,String type ,String id , String updateDoc ,String defDoc){
    	UpdateRequest upsert = new UpdateRequest(ESConfig.apmindex + ip, type, id).doc(updateDoc).upsert(defDoc);
   		esBulkProcessor.add(upsert);
    }
    public static void upsert(String ip,String type ,String id , List<?> updateDoc ,List<?> defDoc){
    	UpdateRequest upsert = new UpdateRequest(ESConfig.apmindex + ip, type, id).doc(updateDoc).upsert(defDoc);
   		esBulkProcessor.add(upsert);
    }
     /**
      * @param ip the user's ip
      * @param type the type of index
      * @param id the doc id
      * @return true for exist,false for null
      */
    public static boolean isExistInEs(String ip , String type ,String id){
    	GetRequest get = new GetRequest(ESConfig.apmindex + ip, type, id);
    	GetResponse getResponse = ConnectES.instance().getClient().get(get).actionGet();
    	return getResponse.isExists();
    }
    public static boolean isExist(String index , String type ,String id){
    	GetRequest get = new GetRequest(index, type, id);
    	GetResponse getResponse = ConnectES.instance().getClient().get(get).actionGet();
    	return getResponse.isExists();
    }
    /**
     * get the designated doc
     */
    public static Map<String, Object> getDoc(String ip, String type,String id){
    	return client.prepareGet(ESConfig.apmindex + ip, type, id).get().getSource();
    }
    public static Map<String, Object> doc(String index, String type,String id){
    	return client.prepareGet(index, type, id).get().getSource();
    }
    
    public static List<String> getAllIndices(){
    	List<String> indices = new ArrayList<String>();
		SearchResponse searchResponse = client.prepareSearch(ESConfig.apmindex + "*").setSize(0)
				.addAggregation(AggregationBuilders.terms("indexTerm").field("_index").size(0)).execute().actionGet();
		if (searchResponse.getHits().getTotalHits() > 0) {
			Terms indexTerms = searchResponse.getAggregations().get("indexTerm");
			for (Bucket bucket : indexTerms.getBuckets()) {
					indices.add(bucket.getKey());
			}
		}
		return indices;
    }
   
    public static Map<String,List<String>> getServiceIdWithUrl(String index,String type){
    	Map<String,List<String>> serviceIdWithUrl = new HashMap<String, List<String>>();
    	
        TermsBuilder serviceIdAgg = AggregationBuilders.terms("serviceId").field("DefaultMessage.serviceId").size(0)
                .subAggregation(AggregationBuilders.terms("url").field("DefaultMessage.url"));
        SearchResponse searchResponse = client.prepareSearch(index).setTypes(type)
                .setSize(0)
                .addAggregation(serviceIdAgg)
                .execute().actionGet();
        Terms serviceIdTerms = searchResponse.getAggregations().get("serviceId");
        for (Bucket serviceIdBucket : serviceIdTerms.getBuckets()) {
        	String serviceId = serviceIdBucket.getKey();
        	if(null == serviceIdWithUrl.get(serviceId)){
        		serviceIdWithUrl.put(serviceId, new ArrayList<String>());
        	}
        	List<String> urlList = serviceIdWithUrl.get(serviceId);
        	Terms urlTerms = serviceIdBucket.getAggregations().get("url");
        	for(Bucket urlBucket : urlTerms.getBuckets()){
        		urlList.add(urlBucket.getKey());
        	}
        }
    	return serviceIdWithUrl;
    }
    
    /**
     * @param index
     * @param type
     * @return the serviceId's urls
     */
    public static List<String> getServiceIdsUrl(String index,String type,String serviceId){
    	List<String> urls = new ArrayList<String>();
    	TermFilterBuilder serviceIdFilter = FilterBuilders.termFilter("DefaultMessage.serviceId", serviceId);
    	QueryBuilder query = QueryBuilders.filteredQuery(null, serviceIdFilter);
        TermsBuilder urlAgg = AggregationBuilders.terms("url").field("DefaultMessage.url").size(0);
        SearchResponse searchResponse = client.prepareSearch(index).setTypes(type)
                .setSize(0)
                .setQuery(query)
                .addAggregation(urlAgg)
                .execute().actionGet();
 
        Terms urlTerms = searchResponse.getAggregations().get("url");
        for(Bucket urlBucket : urlTerms.getBuckets()){
        	urls.add(urlBucket.getKey());
        }
    	return urls;
    }
    
    
    public static void main(String[] args) {
    	List<String> list = new ArrayList<String>();
    	list.add("10.10.105.79:8081");
    	list.add("10.10.105.112:8081");
    	QueryBuilder query = QueryBuilders.filteredQuery(null, FilterBuilders.termsFilter("dst", list));
    	SearchResponse searchResponse = client.prepareSearch("apm2.0_local_183.129.190.82").setTypes("RelationShip").setQuery(query)
    			.setSize(1000).execute().actionGet();
    	System.out.println(searchResponse);
    	/*
    	try {
			Thread.sleep(11000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	System.out.println("finish");
    }
}