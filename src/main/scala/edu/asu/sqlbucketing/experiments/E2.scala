package edu.asu.sqlbucketing.experiments

import edu.asu.sqlpartitioning.utils.ExtraOps.timedBlock
import edu.asu.sqlpartitioning.utils.MatrixOps._
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

/**
 * This class implements the E1 (as mentioned in the document).
 * Step 1: Write the matrices to tables with bucketing
 * Step 2: Read the matrices
 * Step 3: Perform multiplication operation
 * Step 4: Write the result to secondary storage
 *
 * Method [[execute()]] will do all the above mentioned steps.
 * And also calculate time required from step 3 to step 4.
 */
private[sqlbucketing] class E2(interNumParts: Int)(
  implicit spark: SparkSession
) {

  /**
   * Method to execute the required steps.
   *
   * @param basePath Base path on the secondary storage
   * @param log      Logger instance to display result on the console.
   */
  def execute(basePath: String)(
    implicit log: Logger
  ): Unit = {

    val (_, timeToDisk: Long) = timedBlock {
      val leftDF = spark.read.parquet(s"$basePath/common/left")
      val rightDF = spark.read.parquet(s"$basePath/common/right")

      leftDF
        .repartition(interNumParts, col("columnID"))
        .createOrReplaceTempView("left")

      rightDF
        .repartition(interNumParts, col("rowID"))
        .createOrReplaceTempView("right")
    }

    val dataTotalSeconds = timeToDisk / math.pow(10, 3)
    log.info(
      s"E2 -> Time to persist random data to disk after partitioning " +
        s"is $dataTotalSeconds seconds"
    )

    val (_, timeToMultiply: Long) = timedBlock {
      val leftDF = spark.read.table("left")
      val rightDF = spark.read.table("right")

      val res = leftDF.multiply(rightDF, interNumParts)

      res.count
    }

    val multiplyTotalSeconds = timeToMultiply / math.pow(10, 3)
    log.info(
      s"E2 -> Time to multiply and persist result to disk " +
        s"is $multiplyTotalSeconds seconds"
    )
  }

}
