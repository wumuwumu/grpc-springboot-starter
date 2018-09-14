package cn.sciento.gprc.autoconfigure.client;

import io.grpc.Channel;
import io.grpc.ClientInterceptor;

import javax.net.ssl.SSLException;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * User: Michael
 * Email: yidongnan@gmail.com
 * Date: 5/17/16
 */
public interface GrpcChannelFactory {

    Channel createChannel(String name) throws FileNotFoundException, SSLException;

    Channel createChannel(String name, List<ClientInterceptor> interceptors) throws FileNotFoundException, SSLException;
}
