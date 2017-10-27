import org.apache.spark.sql.SparkSession
import org.scalatest.FunSuite
class WordCountCodeVsNLTest extends FunSuite {
    val spark = SparkSession.builder().
        appName("Word Count")
        .master("local")
        .config("SparkHome", "src/main/resources")
        .getOrCreate()

    test("WordCountCodeVsNLSample") {
        val pathToQuestions = "src/main/resources/posts_questions_sample.json"
        val questionsRdd = spark.read.format("json").load(pathToQuestions).rdd
        val (codeWordsCount: Array[(String,Int)], nlWordsCount: Array[(String,Int)]) =
            WordCountCodeVsNL.wordCountCodeVsNL(questionsRdd)
        assert( codeWordsCount.slice(0,5) ===
            Array(("val", 8), ("list", 6), ("world", 5), ("hello", 4), ("print", 3))
        )
        assert( nlWordsCount.slice(0,5) ===
            Array(("fun", 7), ("hello", 6), ("python", 5), ("world", 4), ("scala", 3))
        )
    }

    test("WordCountCodeVsNLSmall") {
        val pathToQuestions2 = "src/main/resources/posts_questions_small.json"
        val questionsRdd2 = spark.read.format("json").load(pathToQuestions2).rdd
        val (codeWordsCount: Array[(String,Int)], nlWordsCount: Array[(String,Int)]) =
            WordCountCodeVsNL.wordCountCodeVsNL(questionsRdd2)
        assert( codeWordsCount.slice(0, 5) ===
            Array(("gt",632), ("lt",586), ("1",190), ("script",171), ("js",158))
        )
        assert( nlWordsCount.slice(0,5) ===
            Array(("gt",604), ("lt",563), ("i'm",481), ("using",474), ("like",456))
        )
    }
}