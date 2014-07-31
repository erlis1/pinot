package com.linkedin.pinot.core.query.planner;

import java.util.List;

import com.linkedin.pinot.common.query.request.Query;
import com.linkedin.pinot.core.indexsegment.IndexSegment;


/**
 * QueryPlanner interface will provide different strategy to plan on how to process segments.
 *
 */
public interface QueryPlanner {
  public QueryPlan computeQueryPlan(Query query, List<IndexSegment> indexSegmentList);
}
