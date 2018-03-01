package sg.ncl.webssh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.inject.Inject;

/**
 * References:
 * [1] http://www.sergialmar.com/2014/03/detect-websocket-connects-and-disconnects-in-spring-4/
 * [2] http://www.devglan.com/spring-boot/spring-session-stomp-websocket
 * [3] https://stackoverflow.com/questions/33977803/origin-header-value-not-allowed-even-though-url-is-allowed
 * [4] https://stackoverflow.com/questions/27526281/websockets-and-apache-proxy-how-to-configure-mod-proxy-wstunnel
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Bean
    public SocketChannelInterceptor socketChannelInterceptor() {
        return new SocketChannelInterceptor();
    }

    @Inject
    private WebSocketProperties webSocketProperties;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(socketChannelInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/shell");
        config.setApplicationDestinationPrefixes("/webssh");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String url = webSocketProperties.getHttpMode() + webSocketProperties.getDomain();
        registry.addEndpoint("/websocket").setAllowedOrigins(url).withSockJS();
    }
}
