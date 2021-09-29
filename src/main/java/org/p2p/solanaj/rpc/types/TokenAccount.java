package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;
import org.bouncycastle.util.encoders.Base64;

import java.util.List;

public class TokenAccount extends RpcResultObject {
    public static class Value{
        public Account account;
        public String pubkey;

        public static class Account{
            public Data data;
            public boolean executable;
            public int lamports;
            public String owner;
            public int rentEpoch;

            public static class Data{
                public Parsed parsed;
                public String program;
                public int space;

                public static class Parsed{
                    public Info info;
                    public String type;

                    public static class Info{
                        public boolean isNative;
                        public String mint;
                        public String owner;
                        public String state;
                        public TokenAmount tokenAmount;

                        public static class TokenAmount{
                            public String amount;
                            public int decimals;
                            public double uiAmount;
                            public String uiAmountString;
                        }
                    }
                }
            }
        }
    }


    @Json(name = "value")
    private List<Value> value;

    public List<Value> getValue() {
        return value;
    }
}


