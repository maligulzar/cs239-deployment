val path = "/Users/Daniel/IdeaProjects/anothertry/src/main/resources/posts_questions_small.json"
val languages = List("python","java","scala","c","c++")

val jsonrdd = spark.read.format("json").load(path).rdd

// turn each row into an array of strings 
val text = jsonrdd.map(row => 
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

// results: Array[(String, Int)] = Array((c++,53), (python,41), (java,66), (c,221))