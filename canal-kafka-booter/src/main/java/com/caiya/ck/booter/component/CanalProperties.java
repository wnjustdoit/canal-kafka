package com.caiya.ck.booter.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * canal配置类.
 *
 * @author wangnan
 * @since 1.0
 */
@Component
@ConfigurationProperties(prefix = "canal")
@Data
public class CanalProperties {

    private String zkServers;

    private String destination;

    private String username;

    private String password;


}
