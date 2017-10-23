// min,max,mean,std of taxis money/minute
val pathToData = "/Users/Daniel/Documents/cs239-deployment/assignments/Assignment3/taxi_trips_small.json"

val taxiData = spark.read.format("json").load(pathToData).rdd

val taxiFiltered = taxiData.filter( row => 
    row.getAs("taxi_id") != null &&
    row.getAs("trip_total") != null &&
    row.getAs("trip_seconds") != null)

val taxiPairRdd = taxiFiltered.map( row =>
        (//key value pair
            row.getAs("taxi_id").toString,
            (row.getAs[String]("trip_total").toFloat, row.getAs[String]("trip_seconds").toInt)
            // (row.getAs("trip_total").toString.toFloat, row.getAs("trip_seconds").toString.toInt)
        )
    )

val taxiDollarPerMinute = taxiPairRdd.reduceByKey( (tripX, tripY) =>
        (tripX._1 + tripY._1, tripX._2 + tripY._2)

    )


