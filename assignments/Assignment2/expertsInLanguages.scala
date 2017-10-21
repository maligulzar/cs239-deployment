// join posts_answers (a) with posts_questions (q) on a.parent_id and q.id

//  need question information and user id and answer score
//  each row -> (user id, ([lang1,...langN],score))

// parse relevant text (maybe just tags) for languages
// reduce by key (user id). put each into form (lang,score). reduce these by lang => (user id, [(lang1,score),...,(langN,score)])

// import org.apache.spark.rdd.PairRDDFunctions
val pathToQuestions = "/Users/Daniel/IdeaProjects/anothertry/src/main/resources/posts_questions_small.json"
val pathToAnswers = "/Users/Daniel/IdeaProjects/anothertry/src/main/resources/posts_answers_small.json"
val languages = List("python","java","scala","c","c++")

val questions = spark.read.format("json").load(pathToQuestions).rdd
val answers = spark.read.format("json").load(pathToAnswers).rdd

// some rows have owner_user_id==null. 
// filter all rows that have null values for fields that are going to be used
val answersFiltered = answers.filter(row => 
    row.getAs("owner_user_id") != null && 
    row.getAs("score") != null &&
    row.getAs("parent_id") != null)

val questionsFiltered = questions.filter( row => 
    row.getAs("id") != null &&
    row.getAs("title") != null &&
    row.getAs("body") != null &&
    row.getAs("tags") != null)

// use .toString otherwise get java.lang.ClassCastException: java.lang.String cannot be cast to scala.runtime.Nothing$
// rdd[(String,         List[String])]
//      question id     unique languages in the title/body/tags
// key = question_id, value = [languages]
val questionPairRdd = questionsFiltered.map(row => 
        (//tuple
            row.getAs("id").toString,
            List(row.getAs("title").toString, row.getAs("body").toString, row.getAs("tags").toString)
                .flatMap(strings => strings.toLowerCase.split("[^\\w+]+"))
                .filter(languages.contains(_))
                .toSet.toList
        )
    ).filter(row => !row._2.isEmpty) //list of languages is empty

// rdd[(String,     List[String])]
//      parent_id   owner_user_id, score
// key = pareint_id, value = [owner_user_id,score]
val answerPairRdd = answersFiltered.map(row => 
        (//tuple
            row.getAs("parent_id").toString,
            List(row.getAs("owner_user_id").toString.toInt, row.getAs("score").toString.toInt)
        )
    )

// rdd[(String,         (List[String],      List[String]))]
//      question_id      languages          owner_user_id,score
// join on question_id and parent_id to get key = question_id, value = ([languages],[owner_user_id,score])
var joinedQuestionAnswer = questionPairRdd.join(answerPairRdd)

// rdd[(String,         (List[String],  String)]
//      owner_user_id   languages,      score
// extract revelant info: (owner_user_id, ([languages],score))
var answerLanguagesAndScores = joinedQuestionAnswer.map(row =>  
        (
            row._2._2(0), 
            (
                row._2._1,
                row._2._2(1)
            )
        )
    )

// rdd[(String,         (List[(lang1,score),...,(langN,score)]))]
//      user_id
var userLanguagesAndScores = answerLanguagesAndScores.map(row =>
        (
            row._1,
            row._2._1.map(lang => (lang, row._2._2))  
        )
    )
// .reduceByKey to combine lists of (lang,score) tuples for each user
// (l1++l2) concatenates the lists of (lang,score) tuples 
// .groupBy will group tuples by the first element (lang) and return a map
//      of key = lang and value = list of tuples (lang,score)
// .map returns another map. the key is the same, but the value is the sum 
//      of the second element (score) of the tuples
//      kv.map(_._2).sum will take the score of each tuple and sum them
// .toList changes map back to list of (lang,score) tuples 
var userScoresAcc = userLanguagesAndScores.reduceByKey( (l1,l2) => 
        (l1++l2).groupBy(_._1).map {
            case (key, kv) => (key, kv.map(_._2).sum)
        }.toList
    )

var languageUsersAndScores = userScoresAcc.flatMap {
        case (user, langScoreList) => langScoreList.map {
            case (lang,score) => (lang, (user,score))
        }
    }


// groupBy will group (lang, (user,score)) tuples by lang and return a map of
//      key = lang, value = iterable of (lang,(user,score)) tuples
var languageExperts = languageUsersAndScores.groupBy(_._1).map {
        case (lang, tupleList) => (lang, tupleList.map(_._2).toList.sortBy(_._2).reverse)
    }
// (user1, [(lang1,score1),...,(langN,scoreN)] -> [ (lang1, (user1,score1)) , ...., (langN, (user1,scoreN)) ]
//                                             -> [ (lang1, (user2,score1)) , ...., (langN, (user2,scoreN)) ]
// lang1, [(user1,score), ...,(userN,score)]
// ...
// langN, [(user1,score), ...,(userN,score)]










