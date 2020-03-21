### Spark-Partitioning

Investigating and benchmarking how partitioning of data on HDFS will affect Spark performance

#### Steps to execute the experiments.

1. Build using the command - `mvn clean package`. It will create a tar.gz file named `Spark-Partitioning-0.1-SNAPSHOT.tar.gz` with the 4 directories `bin`, `etc`, `python` and `lib`. 

2. To parse the text files and create object files

    ```
        nohup spark-submit \
        --class edu.asu.sparkpartitioning.TextToObjectFiles \
        --master spark://172.31.19.91:7077 \
        --deploy-mode client \
        ${PATH_TO_JAR}/Spark-Partitioning-0.1-SNAPSHOT.jar \
        hdfs://172.31.19.91:9000/${BASE_PATH} hdfs://172.31.19.91:9000/spark/applicationHistory > parsing_logs.out &
    ```
   
3. To execute a particular experiment

    ```
        nohup spark-submit \
        --class edu.asu.sparkpartitioning.Main \
        --master spark://172.31.19.91:7077 \
        --deploy-mode client \
        ${PATH_TO_JAR}/Spark-Partitioning-0.1-SNAPSHOT.jar \
        hdfs://172.31.19.91:9000/${BASE_PATH} hdfs://172.31.19.91:9000/spark/applicationHistory ${NUM_PARTITION} ${EXPERIMENT} > job_logs_${NUM_PARTITION}.out &
    ```

**Coding style note**
1. Python indentation and tabs = 4 spaces. (We are using Python 3)
2. Bash script indentation and tabs = 2 spaces.
3. Set up the Scalafmt plugin and use the `.scalafmt.conf` for auto formatting.