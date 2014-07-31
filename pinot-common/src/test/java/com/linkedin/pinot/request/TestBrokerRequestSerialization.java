package com.linkedin.pinot.request;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.junit.Test;

import com.linkedin.pinot.common.metrics.MetricsHelper;
import com.linkedin.pinot.common.metrics.MetricsHelper.TimerContext;
import com.linkedin.pinot.common.request.AggregationInfo;
import com.linkedin.pinot.common.request.BrokerRequest;
import com.linkedin.pinot.common.request.FilterOperator;
import com.linkedin.pinot.common.request.FilterQuery;
import com.linkedin.pinot.common.request.FilterQueryMap;
import com.linkedin.pinot.common.request.GroupBy;
import com.linkedin.pinot.common.request.QuerySource;
import com.linkedin.pinot.common.request.QueryType;
import com.linkedin.pinot.common.request.Selection;
import com.linkedin.pinot.common.request.SelectionSort;

public class TestBrokerRequestSerialization extends TestCase {

  @Test
  public static void testSerialization()
  {
    BrokerRequest req = new BrokerRequest();

    // Populate Query Type
    QueryType type = new QueryType();
    type.setHasAggregation(true);
    type.setHasFilter(true);
    type.setHasSelection(true);
    type.setHasGroup_by(true);
    req.setQueryType(type);

    // Populate Query source
    QuerySource s = new QuerySource();
    s.setResourceName("dummy");
    s.setTableName("dummy");
    req.setQuerySource(s);

    req.setDuration("dummy");
    req.setTimeInterval("dummy");

    //Populate Group-By
    GroupBy groupBy = new GroupBy();
    List<String> columns = new ArrayList<String>();
    columns.add("dummy1");
    columns.add("dummy2");
    groupBy.setColumns(columns);
    groupBy.setTopN(100);
    req.setGroupBy(groupBy);

    //Populate Selections
    Selection sel = new Selection();
    sel.setSize(1);
    SelectionSort s2 = new SelectionSort();
    s2.setColumn("dummy1");
    s2.setIsAsc(true);
    sel.addToSelectionSortSequence(s2);
    sel.addToSelectionColumns("dummy1");
    req.setSelections(sel);

    //Populate FilterQuery
    FilterQuery q1 = new FilterQuery();
    q1.setId(1);
    q1.setColumn("dummy1");
    q1.addToValue("dummy1");
    q1.addToNestedFilterQueryIds(2);
    q1.setOperator(FilterOperator.AND);
    FilterQuery q2 = new FilterQuery();
    q2.setId(2);
    q2.setColumn("dummy2");
    q2.addToValue("dummy2");
    q2.setOperator(FilterOperator.AND);

    FilterQueryMap map = new FilterQueryMap();
    map.putToFilterQueryMap(1, q1);
    map.putToFilterQueryMap(2, q2);
    req.setFilterQuery(q1);
    req.setFilterSubQueryMap(map);

    //Populate Aggregations
    AggregationInfo agg = new AggregationInfo();
    agg.setAggregationType("dummy1");
    agg.putToAggregationParams("key1", "dummy1");
    req.addToAggregationsInfo(agg);


    int numRequests = 100000;
    TimerContext t = MetricsHelper.startTimer();
    //TSerializer serializer = new TSerializer(new TCompactProtocol.Factory());
    TSerializer serializer = new TSerializer();
    //Compact : Size 183 , Serialization Latency : 0.03361ms
    // Normal : Size 385 , Serialization Latency : 0.01144ms

    for ( int i = 0 ; i < numRequests; i++)
    {
      try {
        serializer.serialize(req);
        //System.out.println(s3.length);
        //break;
      } catch (TException e) {
        e.printStackTrace();
      }
    }
    t.stop();
    System.out.println("Latency is :" + (t.getLatencyMs()/(float)numRequests));
  }

}
