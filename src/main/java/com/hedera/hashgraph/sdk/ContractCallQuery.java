package com.hedera.hashgraph.sdk;

import com.hedera.hashgraph.sdk.proto.Query;
import com.hedera.hashgraph.sdk.proto.QueryHeader;
import com.hedera.hashgraph.sdk.proto.Response;
import com.hedera.hashgraph.sdk.proto.ResponseHeader;
import io.grpc.MethodDescriptor;

// TODO: ContractFunctionResult
public final class ContractCallQuery extends QueryBuilder<Void, ContractCallQuery> {
    @Override
    protected void onMakeRequest(Query.Builder queryBuilder, QueryHeader header) {
    }

    @Override
    protected ResponseHeader mapResponseHeader(Response response) {
        return response.getContractCallLocal().getHeader();
    }

    @Override
    protected QueryHeader mapRequestHeader(Query request) {
        return request.getContractCallLocal().getHeader();
    }

    @Override
    protected Void mapResponse(Response response) {
        return null;
    }

    @Override
    protected MethodDescriptor<Query, Response> getMethodDescriptor() {
        return null;
    }
}