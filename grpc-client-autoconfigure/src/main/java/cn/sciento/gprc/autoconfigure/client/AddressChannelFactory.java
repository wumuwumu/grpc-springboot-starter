package cn.sciento.gprc.autoconfigure.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.grpc.*;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 5/17/16
 */
public class AddressChannelFactory implements GrpcChannelFactory {
    private final GrpcChannelsProperties properties;
    private final LoadBalancer.Factory loadBalancerFactory;
    private final NameResolver.Factory nameResolverFactory;
    private final GlobalClientInterceptorRegistry globalClientInterceptorRegistry;

    public AddressChannelFactory(GrpcChannelsProperties properties, LoadBalancer.Factory loadBalancerFactory, GlobalClientInterceptorRegistry globalClientInterceptorRegistry) {
        this.properties = properties;
        this.loadBalancerFactory = loadBalancerFactory;
        this.nameResolverFactory = new AddressChannelResolverFactory(properties);
        this.globalClientInterceptorRegistry = globalClientInterceptorRegistry;
    }

    @Override
    public Channel createChannel(String name) throws FileNotFoundException, SSLException {
        return this.createChannel(name, null);
    }

    @Override
    public Channel createChannel(String name, List<ClientInterceptor> interceptors) throws FileNotFoundException, SSLException {
        GrpcChannelProperties channelProperties = properties.getChannel(name);
        NettyChannelBuilder builder = NettyChannelBuilder.forTarget(name)
                .loadBalancerFactory(loadBalancerFactory)
                .nameResolverFactory(nameResolverFactory);
        if (properties.getChannel(name).isPlaintext()) {
            builder.usePlaintext();
        }
        if (channelProperties.isEnableKeepAlive()) {
            builder.keepAliveWithoutCalls(channelProperties.isKeepAliveWithoutCalls())
                    .keepAliveTime(channelProperties.getKeepAliveTime(), TimeUnit.SECONDS)
                    .keepAliveTimeout(channelProperties.getKeepAliveTimeout(), TimeUnit.SECONDS);
        }
        if(channelProperties.getMaxInboundMessageSize() > 0) {
        	builder.maxInboundMessageSize(channelProperties.getMaxInboundMessageSize());
        }
        if(channelProperties.getTrustCertCollectionPath() != null || channelProperties.getTrustCertCollectionPath().length() >0){
            File file= ResourceUtils.getFile(channelProperties.getTrustCertCollectionPath());
            builder.sslContext(GrpcSslContexts.forClient().trustManager(file).build());
        }
        Channel channel = builder.build();

        List<ClientInterceptor> globalInterceptorList = globalClientInterceptorRegistry.getClientInterceptors();
        Set<ClientInterceptor> interceptorSet = Sets.newHashSet();
        if (globalInterceptorList != null && !globalInterceptorList.isEmpty()) {
            interceptorSet.addAll(globalInterceptorList);
        }
        if (interceptors != null && !interceptors.isEmpty()) {
            interceptorSet.addAll(interceptors);
        }
        return ClientInterceptors.intercept(channel, Lists.newArrayList(interceptorSet));
    }
}
