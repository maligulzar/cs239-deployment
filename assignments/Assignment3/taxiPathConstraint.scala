// Who started in X between a-b and travelled through Y and ended in Z between c-d.
val pathToData = "/Users/Daniel/Documents/cs239-deployment/assignments/Assignment3/taxi_trips_small.json"

val taxiData = spark.read.format("json").load(pathToData).rdd

// TODO: convert date:String->timestamp:Long
def DatetimeToTimestamp(dateTime:String) : Long = {
    val df = Seq(dateTime).toDF("date")
    val res = df.select(unix_timestamp($"date", "yyyy/MM/dd HH:mm:ss"))
    return res.head.getLong(0)
}

// TODO: find all rows that start/stop in X and have a pickup/dropoff time in between a-b
// function(area, timeA, timeB) -> all rows that satsify: dropoff or pickup community area matches
//                                                      dropoff or pickuptime is inbetween timeA, timeB
//                                 (taxi_id, (unique_key, ))

// TODO: join all the tables of (taxi_id, unique_key) to find taxi_id that is in each table. after each join
// reduce as well so we have (taxi_id, [list of unique_keys])

// TODO: take (taxi_id, [list of unique_keys]) and get all those rides and sort by timestamps

// TODO: given a list of unique_keys -> List( (startArea,time), (stopArea,time), .... )

def getTaxisWithConstraints(startArea:Int, stopArea:Int, travelledThrough:List[Int]) : List[String] = {

}




