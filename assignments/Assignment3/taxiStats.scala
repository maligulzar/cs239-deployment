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
        )
    )

val idAndDollarsPerMinute = taxiPairRdd.reduceByKey( (tripX, tripY) =>
        (tripX._1 + tripY._1, tripX._2 + tripY._2)
    ).map{
        case (taxi_id, (dollars, seconds)) => 
            seconds match {
                case 0 => (taxi_id, 0.0)
                case _ => (taxi_id, dollars/(seconds.toDouble/60.0))
            }
    }
    
val dollarsPerMinute = idAndDollarsPerMinute.map( row => row._2)
val numTaxis = dollarsPerMinute.count

val mean = dollarsPerMinute.sum/numTaxis
val devs = dollarsPerMinute.map( dpm => (dpm - mean) * (dpm - mean) )
val std = Math.sqrt(devs.sum/ (numTaxis - 1))





