// This is a script for Query 1 by Mushi
 
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.joda.time.DateTime
import math

/*

Write a query that, within 5 mile radius of the BoA building in SF (given as a lat/lon pair), 
compute the average sample air quality (pm10_hours_summary) for each month between 2005 and 2008

lat: 37.773972
long: -122.43129
*/
    
// create Spark context with Spark configuration
val sc = new SparkContext(new SparkConf().setAppName("Query 1"))

// get threshold
val lat = 37.773972
var long = -122.43129


// read in text file and split each document into words
val csv = sc.textFile("pm10_data_200M.csv")
val rows = csv.map(line => line.split(",").map(_.trim))
val header = rows.first
val data = rows.filter(_(0) != header(0))
val cols = data.map(row => Array[String](row(5), row(6), DateTime.parse(row(9)).getYear().toString, row(13)))
val time = cols.filter(_(2).toInt <= 2008).filter(_(2).toInt >= 2005)
val relevant = time.filter(location => 
{   
    var a = math.pow(math.sin((location(0).toDouble - lat)/2), 2) + math.cos(lat) * math.cos(location(0).toDouble) * math.pow(math.sin((location(1).toDouble - long)/2),2)
    var c = 2 * math.atan2(math.sqrt(a) , math.sqrt(1-a))
    var d = 6371e3/1609.34 * c

    if (d > 1000) {
        false
    } else {
        true
    }

}
)

val avg = relevant.map(_(3).toDouble).mean()

var output = avg - (avg % 0.01)


// System.out.println(s"$avg")
