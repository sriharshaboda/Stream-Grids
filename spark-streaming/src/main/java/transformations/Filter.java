package transformations;

import com.wipro.ats.bdre.md.api.GetProperties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import scala.Tuple2;
import util.WrapperMessage;

import java.util.*;
import java.util.Map;

/**
 * Created by cloudera on 5/21/17.
 */
public class Filter implements Transformation {
    @Override
    public JavaPairDStream<String,WrapperMessage> transform(JavaRDD emptyRDD, Map<Integer, JavaPairDStream<String,WrapperMessage>> prevDStreamMap, Map<Integer, Set<Integer>> prevMap, Integer pid, StructType schema) {
        List<Integer> prevPidList = new ArrayList<>();
        prevPidList.addAll(prevMap.get(pid));
        Integer prevPid = prevPidList.get(0);
        System.out.println("Inside filter prevPid = " + prevPid);
        JavaPairDStream<String,WrapperMessage> prevDStream = prevDStreamMap.get(prevPid);

        GetProperties getProperties = new GetProperties();
        Properties filterProperties = getProperties.getProperties(String.valueOf(pid), "default");
        final String check = filterProperties.getProperty("operator");
        final String filterValue = filterProperties.getProperty("filtervalue");
        final String colName = filterProperties.getProperty("column");

        System.out.println("operator = " + check);
        System.out.println("filtervalue = " + filterValue);
        System.out.println("colName = " + colName);
        JavaDStream<WrapperMessage> dStream = prevDStream.map(s -> s._2);
        JavaDStream<WrapperMessage> finalDStream = dStream.transform(new Function<JavaRDD<WrapperMessage>, JavaRDD<WrapperMessage>>() {
            @Override
            public JavaRDD<WrapperMessage> call(JavaRDD<WrapperMessage> rddWrapperMessage) throws Exception {
                JavaRDD<Row> rddRow = rddWrapperMessage.map(record -> WrapperMessage.convertToRow(record));
                SQLContext sqlContext = SQLContext.getOrCreate(rddWrapperMessage.context());
                DataFrame dataFrame = sqlContext.createDataFrame(rddRow, schema);
                DataFrame filteredDF = null;

                if (dataFrame != null && !dataFrame.rdd().isEmpty()) {
                    if (check.equals("equals")) {
                        System.out.println("showing dataframe before filter ");
                        dataFrame.show(100);
                        filteredDF = dataFrame.filter(dataFrame.col(colName).equalTo(filterValue));
                        filteredDF.show(100);
                        System.out.println("showing dataframe after filter ");
                    } else {
                        System.out.println("showing dataframe before filter ");
                        dataFrame.show(100);
                        filteredDF = dataFrame.filter(dataFrame.col(colName).gt(filterValue));
                        filteredDF.show(100);
                        System.out.println("showing dataframe after filter ");
                    }
                }
                JavaRDD<WrapperMessage> finalRDD = emptyRDD;
                if (filteredDF != null)
                    finalRDD = filteredDF.javaRDD().map(s -> WrapperMessage.convertToWrapperMessage(s));
                return finalRDD;
            }
        });
        return finalDStream.mapToPair(s -> new Tuple2<String, WrapperMessage>(null,s));
    }
}