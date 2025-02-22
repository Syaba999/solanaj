package org.p2p.solanaj.rpc;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.rpc.types.*;
import org.p2p.solanaj.rpc.types.ConfigObjects.*;
import org.p2p.solanaj.rpc.types.RpcResultTypes.ValueLong;
import org.p2p.solanaj.rpc.types.RpcSendTransactionConfig.Encoding;
import org.p2p.solanaj.ws.SubscriptionWebSocketClient;
import org.p2p.solanaj.ws.listeners.NotificationEventListener;

public class RpcApi {
    private RpcClient client;

    public RpcApi(RpcClient client) {
        this.client = client;
    }

    public String getRecentBlockhash() throws RpcException {
        return client.call("getRecentBlockhash", Collections.emptyList(), RecentBlockhash.class).getRecentBlockhash();
    }

    public String sendTransaction(Transaction transaction, Account ... signers) throws RpcException {
        List<Object> params = prepareTransactionParams(transaction, signers);

        return client.call("sendTransaction", params, String.class);
    }

    public String simulateTransaction(Transaction transaction, Account ... signers) throws RpcException {
        List<Object> params = prepareTransactionParams(transaction, signers);

        return client.call("simulateTransaction", params, String.class);
    }

    @NotNull
    private List<Object> prepareTransactionParams(Transaction transaction, Account[] signers) throws RpcException {
        String recentBlockhash = getRecentBlockhash();
        transaction.setRecentBlockHash(recentBlockhash);
        transaction.sign(Arrays.asList(signers));
        byte[] serializedTransaction = transaction.serialize();

        String base64Trx = Base64.getEncoder().encodeToString(serializedTransaction);

        List<Object> params = new ArrayList<Object>();

        params.add(base64Trx);
        params.add(new RpcSendTransactionConfig());
        return params;
    }

    public void sendAndConfirmTransaction(Transaction transaction,
                                          NotificationEventListener listener,
                                          Account ... signers
            ) throws RpcException {
        String signature = sendTransaction(transaction, signers);

        SubscriptionWebSocketClient subClient = SubscriptionWebSocketClient.getInstance(client.getEndpoint(), 0);
        subClient.signatureSubscribe(signature, listener);
    }

    public long getBalance(PublicKey account) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());

        return client.call("getBalance", params, ValueLong.class).getValue();
    }

    public ConfirmedTransaction getConfirmedTransaction(String signature, String commitment) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(signature);
        if (commitment != null)
            params.add(new Commitment(commitment));
        // TODO jsonParsed, base58, base64
        // the default encoding is JSON
        // params.add("json");

        return client.call("getConfirmedTransaction", params, ConfirmedTransaction.class);
    }

    public List<SignatureInformation> getConfirmedSignaturesForAddress2(PublicKey account, int limit) throws RpcException {
        return getConfirmedSignaturesForAddress2Inner(account, new ConfirmedSignFAddr2(limit));
    }

    public List<SignatureInformation> getConfirmedSignaturesForAddress2(PublicKey account, String commitment) throws RpcException {
        return getConfirmedSignaturesForAddress2Inner(account, new Commitment(commitment));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<SignatureInformation> getConfirmedSignaturesForAddress2Inner(PublicKey account, IParam param)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());
        params.add(param);

        List<AbstractMap> rawResult = client.call("getConfirmedSignaturesForAddress2", params, List.class);

        List<SignatureInformation> result = new ArrayList<SignatureInformation>();
        for (AbstractMap item : rawResult) {
            result.add(new SignatureInformation(item));
        }

        return result;
    }

    public List<ProgramAccount> getProgramAccounts(PublicKey account, long offset, String bytes) throws RpcException {
        List<Object> filters = new ArrayList<Object>();
        filters.add(new Filter(new Memcmp(offset, bytes)));

        ProgramAccountConfig programAccountConfig = new ProgramAccountConfig(filters);
        return getProgramAccounts(account, programAccountConfig);
    }

    public List<ProgramAccount> getProgramAccounts(PublicKey account) throws RpcException {
        return getProgramAccounts(account, new ProgramAccountConfig(Encoding.base64));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ProgramAccount> getProgramAccounts(PublicKey account, ProgramAccountConfig programAccountConfig)
            throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());

        if (programAccountConfig != null) {
            params.add(programAccountConfig);
        }

        List<AbstractMap> rawResult = client.call("getProgramAccounts", params, List.class);

        List<ProgramAccount> result = new ArrayList<ProgramAccount>();
        for (AbstractMap item : rawResult) {
            result.add(new ProgramAccount(item));
        }

        return result;
    }

    public AccountInfo getAccountInfo(PublicKey account) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(account.toString());
        params.add(new RpcSendTransactionConfig());

        return client.call("getAccountInfo", params, AccountInfo.class);
    }

    public TokenAccount getTokenAccountsByOwner(PublicKey owner, String mint) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(owner.toString());
        if (mint != null) {
            HashMap<String, String> mintParams = new HashMap<>();
            mintParams.put("mint", mint);
            params.add(mintParams);
        }
        HashMap<String,String> encodingParams = new HashMap<>();
        encodingParams.put("encoding", "jsonParsed");
        params.add(encodingParams);

        return client.call("getTokenAccountsByOwner", params, TokenAccount.class);
    }

    public long getMinimumBalanceForRentExemption(long dataLength) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(dataLength);

        return client.call("getMinimumBalanceForRentExemption", params, Long.class);
    }

    public long getBlockTime(long block) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(block);

        return client.call("getBlockTime", params, Long.class);
    }

    public String requestAirdrop(PublicKey address, long lamports) throws RpcException {
        List<Object> params = new ArrayList<Object>();

        params.add(address.toString());
        params.add(lamports);

        return client.call("requestAirdrop", params, String.class);
    }

    public SolanaVersion getVersion() throws RpcException {
        return client.call("getVersion", List.of(), SolanaVersion.class);
    }

    public long getTransactionCount() throws RpcException {
        return client.call("getTransactionCount", List.of(), Long.class);
    }

}
