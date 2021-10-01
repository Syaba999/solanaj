package org.p2p.solanaj.rpc;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.p2p.solanaj.rpc.types.RpcRequest;
import org.p2p.solanaj.rpc.types.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcClient {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Logger LOG = LoggerFactory.getLogger(RpcClient.class.getName());

    private String endpoint;
    private OkHttpClient httpClient;
    private RpcApi rpcApi;

    public RpcClient(Cluster endpoint) {
        this(endpoint.getEndpoint());
    }

    public RpcClient(String endpoint) {
        this.endpoint = endpoint;
        rpcApi = new RpcApi(this);
        LoggingInterceptor logging = new LoggingInterceptor();
        httpClient = (new OkHttpClient.Builder()).addInterceptor(logging).build();
    }

    public <T> T call(String method, List<Object> params, Class<T> clazz) throws RpcException {
        RpcRequest rpcRequest = new RpcRequest(method, params);

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<RpcRequest> rpcRequestJsonAdapter = moshi.adapter(RpcRequest.class);
        JsonAdapter<RpcResponse<T>> resultAdapter = new Moshi.Builder().build()
                .adapter(Types.newParameterizedType(RpcResponse.class, Type.class.cast(clazz)));

        String body = rpcRequestJsonAdapter.toJson(rpcRequest);
        LOG.debug(String.format("METHOD: %s, BODY: %s", method, body));
        Request request = new Request.Builder().url(endpoint)
                .post(RequestBody.create(body, JSON)).build();

        try {
            Response response = httpClient.newCall(request).execute();
            String responseBody = response.body().string();
            LOG.debug(String.format("RESULT: %s", responseBody));
            RpcResponse<T> rpcResult = resultAdapter.fromJson(responseBody);
            if (rpcResult.getError() != null) {
                throw new RpcException(rpcResult.getError().getMessage());
            }

            return (T) rpcResult.getResult();
        } catch (IOException e) {
            throw new RpcException(e.getMessage());
        }
    }

    public RpcApi getApi() {
        return rpcApi;
    }

    public String getEndpoint() {
        return endpoint;
    }

    static class LoggingInterceptor implements Interceptor {
        @Override public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            LOG.debug(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            LOG.debug(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }
}
