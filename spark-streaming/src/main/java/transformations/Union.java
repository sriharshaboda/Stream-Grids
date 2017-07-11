package transformations;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import util.WrapperMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by cloudera on 5/22/17.
 */
public class Union implements Transformation
{
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid1 = prevPidList.get(0);
        System.out.println("before entering for loop first prevPid1 = " + prevPid1);
        JavaPairDStream<String,WrapperMessage> unionedDStream = prevDStreamMap.get(prevPid1);
        for(int i=1;i< prevPidList.size();i++){
            System.out.println("union of dstream of pid = " + prevPidList.get(i));
            JavaPairDStream<String,WrapperMessage> dStream1 = prevDStreamMap.get(prevPidList.get(i));
            if(unionedDStream!=null && dStream1!=null){

                System.out.println("showing dstream df1 before union ");
                dStream1.print(100);
                System.out.println("showing dataframe unionedDF before union ");
                unionedDStream.print(100);
                unionedDStream = unionedDStream.union(dStream1);
                System.out.println("showing dataframe unionedDF after union ");
                unionedDStream.print(100);

            }

        }
        return unionedDStream;
    }

}

