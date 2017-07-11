package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 7/7/17.
 */
public class GroupByKey implements Transformation {
    @Override
    public JavaPairDStream<String, WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String, WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("Inside GroupBy prevPid1 = " + prevPid1);
        JavaPairDStream<String,WrapperMessage> inputDStream = prevDStreamMap.get(prevPid1);
        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "default");
        //operator can be groupByKey or groupByKeyAndWindow
        String operator = filterProperties.getProperty("operator");

        JavaPairDStream<String,Iterable<WrapperMessage>> finalDStream = null;
        if(operator.equalsIgnoreCase("groupByKey")){
            finalDStream = inputDStream.groupByKey();
        }
        JavaPairDStream<String,WrapperMessage> finalDStream2 = finalDStream.mapValues(new Function<Iterable<WrapperMessage>, WrapperMessage>() {
            @Override
            public WrapperMessage call(Iterable<WrapperMessage> rddWrapperMessage) throws Exception {
                return (WrapperMessage)rddWrapperMessage.iterator().next();
            }
        });
        return finalDStream2;
    }
}
