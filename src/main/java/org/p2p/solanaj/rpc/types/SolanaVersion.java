package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class SolanaVersion {

    @Json(name = "solana-core")
    private String solanaCore;

    public String getSolanaCore() {
        return solanaCore;
    }

    public void setSolanaCore(String solanaCore) {
        this.solanaCore = solanaCore;
    }
}
