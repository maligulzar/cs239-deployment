import org.apache.spark.sql.SparkSession
import org.scalatest.FunSuite
object LanguagePopularity {
    def languagePopularity(rdd:org.apache.spark.rdd.RDD[org.apache.spark.sql.Row]) :org.apache.spark.rdd.RDD[(String,Int)]  = {
        val languages = List("python","java","scala","c","c++")

        // turn each row into an array of strings
        val text = rdd.map(row =>
            List(row.getAs("title").toString,row.getAs("body").toString,row.getAs("tags").toString)
        )

        // take each string in the array and make it lowercase and split at everything except A-Za-z0-9 and '+'
        //flatMap combines all the words together for each array
        val words = text.map( txt =>
            txt.flatMap(strings =>
                strings.toLowerCase.split("[^\\w+]+")
            )
        )

        // filter each array of words and keep a set of those in the languages dictionary. combine all into one list
        val filteredForLanguages = words.flatMap(wordArray => wordArray
            .filter(languages.contains(_))
            .toSet.toList
        )


        // basic word count
        val languageFreq = filteredForLanguages.map(word => (word,1)).reduceByKey(_+_)
//        languageFreq.saveAsTextFile("src/main/resources/outfile")
        return languageFreq
    }
}
