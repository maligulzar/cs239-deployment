// get all text in between <code>...</code> and the rest of the text
// do word count for both
// remove filler words

val pathToQuestions = "/Users/Daniel/IdeaProjects/anothertry/src/main/resources/posts_questions_small.json"
val pathToStopWords = "/Users/Daniel/Documents/cs239-deployment/assignments/Assignment2/stopwords.txt"

val additionalWords = List("")
val stopWords = spark.read.textFile(pathToStopWords).rdd.collect().toList 


val questions = spark.read.format("json").load(pathToQuestions).rdd

val questionsFiltered = questions.filter( row => 
    row.getAs("id") != null &&
    row.getAs("title") != null &&
    row.getAs("body") != null &&
    row.getAs("tags") != null)

val questionsText = questionsFiltered.map(row => 
        row.getAs("title").toString.toLowerCase + " " + row.getAs("body").toString.toLowerCase
    )

//  (?s) enables dotall mode which makes '.' match newline as well
// .*? the ? makes it match 0 or 1 of the preceding expression so that it doesnt capture everything between the first <code> and last </code>
val pattern = "(?s)(?<=(<code>)).*?(?=(</code>))".r
val codeWords = questionsText.flatMap( text => 
        pattern.findAllIn(text)
        .toList
        .mkString(" ")
        .split("[^\\w']+")
        .filter(!stopWords.contains(_))
    )

val pattern = "(?s) <code>.*?</code>".r
val naturalWords = questionsText.flatMap( text => 
        pattern.replaceAllIn(text,"")
        .split("[^\\w']+")
        .filter(!stopWords.contains(_))
    )

val codeWordsCount = codeWords.map( word => (word,1)).reduceByKey(_+_).collect.sortBy(_._2).reverse
val naturalWordsCount = naturalWords.map( word => (word,1)).reduceByKey(_+_).collect.sortBy(_._2).reverse









