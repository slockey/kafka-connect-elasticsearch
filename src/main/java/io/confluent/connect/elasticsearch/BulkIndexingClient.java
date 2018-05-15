/**
 * Copyright 2016 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 **/

package io.confluent.connect.elasticsearch;

import io.confluent.connect.elasticsearch.bulk.BulkClient;
import io.confluent.connect.elasticsearch.bulk.BulkRequest;
import io.confluent.connect.elasticsearch.bulk.BulkResponse;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkIndexingClient implements BulkClient<IndexableRecord, BulkRequest> {
  private static final Logger log = LoggerFactory.getLogger(BulkIndexingClient.class);

  private final ElasticsearchClient client;

  public BulkIndexingClient(ElasticsearchClient client) {
    this.client = client;
  }

  @Override
  public BulkRequest bulkRequest(List<IndexableRecord> batch) {
    return client.createBulkRequest(batch);
  }

  @Override
  public BulkResponse execute(BulkRequest bulk, List<IndexableRecord> batch) throws IOException {
    BulkResponse result = client.executeBulk(bulk);
    if (result.isSucceeded()) {
      for (int i = 0; i < batch.size(); i++) {
        IndexableRecord r = (IndexableRecord) batch.get(0);
        log.info("Written into ES: " + r.key);
      }
      return result;
    }
    return BulkResponse.failure(false, "unknown error");
  }


}
