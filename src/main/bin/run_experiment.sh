#!/bin/bash
input_checks() {
	if [ "$1" == "-h" ]; then
			echo "Usage: $(basename "${0}") {WORK_FLOW = (SQL, RDD, BUCKET, HIVE)} {BASE_PATH} {EXPERIMENT NUMBER - can be e1, e2 or e3} {NUMBER OF PARTITIONS}"
			exit 0
	fi

	if [ $# -lt 4 ]
	then
			echo "Missing Operand"
			echo "Run $(basename "${0}") -h for usage"
			exit 0
	fi

	echo "Your Input :-"
	echo "WORK_FLOW - ${1}"
	echo "BASE_PATH - ${2}"
	echo "EXPERIMENT - ${3}"
	echo "NUMBER OF PARTITIONS - ${4}"
}

find_script_home() {
  pushd . > /dev/null
  SCRIPT_DIRECTORY="${BASH_SOURCE[0]}";
  while [ -h "${SCRIPT_DIRECTORY}" ];
  do
    cd "$(dirname "${SCRIPT_DIRECTORY}")" || exit
    SCRIPT_DIRECTORY="$(readlink "$(basename "${SCRIPT_DIRECTORY}")")";
  done
  cd "$(dirname "${SCRIPT_DIRECTORY}")" > /dev/null || exit
  SCRIPT_DIRECTORY="$(pwd)";
  popd  > /dev/null || exit
  APP_HOME="$(dirname "${SCRIPT_DIRECTORY}")"
}

main() {

	find_script_home
  input_checks "${@}"

  WORK_FLOW=$1
	BASE_PATH=$2
	EXPERIMENT=$3
	PARTITIONS=$4

  case $WORK_FLOW in

  "RDD")
    echo "Running RDD experiment"
    # spark command for running an experiment
    spark-submit \
    --class edu.asu.sparkpartitioning.Main \
    --master spark://172.31.19.91:7077 \
    --deploy-mode client \
    "${APP_HOME}"/lib/Spark-Partitioning-0.1-SNAPSHOT.jar \
    hdfs://172.31.19.91:9000"${BASE_PATH}" hdfs://172.31.19.91:9000/spark/applicationHistory \
    "${PARTITIONS}" "${EXPERIMENT}"

    #remove temp data generated by the experiment from the dfs
	  hdfs dfs -rm -r -skipTrash "${BASE_PATH}"/"${EXPERIMENT}"/*
  ;;

  "SQL")
    echo "Running SQL experiment"
    # spark command for running an experiment

    spark-submit \
    --class edu.asu.sqlpartitioning.Main \
    --master spark://172.31.19.91:7077 \
    --deploy-mode client \
    "${APP_HOME}"/lib/Spark-Partitioning-0.1-SNAPSHOT.jar \
    hdfs://172.31.19.91:9000"${BASE_PATH}" hdfs://172.31.19.91:9000/spark/applicationHistory \
    "${PARTITIONS}" "${EXPERIMENT}"

    #remove temp data generated by the experiment from the dfs
	  hdfs dfs -rm -r -skipTrash "${BASE_PATH}"/"${EXPERIMENT}"/*
  ;;

  "HIVE")
    echo "Running SQL with HIVE experiment"
    # spark command for running an experiment

    spark-submit \
    --class edu.asu.sqlhive.Main \
    --master spark://172.31.19.91:7077 \
    --deploy-mode client \
    "${APP_HOME}"/lib/Spark-Partitioning-0.1-SNAPSHOT.jar \
    hdfs://172.31.19.91:9000"${BASE_PATH}" hdfs://172.31.19.91:9000/spark/applicationHistory \
    "${PARTITIONS}" "${EXPERIMENT}"

    #remove temp data generated by the experiment from the dfs
	  hdfs dfs -rm -r -skipTrash "${BASE_PATH}"/"${EXPERIMENT}"/*
	 ;;

  "BUCKET")
    echo "Running SQL with bucketing experiment"
    # spark command for running an experiment

    spark-submit \
    --class edu.asu.sqlbucketing.Main \
    --master spark://172.31.19.91:7077 \
    --deploy-mode client \
    "${APP_HOME}"/lib/Spark-Partitioning-0.1-SNAPSHOT.jar \
    hdfs://172.31.19.91:9000"${BASE_PATH}" hdfs://172.31.19.91:9000/spark/applicationHistory \
    "${PARTITIONS}" "${EXPERIMENT}"

    #remove temp data generated by the experiment from the dfs
	  hdfs dfs -rm -r -skipTrash "${BASE_PATH}"/"${EXPERIMENT}"/*
  ;;
  esac

}

main "${@}"