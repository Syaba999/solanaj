package org.p2p.solanaj.core;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.p2p.solanaj.programs.SystemProgram;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;
import org.p2p.solanaj.rpc.types.RpcNotificationResult;

public class TransactionTest {

    @Test
    public void signAndSerialize() {
        PublicKey fromPublicKey = new PublicKey("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo");
        PublicKey toPublickKey = new PublicKey("GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5");
        int lamports = 3000;

        Account signer = new Account(Base58
                .decode("4Z7cXSyeFR8wNGMVXUE1TwtKn5D5Vu7FzEv69dokLv7KrQk7h6pu4LF8ZRR9yQBhc7uSM6RTTZtU1fmaxiNrxXrs"));

        Transaction transaction = new Transaction();
        transaction.addInstruction(SystemProgram.transfer(fromPublicKey, toPublickKey, lamports));
        transaction.setRecentBlockHash("Eit7RCyhUixAe2hGBS8oqnw59QK3kgMMjfLME5bm9wRn");
        transaction.sign(signer);
        byte[] serializedTransaction = transaction.serialize();

        assertEquals(
                "ASdDdWBaKXVRA+6flVFiZokic9gK0+r1JWgwGg/GJAkLSreYrGF4rbTCXNJvyut6K6hupJtm72GztLbWNmRF1Q4BAAEDBhrZ0FOHFUhTft4+JhhJo9+3/QL6vHWyI8jkatuFPQzrerzQ2HXrwm2hsYGjM5s+8qMWlbt6vbxngnO8rc3lqgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAy+KIwZmU8DLmYglP3bPzrlpDaKkGu6VIJJwTOYQmRfUBAgIAAQwCAAAAuAsAAAAAAAA=",
                Base64.getEncoder().encodeToString(serializedTransaction));
    }

    @Test
    public void marshallUnmarshallTransaction() throws IOException {
        File resource = new File(this.getClass().getClassLoader().getResource("transaction.json").getFile());
        String transactionJson = Files.readString(resource.toPath());

        JsonAdapter<ConfirmedTransaction> adapter = new Moshi.Builder().build().adapter(ConfirmedTransaction.class);
        ConfirmedTransaction transaction = adapter.fromJson(transactionJson);

        ConfirmedTransaction.TokenBalance tokenBalance = transaction.getMeta().getPostTokenBalances().get(0);
        assertEquals(tokenBalance.getMint(), "EPjFWdd5AufqSSqeM2qN1xzybapC8G4wEGGkZwyTDt1v");
        assertEquals(tokenBalance.getUiTokenAmount().getAmount(), "280000");
        assertEquals(tokenBalance.getUiTokenAmount().getDecimals(), Integer.valueOf(6));
        assertEquals(tokenBalance.getUiTokenAmount().getUiAmountString(), "0.28");
        assertEquals(tokenBalance.getUiTokenAmount().getUiAmount(), Float.valueOf(0.28f));
    }

    @Test
    public void marshallUnmarshallSignatureNotification() throws IOException {
        File resource = new File(this.getClass().getClassLoader().getResource("signatureNotification.json").getFile());
        String signatureNtfJson = Files.readString(resource.toPath());
        JsonAdapter<RpcNotificationResult> adapter = new Moshi.Builder().build().adapter(RpcNotificationResult.class);
        RpcNotificationResult notification = adapter.fromJson(signatureNtfJson);

        assertEquals("2.0", notification.getJsonrpc());
        assertEquals("signatureNotification", notification.getMethod());
        assertEquals(24006L, notification.getParams().getSubscription());

        RpcNotificationResult.Result result = notification.getParams().getResult();
        assertNull(((Map) result.getValue()).get("err"));
        assertEquals(5207624, result.gContext().getSlot());
    }
}
