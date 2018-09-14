package cn.sciento.gprc.autoconfigure.client;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 5/17/16
 */
@Data
@ConfigurationProperties("grpc")
public class GrpcChannelsProperties {

    @NestedConfigurationProperty
    private Map<String, GrpcChannelProperties> client = Maps.newHashMap();

    public GrpcChannelProperties getChannel(String name) {
        GrpcChannelProperties grpcChannelProperties = client.get(name);
        if (grpcChannelProperties == null) {
            grpcChannelProperties = GrpcChannelProperties.DEFAULT;
        }
        return grpcChannelProperties;
    }

    public GrpcChannelProperties getChannelScheme(String scheme) {
        for(GrpcChannelProperties channelProperties : client.values()){
            if(channelProperties.getScheme().equals(scheme)){
                return channelProperties;
            }
        }
        return null;
    }
}
