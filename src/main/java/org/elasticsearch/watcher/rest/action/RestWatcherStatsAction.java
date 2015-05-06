/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.watcher.rest.action;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;
import org.elasticsearch.rest.action.support.RestBuilderListener;
import org.elasticsearch.watcher.client.WatcherClient;
import org.elasticsearch.watcher.rest.WatcherRestHandler;
import org.elasticsearch.watcher.transport.actions.stats.WatcherStatsResponse;
import org.elasticsearch.watcher.transport.actions.stats.WatcherStatsRequest;

import java.util.Locale;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;

public class RestWatcherStatsAction extends WatcherRestHandler {

    @Inject
    protected RestWatcherStatsAction(Settings settings, RestController controller, Client client) {
        super(settings, controller, client);
        controller.registerHandler(GET, URI_BASE + "/stats", this);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel restChannel, WatcherClient client) throws Exception {
        client.watcherStats(new WatcherStatsRequest(), new RestBuilderListener<WatcherStatsResponse>(restChannel) {
            @Override
            public RestResponse buildResponse(WatcherStatsResponse watcherStatsResponse, XContentBuilder builder) throws Exception {
                builder.startObject();
                builder.field("watcher_state", watcherStatsResponse.getWatcherState().toString().toLowerCase(Locale.ROOT))
                        .field("watch_count", watcherStatsResponse.getWatchesCount());

                builder.startObject("execution_queue")
                        .field("size", watcherStatsResponse.getExecutionQueueSize())
                        .field("max_size", watcherStatsResponse.getWatchExecutionQueueMaxSize())
                        .endObject();
                builder.endObject();
                return new BytesRestResponse(OK, builder);

            }
        });
    }
}
