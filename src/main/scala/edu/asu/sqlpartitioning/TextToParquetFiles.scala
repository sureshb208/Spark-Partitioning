package edu.asu.sqlpartitioning

import edu.asu.sqlpartitioning.utils.Parser.readMatrix
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

object TextToParquetFiles {

  def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      throw new IllegalArgumentException(
        "Base path for storing data and spark history log directory are expected." +
          s"\nProvide: ${args.toList}"
      )
    }

    val basePath = args(0)
    val historyDir = args(1)

    val conf = new SparkConf()
      .setAppName("parsing_text_to_parquet_files")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.history.fs.logDirectory", historyDir)
      .set("spark.eventLog.enabled", "true")
      .set("spark.eventLog.dir", historyDir)

    implicit val spark: SparkSession =
      SparkSession.builder().appName("ParquetFiles").config(conf).getOrCreate()

    val left: DataFrame = readMatrix(s"$basePath/raw/left")
    val right: DataFrame = readMatrix(s"$basePath/raw/right")

    left.write.partitionBy("columnID").parquet(s"$basePath/common/left")
    right.write.partitionBy("rowID").parquet(s"$basePath/common/right")
  }
}
