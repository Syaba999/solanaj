package org.p2p.solanaj.rpc.types;

import com.squareup.moshi.Json;

import java.util.List;

public class TokenAccount extends RpcResultObject {
    public static class Value {
        @Json(name = "data")
        private Data data;
        @Json(name = "executable")
        private boolean executable;
        @Json(name = "lamports")
        private long lamports;
        @Json(name = "owner")
        private String owner;
        @Json(name = "rentEpoch")
        private long rentEpoch;

        public Data getData() {
            return data;
        }

        public boolean isExecutable() {
            return executable;
        }

        public long getLamports() {
            return lamports;
        }

        public String getOwner() {
            return owner;
        }

        public long getRentEpoch() {
            return rentEpoch;
        }

    }

    public static class Data {

        public static class Parsed {
            public static class Info {
                public static class Amount {
                    @Json(name = "amount")
                    private String amount;
                    @Json(name = "decimals")
                    private int decimals;
                    @Json(name = "uiAmount")
                    private double uiAmount;
                    @Json(name = "uiAmountString")
                    private String uiAmountString;

                    public String getAmount() {
                        return amount;
                    }

                    public int getDecimals() {
                        return decimals;
                    }

                    public double getUiAmount() {
                        return uiAmount;
                    }

                    public String getUiAmountString() {
                        return uiAmountString;
                    }
                }

                @Json(name = "tokenAmount")
                private Amount tokenAmount;
                @Json(name = "delegate")
                private String delegate;
                @Json(name = "delegatedAmount")
                private Amount delegatedAmount;
                @Json(name = "state")
                private String state;
                @Json(name = "isNative")
                private boolean isNative;
                @Json(name = "mint")
                private String mint;
                @Json(name = "owner")
                private String owner;

                public Amount getTokenAmount() {
                    return tokenAmount;
                }

                public String getDelegate() {
                    return delegate;
                }

                public Amount getDelegatedAmount() {
                    return delegatedAmount;
                }

                public String getState() {
                    return state;
                }

                public boolean isNative() {
                    return isNative;
                }

                public String getMint() {
                    return mint;
                }

                public String getOwner() {
                    return owner;
                }
            }
            @Json(name = "accountType")
            private String accountType;
            @Json(name = "info")
            private Info info;
            @Json(name = "type")
            private String type;

            public String getAccountType() {
                return accountType;
            }

            public Info getInfo() {
                return info;
            }

            public String getType() {
                return type;
            }
        }

        @Json(name = "program")
        private String program;
        @Json(name = "parsed")
        private Parsed parsed;
        @Json(name = "space")
        private long space;

        public String getProgram() {
            return program;
        }

        public Parsed getParsed() {
            return parsed;
        }

        public long getSpace() {
            return space;
        }
    }

    @Json(name = "value")
    private List<Value> value;

    public List<Value> getValue() {
        return value;
    }
}
