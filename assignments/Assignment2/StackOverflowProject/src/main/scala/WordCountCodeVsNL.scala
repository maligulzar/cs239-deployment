import scala.io.Source
import scala.util.matching.Regex
object WordCountCodeVsNL {
    def wordCountCodeVsNL(questions: org.apache.spark.rdd.RDD[org.apache.spark.sql.Row])
        : (Array[(String, Int)], Array[(String, Int)])  = {

        val pathToStopWords = "src/main/resources/stopwords.txt"
        val stopWords = Source.fromFile(pathToStopWords).getLines.toList

        val questionsFiltered = questions.filter( row =>
            row.getAs("id") != null &&
            row.getAs("title") != null &&
            row.getAs("body") != null &&
            row.getAs("tags") != null)


        // val replaceNumbers = "(?<!\\w)[0-9]+(?!\\w)".r
        val questionsText = questionsFiltered.map(row =>
            (row.getAs("title").toString + " " + row.getAs("body").toString).toLowerCase
        )

        //  (?s) enables dotall mode which makes '.' match newline as well
        // .*? the ? makes it match 0 or 1 of the preceding expression so that it doesnt capture everything between the first <code> and last </code>
        val patternFindCode = "(?s)(?<=(<code>)).*?(?=(</code>))".r
        val codeWords = questionsText.flatMap( text =>
            patternFindCode.findAllIn(text)
            .toList
            .mkString(" ")
            .split("[^\\w']+")
            .filter(!stopWords.contains(_))
        )

        // wasn't sure how to extract all text outside of code tags so replaced all the text in beteen tags
        val patternReplaceCode = "(?s) (<code>.*?</code>)|(<.*?>)|(</.*?>)".r
        val naturalWords = questionsText.flatMap( text =>
            patternReplaceCode.replaceAllIn(text,"")
            .split("[^\\w']+")
            .filter(!stopWords.contains(_))
        )

        val codeWordsCount = codeWords.map( word => (word, 1)).reduceByKey(_+_).collect.sortBy(_._2).reverse
        val naturalWordsCount = naturalWords.map( word => (word, 1)).reduceByKey(_+_).collect.sortBy(_._2).reverse
        return (codeWordsCount,naturalWordsCount)
    }
}
