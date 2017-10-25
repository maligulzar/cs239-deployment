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
val travelledThrough = List(8,8)
val startIn = 8
val endIn = 8

val first = (taxiPairRdd.filter( filterByAreaAndTime(_, startIn, t1, t2))
        .map{
            case (taxiId, (uniqueKey, pickupTime, dropoffTime, pickupArea, dropoffArea)) =>
                (
                    taxiId,
                    List( 
                        (uniqueKey, (pickupArea, pickupTime), (dropoffArea, dropoffTime) )
                    )
                )
        }
        .reduceByKey(_++_)
    )

// create a rdd for each area in travelledThrough
// .filter keeps rows that satisfy constraints
// .map keeps only (taxi_id, List( (unique_key, (pickupArea,pickupTime),(dropoffArea,dropoffTime))  ))
// .reduceByKey will concatenate the lists of unique_keys if a taxi made 
//      multiple trips that satisifed constraints
val rddForEachArea = travelledThrough.map( area => 
        taxiPairRdd.filter( filterByAreaAndTime(_,area,t1,t2) )
        .map{
            case (taxiId, (uniqueKey, pickupTime, dropoffTime, pickupArea, dropoffArea)) =>
                (
                    taxiId,
                    List( 
                        (uniqueKey, (pickupArea, pickupTime), (dropoffArea, dropoffTime) )
                    )
                )
        }
        .reduceByKey(_++_)
    )

// join all the rdds on taxi_id
// b.join(a) will give (taxi_id, (List( (unique_keys,...) ), List( (unique_keys,...)) ) 
// .map in order to combine the two Lists into one list
// .toSet.toList will get rid of duplicate tuples which can happen if
//      a trip satisfied constraints for multiple areas
// List[RDD[(String, List[(String, (Int, Long), (Int, Long))])]]
//   -> RDD[(String, List[(String, (Int, Long), (Int, Long))])]
val taxisVisitedAllAreas = rddForEachArea.foldLeft(first)( (b,a) => 
        b.join(a)
        .map{
            case (taxiId, (l1, l2)) => 
                (
                    taxiId,
                    (l2 ++ l2).toSet.toList
                )
        }
    )


// sort the list by dropoff time and drop the trip uniquekeys
// RDD[(String, List[(String, (Int, Long), (Int, Long))])] ->
// RDD[(String, List[(        (Int, Long), (Int, Long))])]
val taxiRoute = taxisVisitedAllAreas.map{
        case (taxiId, listTripInfo) => 
            (
                taxiId,
                listTripInfo.map{
                    case (uniqueKey, (pickupArea, pickupTime), (dropoffArea, dropoffTime)) =>
                        ((pickupArea, pickupTime), (dropoffArea, dropoffTime))
                }.sortBy{
                    case ((pickupArea, pickupTime), (dropoffArea,dropoffTime)) => pickupTime
                }
            )
    }

// TODO: given a list of unique_keys -> List( (startArea,time), (stopArea,time), .... )

def getTaxisWithConstraints(startArea:Int, stopArea:Int, travelledThrough:List[Int]) : List[String] = {

}




