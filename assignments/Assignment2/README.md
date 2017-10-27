# Question 1: What languages are mentioned most in Stack Overflow questions? 
## Given a list of languages, what are the frequencies of each language? Only count a language once per question. 
 Only examine the "title", "body", and "tags" fields for each question. Ignore capitalization. Only look for words that are made of the following characters: [A-Za-z0-9_+]. 'scala+' would be recognized as 'scala+'. 'scala'd' would be recognized as 'scala' and 'd'.

 Current languages: python, java, scala, c, c++

Testing on the sample data (posts_questions_sample.json) should give the following results:
```
Res: Array[(String, Int)] = Array(("scala",2), ("c++",3), ("python",3), ("java",1), ("c",1))
```

# Question 2: What are the most frequent words in the natural language text and the code sections?
## Report the five most common words in the natural language text and the code, respectively.
The code sections are everything in between code tags eg `<code> This is code </code>`

Natural language is the text outside of the code tags and that is not part of a tag eg `<a ignore this> but not this </a>`

Extract words using `"[^\\w']+"` to split text. 

Ignore all words in *stopwords.txt*


# Question 3: Who are the experts in each language?
## Report the top two experts in each language. Expertise is based on the score of their answer to a question that mentions the language.

If there is the following question: `How do I reverse a list in Python?` and there is the following answer: `myList[::-1]` by a user with the id: `1` and the answer has a score: `613` then user `1` would have a score of `613` for `Python`. Add up all the score of all their answers to questions that mention `Python`.

Testing on the sample data (*posts_questions_sample.json* and *posts_answers_sample.json*) would give the results:
```
    ("scala", List((11, 100), (12, 30))), 
    ("c++", List((11, 120), (12, 90))),
    ("python", List((11, 100), (13, 95))), 
    ("java", List((13, 90))), 
    ("c", List((12, 60), (11, 20)))
```
where each tuple is a `(user_id, total_score)`

Find words in the same manner as in Question 1.

