// Who started in X between a-b and travelled through Y and ended in Z between c-d.
// converts a string in the format yyyy/MM/dd HH:mm:ss to a Long representing unix time stamp
def datetimeToTimestamp(dateTime:String) : Long = {
    val df = Seq(dateTime).toDF("date")
    val res = df.select(unix_timestamp($"date", "yyyy/MM/dd HH:mm:ss"))
    return res.head.getLong(0)
}

// check that a trip occured in a certain area between two times
def filterByAreaAndTime(row:(String, (String, Long, Long, Int, Int)), area:Int, timeA:Long, timeB:Long) : Boolean = {
    row match {
        case (taxiId, (uniqueKey, pickupTime, dropoffTime, pickupArea, dropoffArea)) =>
            (pickupArea == area || dropoffArea == area) && 
            (   (timeA <= pickupTime && pickupTime <= timeB) || 
                (timeA <= dropoffTime && dropoffTime <= timeB)
            )
        case _ => false
    } 
}

val pathToData = "/Users/Daniel/Documents/cs239-deployment/assignments/Assignment3/taxi_trips_small.json"

val taxiData = spark.read.format("json").load(pathToData).rdd

val taxiFiltered = taxiData.filter( row => 
    row.getAs("unique_key") != null &&
    row.getAs("taxi_id") != null &&
    row.getAs("trip_start_timestamp") != null &&
    row.getAs("trip_end_timestamp") != null &&
    row.getAs("pickup_community_area") != null &&
    row.getAs("dropoff_community_area") != null )

val taxiPairRdd = taxiFiltered.map( row =>
        (//key value pair
            row.getAs[String]("taxi_id"),
            (row.getAs[String]("unique_key"), row.getAs[String]("trip_start_timestamp").toDouble.toLong,
                row.getAs[String]("trip_end_timestamp").toDouble.toLong, row.getAs[String]("pickup_community_area").toInt,
                row.getAs[String]("dropoff_community_area").toInt
            )
        )
    )

val t1 = datetimeToTimestamp("2016/12/07 01:30:00")
val t2 = datetimeToTimestamp("2016/12/07 03:30:00")
val test = taxiPairRdd.filter( r => filterByAreaAndTime(r,8,t1,t2))

// TODO: join all the tables of (taxi_id, unique_key) to find taxi_id that is in each table. after each join
// reduce as well so we have (taxi_id, [list of unique_keys])

// TODO: take (taxi_id, [list of unique_keys]) and get all those rides and sort by timestamps

// TODO: given a list of unique_keys -> List( (startArea,time), (stopArea,time), .... )

def getTaxisWithConstraints(startArea:Int, stopArea:Int, travelledThrough:List[Int]) : List[String] = {

}




