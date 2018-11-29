package utilities;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
//import de.flapdoodle.embed.mongo.config.IMongodConfig;
//import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
//import de.flapdoodle.embed.mongo.config.Net;
//import de.flapdoodle.embed.mongo.distribution.Version;
//import de.flapdoodle.embed.process.distribution.Distribution;
//import de.flapdoodle.embed.process.runtime.Network;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;
//import de.flapdoodle.embed.mongo.MongodExecutable;
//import de.flapdoodle.embed.mongo.MongodStarter;

import java.util.Collections;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonTestManager {

    private static final Logger logger = LoggerFactory.getLogger(SingletonTestManager.class);

    private static HazelcastInstance hazelcastInstanceMember;
    private static CuratorFramework cli;
    private static TestingServer server;
    private static ConfigurableApplicationContext context;
    private static String url;
    private static TestRestTemplate template;
    private static KafkaLocal kafkaserver;
    //private static MongodExecutable mongodExecutable;





    public static void startUpServices(Class startUpClassName) throws Exception {
        SingletonTestManager.getZooServer();
        SingletonTestManager.getCurator();
        SingletonTestManager.getHz();
        SingletonTestManager.getApplicationContext(startUpClassName);
        SingletonTestManager.getRestTemplate();
        SingletonTestManager.getKafkaserver().start();
        //SingletonTestManager.startMongo();

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
                    "--spring.cloud.zookeeper.connect-string=localhost:" + 2181,
                    "--kafka.bootstrap-servers=localhost:9092",
                    "--spring.data.mongodb.host=localhost");
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

    public static KafkaLocal getKafkaserver() throws Exception{


        if(kafkaserver==null){
            logger.info("Starting kafka server.");
            Properties kafkaProperties = new Properties();

            kafkaProperties.load(Class.class.getResourceAsStream(
                    "/kafka-server.properties"));
            // override the Zookeeper url.
            kafkaProperties.setProperty("zookeeper.connect", "localhost:2181");
            kafkaProperties.setProperty("port", "9092");
            kafkaserver = new KafkaLocal(kafkaProperties);
        }


        return kafkaserver;


    }

//    public static void startMongo() throws Exception{
//
//        if(mongodExecutable == null){
//            String ip = "localhost";
//            int randomPort = 27017;
//
//            IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
//                    .net(new Net(ip, randomPort, Network.localhostIsIPv6()))
//                    .build();
//
//            MongodStarter starter = MongodStarter.getDefaultInstance();
//
//            mongodExecutable = starter.prepare(mongodConfig);
//            mongodExecutable.start();
//
//        }
//
//
//    }



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

        if(kafkaserver != null)
            SingletonTestManager.getKafkaserver().stop();
//
//        if(mongodExecutable != null)
//            mongodExecutable.stop();

    }
}
