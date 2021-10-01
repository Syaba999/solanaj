package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

public class SolanaVersion {

    public static class Result {
        @Json(name = "solana-core")
        private String solanaCore;

        public String getSolanaCore() {
            return solanaCore;
        }

        public void setSolanaCore(String solanaCore) {
            this.solanaCore = solanaCore;
        }
    }

    @Json(name = "result")
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
