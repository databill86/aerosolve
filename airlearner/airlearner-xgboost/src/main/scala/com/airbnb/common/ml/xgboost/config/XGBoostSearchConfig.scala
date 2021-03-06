package com.airbnb.common.ml.xgboost.config

import scala.util.Try
import com.typesafe.config.Config
import org.apache.spark.SparkContext
import com.airbnb.common.ml.search.MonteCarloParams
import com.airbnb.common.ml.util.ConfigUtils


case class XGBoostSearchConfig(
    trainingData: String,
    evalData: String,
    round: Int,
    minRound: Int,
    outputPath: String,
    overwrite: Boolean,
    paramMap: List[(String, List[Any])],
    stableParamMap: List[(String, Any)],
    partitionSpec: String,
    outputTable: String,
    trainingDataDir: String,
    evalDataDir: String,
    useModelPostfix: Boolean,
    reverse: Boolean,
    tmpFolder: String,
    loss: String
) {
}

object XGBoostSearchConfig {

  def loadConfig(sc: SparkContext, config: Config): XGBoostSearchConfig = {
    val trainingData = config.getString("training_query")
    val evalData = config.getString("eval_query")
    val round = config.getInt("round")
    val minRound = config.getInt("min_round")

    val outputPath = config.getString("search_output")
    val overwrite = config.getBoolean("overwrite")
    val paramMap = MonteCarloParams.loadFromConfig(config.getConfig("params"))

    val stableParamMap = ConfigUtils.configToMap(config, "stable_param_map")
    val stableParamList: List[(String, Any)] = if (stableParamMap.isEmpty) {
      // default binary:logistic
      List("objective" -> "binary:logistic")
    } else {
      stableParamMap.toList
    }

    val useModelPostfix = config.getBoolean("use_model_postfix")
    val partitionSpec = config.getString("output_partition_spec")
    val outputTable = config.getString("output_table")
    val trainingDataDir = config.getString("training_data_dir")
    val evalDataDir = config.getString("eval_data_dir")

    val reverse = config.getBoolean("reverse_search")
    val temFolder = Try(config.getString("tmp_folder")).getOrElse("/mnt/var/spark/tmp/")

    val loss = Try(config.getString("loss")).getOrElse("logLoss")

    XGBoostSearchConfig(
      trainingData,
      evalData,
      round,
      minRound,
      outputPath,
      overwrite,
      paramMap,
      stableParamList,
      partitionSpec,
      outputTable,
      trainingDataDir,
      evalDataDir,
      useModelPostfix,
      reverse,
      temFolder,
      loss)
  }

  def updateParamMap(
      cfg: XGBoostSearchConfig,
      paramMap: List[(String, List[Any])]
  ): XGBoostSearchConfig = {
    XGBoostSearchConfig(
      cfg.trainingData,
      cfg.evalData,
      cfg.round,
      cfg.minRound,
      cfg.outputPath,
      cfg.overwrite,
      paramMap,
      cfg.stableParamMap,
      cfg.partitionSpec,
      cfg.outputTable,
      cfg.trainingDataDir,
      cfg.evalDataDir,
      cfg.useModelPostfix,
      cfg.reverse,
      cfg.tmpFolder,
      cfg.loss)
  }
}
