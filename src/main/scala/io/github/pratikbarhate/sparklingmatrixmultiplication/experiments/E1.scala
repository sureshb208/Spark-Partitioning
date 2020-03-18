package io.github.pratikbarhate.sparklingmatrixmultiplication.experiments

import io.github.pratikbarhate.sparklingmatrixmultiplication.utils.ExtraOps.timedBlock
import io.github.pratikbarhate.sparklingmatrixmultiplication.utils.MatrixOps._
import org.apache.log4j.Logger
import org.apache.spark.SparkContext

/**
 * This class implements the E1 (as mentioned in the document).
 * Step 1: Write the random matrices to secondary storage
 * Step 2: Read the matrices
 * Step 3: Perform multiplication operation
 * Step 4: Write the result to secondary storage
 *
 * Method [[execute()]] will do all the above mentioned steps.
 * And also calculate time required from step 2 to step 4.
 *
 * @param sc [[SparkContext]] of the application.
 */
class E1(interNumParts: Int)(implicit sc: SparkContext) {

  /**
   * Method to execute the required steps.
   *
   * @param basePath Base path on the secondary storage
   * @param log      Logger instance to display result on the console.
   */
  def execute(basePath: String)(implicit log: Logger): Unit = {

    val (_, timeToDisk: Long) = timedBlock {
      val left = sc.objectFile[(Long, (Long, Double))](s"$basePath/common/left")
      val right = sc.objectFile[(Long, (Long, Double))](s"$basePath/common/right")

      left.saveAsObjectFile(s"$basePath/e1/left")
      right.saveAsObjectFile(s"$basePath/e1/right")
    }

    val (_, timeToMultiply: Long) = timedBlock {
      val leftMat = sc.objectFile[(Long, (Long, Double))](s"$basePath/e1/left")
      val rightMat = sc.objectFile[(Long, (Long, Double))](s"$basePath/e1/right")

      val res = leftMat.multiply(rightMat, interNumParts)

      res.saveAsObjectFile(s"$basePath/e1/multiplication_op")
    }

    val dataTotalSeconds = timeToDisk / math.pow(10, 3)
    val dataMinutes = (dataTotalSeconds / 60).toLong
    val dataSeconds = (dataTotalSeconds % 60).toInt

    val multiplyTotalSeconds = timeToMultiply / math.pow(10, 3)
    val multiplyMinutes = (multiplyTotalSeconds / 60).toLong
    val multiplySeconds = (multiplyTotalSeconds % 60).toInt

    log.info(s"E1 -> Time to persist random data to disk is $dataMinutes minutes $dataSeconds seconds")
    log.info(s"E1 -> Time to multiply and persist result to disk " +
      s"is $multiplyMinutes minutes $multiplySeconds seconds")
  }

}
