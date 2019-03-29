package com.hedera.sdk;

import com.hedera.sdk.proto.FileServiceGrpc;
import com.hedera.sdk.proto.Transaction;
import com.hedera.sdk.proto.TransactionResponse;
import io.grpc.MethodDescriptor;

public final class AdminUndeleteTransaction extends TransactionBuilder<AdminUndeleteTransaction> {
    public AdminUndeleteTransaction setID(FileId fileId) {
        inner.getBodyBuilder().getAdminUndeleteBuilder().setFileID(fileId.inner);
        return this;
    }

    public AdminUndeleteTransaction setID(ContractId contractId) {
        inner.getBodyBuilder().getAdminUndeleteBuilder().setContractID(contractId.inner);
        return this;
    }

    @Override
    MethodDescriptor<Transaction, TransactionResponse> getMethod() {
        return FileServiceGrpc.getAdminUndeleteMethod();
    }
}