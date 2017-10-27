import org.apache.spark.sql.SparkSession
import org.scalatest.FunSuite
class LanguagePopularityTest extends FunSuite{
    val spark = SparkSession.builder().
        appName("Word Count")
        .master("local")
        .config("SparkHome","src/main/resources")
        .getOrCreate()

    test("LanguagePopularitySample") {
        val path = "src/main/resources/posts_questions_sample.json"
        val rdd = spark.read.format("json").load(path).rdd
        assert(LanguagePopularity.languagePopularity(rdd).collect ===
            Array(("scala",2), ("c++",3), ("python",3), ("java",1), ("c",1)))
    }

    test("LanguagePopularitySmall") {
        val path2 = "src/main/resources/posts_questions_small.json"
        val rdd2 = spark.read.format("json").load(path2).rdd
        assert(LanguagePopularity.languagePopularity(rdd2).collect ===
            Array(("c++",53), ("python",41), ("java",66), ("c",221)))
    }
}
