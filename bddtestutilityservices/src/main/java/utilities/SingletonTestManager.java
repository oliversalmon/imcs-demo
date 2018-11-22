package utilities;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;

import java.util.Collections;

public class SingletonTestManager {

    private static HazelcastInstance hazelcastInstanceMember;
    private static CuratorFramework cli;
    private static TestingServer server;
    private static ConfigurableApplicationContext context;
    private static String url;
    private static TestRestTemplate template;

    public static void startUpServices(Class startUpClassName) throws Exception {
        SingletonTestManager.getZooServer();
        SingletonTestManager.getCurator();
        SingletonTestManager.getHz();
        SingletonTestManager.getApplicationContext(startUpClassName);
        SingletonTestManager.getRestTemplate();

    }

    public static HazelcastInstance getHz() {

        if (hazelcastInstanceMember == null) {
            hazelcastInstanceMember = Hazelcast.newHazelcastInstance();
            return hazelcastInstanceMember;
        } else
            return hazelcastInstanceMember;


    }

    public static TestRestTemplate getRestTemplate() {

        if (template == null) {
            template = new TestRestTemplate();
            template.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) -> {
                request.getHeaders()
                        .add("iv-user", "user");
                return execution.execute(request, body);
            }));
            return template;
        } else
            return template;

    }

    public static ConfigurableApplicationContext getApplicationContext(Class className) {
        if (context == null) {
            int zkPort = SocketUtils.findAvailableTcpPort();
            int port = SocketUtils.findAvailableTcpPort(zkPort + 1);

            context = new SpringApplicationBuilder(className).run(
                    "--server.port=" + port,
                    "--management.endpoints.web.expose=*",
                    "--requireHz=true",
                    "--spring.cloud.zookeeper.connect-string=localhost:" + 2181);
            url = "http://localhost:" + port;
            return context;
        } else return context;
    }

    public static String getURL() {


        return url;

    }

    public static TestingServer getZooServer() throws Exception {
        if (server == null) {
            server = new TestingServer(2181);
            return server;
        } else
            return server;
    }

    public static CuratorFramework getCurator() throws Exception {
        if (cli == null) {
            cli = CuratorFrameworkFactory.newClient(SingletonTestManager.getZooServer().getConnectString(), new RetryOneTime(2000));
            cli.start();
            return cli;
        } else
            return cli;

    }

    public static void shutDownServices() throws Exception {
        if (cli != null) {
            cli.close();
            cli = null;
        }

        if (server != null) {
            server.close();
            server = null;
        }

        if (hazelcastInstanceMember != null) {
            hazelcastInstanceMember.shutdown();
            hazelcastInstanceMember = null;
        }

    }
}
