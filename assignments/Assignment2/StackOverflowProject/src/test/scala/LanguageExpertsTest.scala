import org.apache.spark.sql.SparkSession
import org.scalatest.FunSuite
class LanguageExpertsTest extends FunSuite {
    val spark = SparkSession.builder().
        appName("Word Count")
        .master("local")
        .config("SparkHome", "src/main/resources")
        .getOrCreate()

    test("LanguageExpertsSample") {
        val pathToQuestions = "src/main/resources/posts_questions_sample.json"
        val pathToAnswers = "src/main/resources/posts_answers_sample.json"
        val questionsRdd = spark.read.format("json").load(pathToQuestions).rdd
        val answersRdd = spark.read.format("json").load(pathToAnswers).rdd
        assert(LanguageExperts.languageExperts(questionsRdd, answersRdd)
            .collect.slice(0, 5)
            .map(l => (l._1, l._2.slice(0, 2)))
            .toSet ===
            Array(("scala", List((11, 100), (12, 30))), ("c++", List((11, 120), (12, 90))),
                ("python", List((11, 100), (13, 95))), ("java", List((13, 90))), ("c", List((12, 60), (11, 20)))
            ).toSet
        )
    }
    //    languageExperts.collect.slice(0,5).map(l => (l._1,l._2.slice(0,2)))
    test("LanguagePopularitySmall") {
        val pathToQuestions2 = "src/main/resources/posts_questions_small.json"
        val pathToAnswers2 = "src/main/resources/posts_answers_small.json"
        val questionsRdd2 = spark.read.format("json").load(pathToQuestions2).rdd
        val answersRdd2 = spark.read.format("json").load(pathToAnswers2).rdd
        assert(LanguageExperts.languageExperts(questionsRdd2, answersRdd2)
            .collect.slice(0, 5)
            .map(l => (l._1, l._2.slice(0, 2)))
            .toSet ===
            Array(("c", List((872, 4652), (22656, 2795))), ("python", List((363, 1154), (99, 702))),
                ("java", List((91, 975), (4926, 364))), ("c++", List((91, 957), (267, 705)))
            ).toSet
        )
    }
}
