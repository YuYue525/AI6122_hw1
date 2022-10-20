# Simple Search Engine

## Achieved:

### index the dataset
* case folding (StandardAnalyzer())
* case folding + stopwords (StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet()))
* case folding + stopwords + stemming (EnglishAnalyzer())

### Search Engine
* term query on [asin/reviewText/reviewerName/reviewerID/summary].
* phrase query on [reviewText/reviewerName/summary].
* range search on [overall].
* other high-level query on [reviewText/reviewerName/summary].
* multiple field searching.

## Need to consider in the report:

* inndexing time
* Search time

## Java compile and run:

* javac -d . -classpath "./lib/*" ./src/LuceneIndexWriter.java ./src/SearchEngine.java
* java -classpath "./:./lib/*" searchengine.LuceneIndexWriter
* java -classpath "./:./lib/*" searchengine.SearchEngine
