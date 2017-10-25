// This is a script for Query 1 by Mushi
 
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.joda.time.DateTime
import math.{pow, atan2, sqrt, cos, sin}

/*
Write a query that, within 5 mile radius of the BoA building in SF (given as a lat/lon pair), 
compute the average sample air quality (pm10_hours_summary) for each month between 2005 and 2008
SF:
lat: 37.773972
long: -122.43129
*/

object Query1 {
	def main(args: Array[String]) {
		if (args.length != 6) {
			System.out.println("Usage: jar <data_file_name> <lat(radians)> <long(radians)> <radius(miles)> <year_start> <year_end>")
			System.exit(1)
		}

		// create Spark context with Spark configuration
		val sc = new SparkContext(new SparkConf().setAppName("Query1"))

		// get threshold
		val lat = args(1).toDouble
		var long = args(2).toDouble
		var radius = args(3).toDouble
		var year_start = args(4).toInt
		var year_end = args(5).toInt


		// read in text file and split each document into words
		val csv = sc.textFile(args(0))
		val rows = csv.map(line => line.split(",").map(_.trim))
		val header = rows.first
		val data = rows.filter(_(0) != header(0))
		val cols = data.map(row => Array[String](row(5), row(6), DateTime.parse(row(9)).getYear().toString, row(13)))
		val time = cols.filter(_(2).toInt <= year_end).filter(_(2).toInt >= year_start)
		val relevant = time.filter(location => 
		{   
		    var a = pow(sin((location(0).toDouble - lat)/2), 2) + cos(lat) * cos(location(0).toDouble) * pow(sin((location(1).toDouble - long)/2),2)
		    var c = 2 * atan2(sqrt(a) , sqrt(1-a))
		    var d = 6371e3 / 1609.34 * c

		    if (d > radius) {
		        false
		    } else {
		        true
		    }

		}
		)
		val avg = relevant.map(_(3).toDouble).mean()
		sc.stop()

		var output = avg - (avg % 0.01)
		System.out.println(s"The average pm10 hourly measurement within the range from $year_start to $year_end is $output.")

	}
}

