package com.caiya.ck.common.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 轻量级封装的Canal Client.
 *
 * @author wangnan
 * @since 1.0
 */
public class CanalClient implements Closeable {

    private CanalConnector connector;

    public CanalClient(String hostName, int port, String destination, String username, String password) {
        connector = CanalConnectors.newSingleConnector(new InetSocketAddress(hostName, port), destination, username, password);
    }

    public CanalClient(Map<String, Integer> hostAndPorts, String destination, String username, String password) {
        List<InetSocketAddress> addresses = new ArrayList<>();
        hostAndPorts.forEach((host, port) -> {
            addresses.add(new InetSocketAddress(host, port));
        });
        connector = CanalConnectors.newClusterConnector(addresses, destination, username, password);
    }

    public CanalClient(String zkServers, String destination, String username, String password) {
        connector = CanalConnectors.newClusterConnector(zkServers, destination, username, password);
    }

    /**
     * 获取已连接的,无filter(或者filter由服务端指定)的Connector
     * 注意手动关闭: {@link CanalConnector#disconnect()}
     *
     * @return {@link CanalConnector}
     */
    public CanalConnector getConnectedAndSubscribedConnector() {
        return getConnectedAndSubscribedConnector("");
    }

    /**
     * 获取已连接的,订阅指定filter的Connector
     * 注意手动关闭: {@link CanalConnector#disconnect()}
     *
     * @param filter filter
     * @return {@link CanalConnector}
     */
    public CanalConnector getConnectedAndSubscribedConnector(String filter) {
        connector.connect();
        connector.subscribe(filter);
        return connector;
    }

    @Override
    public void close() {
        close(this.connector);
    }

    private static void close(CanalConnector connector) {
        if (connector != null)
            connector.disconnect();
    }

}
