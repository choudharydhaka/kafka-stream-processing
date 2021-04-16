package nz.co.dhaks.kafka.stream;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nz.co.dhaks.kafka.stream.model.Item;

 

public class MuleStream {
	  private static Logger log = LoggerFactory.getLogger(MuleStream.class);
	public static final String INPUT_TOPIC_NAME= "rss-feed-input";
	private static final String OUTPUT_TOPIC_NAME =  "rss-feed-output";
	private static final String KAFKA_SERVER =  "127.0.0.1:9092";
	private static final String APPLICATION_ID =  "mule-rss-application";
	
	
	private static Properties config=null;
	private static Serde<JsonNode> jsonSerde=null;

	public static String FILTER_JOB="java";
	public static String COUNTRY_REG="java";
	public static String BUDGET_REG="java"; 
	
	

	public MuleStream() {
		super();
		this.config= new Properties(); 
		this.config.put(StreamsConfig.APPLICATION_ID_CONFIG,APPLICATION_ID);
		this.config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
	//	this.config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
	
		
		
		
		//Consumed.with(Serdes.String(), jsonSerde));
        // json Serde
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        
       this.jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
     // Value is String
     	//	this.config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, jsonSerde.getClass());
     		// Key is string
    	//	this.config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
	}

 

	public static void main(String[] args) {

		MuleStream app = new MuleStream();
		StreamsBuilder builder = new StreamsBuilder();
		// 1. stream from kafka
		KStream<String, JsonNode> s_feed = builder.stream(INPUT_TOPIC_NAME, Consumed.with(Serdes.String(), jsonSerde));

		// 2. Select key
		KStream<String, JsonNode> s_feed_t = s_feed
				// 3. filter only objects
				.filter((key, jsonObject) -> !jsonObject.getNodeType().name().equals("ARRAY"))
				// 4. Filtet feeds which are having Country and Budget details
				.filter((key, jo) -> {
					Pattern country = Pattern.compile("(Country</b>:\\s+)\\w+");
					Pattern budget = Pattern.compile("(Budget</b>:\\s+\\$\\d+)");
					return budget.matcher(jo.get(Item.DESCRIPTION).toString()).find()
							&& country.matcher(jo.get(Item.DESCRIPTION).toString()).find();

				})
				// 5. make guid as a key
				.selectKey((ignoreKey, jsonObject) -> {
					log.info("Processing message with GUID" + jsonObject.get("guid").toString());
					return jsonObject.get(Item.GUID).toString();
				}) 

				// 6. Transform Values
				.mapValues((jo) -> {
					// getMessage(jo);

					String msg = String.format("The %s language has job from %s with budget %s, you can apply at %s",
							FILTER_JOB, "**COUNTRY**", // countryTemp,
							"**$200**", // budgetTemp,

							jo.get(Item.LINK)
							);
					ObjectNode newObject = JsonNodeFactory.instance.objectNode();
					newObject.put("message", msg);
					return newObject;
				});

		// 7. write back to kafka stream
		s_feed_t.to(OUTPUT_TOPIC_NAME, Produced.with(Serdes.String(), jsonSerde));

		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp();
		streams.start();

		// print the topology
		streams.localThreadsMetadata().forEach(data -> log.info(data.toString()));

		// shutdown hook to correctly close the streams application
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}

	// Please ignore below code
	private static void getMessage(JsonNode jo) {
		Pattern country = Pattern.compile("(Country</b>:\\s+[a-zA-Z\\s]+)+", Pattern.DOTALL|Pattern.MULTILINE);
		String data=jo.get(Item.DESCRIPTION).toString();
		Matcher m_c=country.matcher(data);
		//Static value
		String ss="We hava a Web application programed by java ee connected to oracle Database and published on wildfly server &amp;quot;joboos server &amp;quot;, We face a problem that application server always hanging , and we must to restart the wildfly server services. <br />\nProbably we have a memory leaking wrong configuration.<br />\nWe need some one to do for us the best tuning for Java applications server .<br />\nWe have a good Infrastructure is :<br />\nCpu: 2&times; Intel Xeon Silver4214 (24c/48t)<br />\nRam: 384GB<br />\nNetwork 10G.<br />\nStorge : NVME <br />\nOs: VMWARE Vsphere 6.7.<br /><br />\nWe need to fix our problem by in one of this way :<br />\n1- Try to solve current server fix the problem ,and do test for 10K current connection.<br />\n2- create a new servers and installation for wildfly server with high availability with tuning for 10K current connection.<br /><br /><b>Budget</b>: $500\n<br /><b>Posted On</b>: April 13, 2021 21:37 UTC<br /><b>Category</b>: Systems Administration<br /><b>Skills</b>:Server Virtualization,     VMWare,     Microsoft Windows Server,     Linux,     Apache HTTP Server,     Large (1000+ employees),     Windows,     Java,     System Administration,     VMware Administration,     WildFly,     JBoss,     Java EE,     Troubleshooting,     SocketCluster,     Load Balancing    \n<br /><b>Country</b>: Turkey\n<br /><a href=\"https://www.upwork.com/jobs/Tuning-Enterprise-Java-Applications-Running-VMware_%7E01892f2722b0a3a41f?source=rss\">click to apply</a>\n";
		m_c=country.matcher(ss);
		
		String countryTemp=null;
		 String budgetTemp=null;
		if (m_c.matches()) {
			
			countryTemp= m_c.group(1) ;

		}
		
		
		Pattern budget = Pattern.compile("(Budget</b>:\\s+(\\$\\d+))+");
		Matcher m_b=budget.matcher(jo.get(Item.DESCRIPTION).toString());
		
		
		if (m_b.find()) {
			m_b.reset();
			budgetTemp=m_b.group(2);

		};
	}
}
