package org.gov.elasticsearch;

//import com.sun.xml.internal.fastinfoset.util.StringArray;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.JSONObject;

//import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by govind on 7/24/16.
 */
public class ESClient {

    protected Client client;

    public ESClient() {

    }

    int init() {

        return 0;
    }



    public Client createClientSession() {
        if(client == null)
        {
            //Try starting search client at context loading
            try
            {
                Settings settings = Settings.settingsBuilder().put("cluster.name", "homeserver").build();

//                client = TransportClient.builder().build()
                client = TransportClient.builder().settings(settings).build()
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
            }
            catch(Exception ex)
            {
                //ignore any exception, dont want to stop context loading
                System.out.printf("Error occured while creating search client! %s", ex);
            }
        }
        return client;
    }

    public ArrayList<String> searchIndex( String indexName, JSONObject jsonQuery) {

        SearchResponse response = client.prepareSearch( indexName)
                .setQuery(QueryBuilders.termQuery("status", "unstaged"))
                .setFrom(0).setSize(500)
                .execute()
                .actionGet();

//        System.out.println("search response: " + response.toString());
//        System.out.println("search hits Total: " + response.getHits().totalHits());

        long totalHits = response.getHits().totalHits();
        SearchHit[] hits = response.getHits().getHits();

        long currentHits = hits.length;

        System.out.println("HITs: " + hits.toString());

        ArrayList<String> arrHits = new ArrayList<String>(20);

        for ( SearchHit hit : hits ) {
//            System.out.println("HIT: " + hit.getSourceAsString());
            arrHits.add(hit.getSourceAsString());

//            indexDocument("photos", hit.getSourceAsString());

        }



        System.out.println("search hits count: " + hits.getClass());

        return arrHits;
    }

    public int indexDocument( String index, JSONObject jsonObjectDocument) {
        System.out.println("indexDocument: " + index);

        IndexResponse response = client.prepareIndex(index, index, jsonObjectDocument.getString("filename"))
                .setSource(jsonObjectDocument.toString())
                .get();

        return 0;
    }

    public int indexBuilkDocuments( ArrayList<JSONObject> arrDocs) {

        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for(JSONObject jsonDocument: arrDocs) {

            System.out.println("indexBuilkDocuments: jsonDocument: " + jsonDocument.toString());
            bulkRequest.add(client.prepareIndex(
                    jsonDocument.getString("index"),
                    jsonDocument.getString("index"),
                    jsonDocument.getString("id"))
                    .setSource(jsonDocument.getJSONObject("data").toString())
            );

        }

        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
            System.out.println("Errors in bulk indexing");
        }

        return 0;
    }

    public int updateBuilkDocuments( String index, ArrayList<JSONObject> arrDocs) {

        BulkRequestBuilder bulkRequest = client.prepareBulk();

        for(JSONObject jsonDocument: arrDocs) {
            bulkRequest.add(client.prepareUpdate(index, index, jsonDocument.getString("filename"))
                    .setDoc((new JSONObject("{\"status\": \"staged\"}")).toString())
            );

        }

        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            // process failures by iterating through each bulk response item
            System.out.println("Errors in bulk indexing");
        }

        return 0;
    }

    public int getDocument(String indexName, String indexType, String id) {
        GetResponse response = client.prepareGet(indexType, indexType, id)
                                            .setOperationThreaded(false)
                                            .get();

        System.out.println("getDocument: " + response.toString());

        return 0;
    }
}
