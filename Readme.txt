Please download all the code from https://github.com/YuYue525/AI6122_hw1
Dataset download link: https://jmcauley.ucsd.edu/data/amazon/
Lucene package: https://lucene.apache.org/
Json-simle package: https://github.com/fangyidong/json-simple

==========File Directory==========

SourceCode
├── Applications
│   ├── Music\ instrumentsReview\ sentiment\ pie\ chart.png
│   ├── Music\ instrumentsneg_wordcloud00.png
│   ├── Music\ instrumentspos_wordcloud00.png
│   ├── Music_instruments_background.jpg
│   ├── Patio_Lawn_and_GardenReview\ sentiment\ pie\ chart.png
│   ├── Patio_Lawn_and_Garden_background.jpg
│   ├── Patio_Lawn_and_Gardenneg_wordcloud01.png
│   ├── Patio_Lawn_and_Gardenpos_wordcloud01.png
│   └── Setiment_analysis.py
├── data_analysis
│   ├── data_analysis.py
│   └── plots
│       ├── reviews_Musical_Instruments_5_range_50.png
│       ├── reviews_Musical_Instruments_5_stem_range_50.png
│       ├── reviews_Patio_Lawn_and_Garden_5_range_50.png
│       ├── reviews_Patio_Lawn_and_Garden_5_stem_range_50.png
│       ├── sentence_distribution.png
│       └── token_distribution.png
├── environment.yml
├── gz_files
│   ├── reviews_Musical_Instruments_5.json.gz
│   └── reviews_Patio_Lawn_and_Garden_5.json.gz
├── json_files
│   ├── reviews_Musical_Instruments_5.json
│   └── reviews_Patio_Lawn_and_Garden_5.json
├── review_summarizer
│   ├── data_loader.py
│   ├── main.py
│   └── textrank.py
└── search_engine
    ├── README.md
    ├── data
    │   ├── reviews_Musical_Instruments_5.json
    │   └── reviews_Patio_Lawn_and_Garden_5.json
    ├── index
    │   ├── _5.cfe
    │   ├── _5.cfs
    │   ├── _5.si
    │   ├── segments_6
    │   └── write.lock
    ├── lib
    │   ├── json-simple-1.1.1.jar
    │   ├── lucene-analysis-common-9.3.0.jar
    │   ├── lucene-core-9.3.0.jar
    │   └── lucene-queryparser-9.3.0.jar
    ├── searchengine
    │   ├── LuceneIndexWriter.class
    │   └── SearchEngine.class
    └── src
        ├── LuceneIndexWriter.java
        └── SearchEngine.java

12 directories, 40 files

==========Environment Configuration==========

For Data Analysis, Review Summarizer and Application parts written by Python:

conda env create -f environment.yml

For Search Engine written by Java:

cd search_engine
javac -d . -classpath "./lib/*" ./src/LuceneIndexWriter.java ./src/SearchEngine.java
java -classpath "./:./lib/*" searchengine.LuceneIndexWriter
java -classpath "./:./lib/*" searchengine.SearchEngine

==========Data Analysis==========

Run the programe:

1. Enter the data_analysis folder in command line mode: 

	cd data_analysis

2. Run the data_analysis.py using the following command:
	
	python data_analysis.py

Description:

Input: The program read all the json files  under json_files folder
Output: In turn, the program output:
1. The number of the selected reviews with randomly selected 200 asin:

	reviews_Musical_Instruments_5.json 2272
	reviews_Patio_Lawn_and_Garden_5.json 2862

2. Five sentences with POS tags by using NLTK and spaCy
3. Sentence number distribution plots generated under the ./plots folder
4. Token number distribution plots generated under the ./plots folder
5. Token numbers before/after stemming:

	{'reviews_Musical_Instruments_5.json': 13984, 'reviews_Patio_Lawn_and_Garden_5.json': 22462}
	{'reviews_Musical_Instruments_5.json': 9305, 'reviews_Patio_Lawn_and_Garden_5.json': 14994}

6. Top 50 tokens with the most frequency before/after stemming
7. Top 10 indicative words of two datasets:

	[('guitar', 0.6294945882306086), ('mic', 0.15124409573285932), ('guitars', 0.14198118917364277), ('pedals', 0.10450229980864838), ('bass', 0.09090190552676371), ('acoustic', 0.08168344433166204), ('Fender', 0.06617963100864147), ('instrument', 0.0647277355471744), ('recording', 0.05795625998265069), ('microphone', 0.0531238652169475)]
	[('feeder', 0.23450230919176207), ('hose', 0.19424921543866427), ('garden', 0.14793224038063504), ('plant', 0.14179498306090182), ('seed', 0.10524463045384715), ('trap', 0.10196678467060946), ('soil', 0.09759781659008937), ('seeds', 0.08341137446702726), ('mice', 0.08232097372065054), ('bait', 0.06794071333056158)]

==========Search Engine==========

1. Enter the search_engine folder in command line mode: 

	cd search_engine

2. Compile the .java files with .jar packages:

	javac -d . -classpath "./lib/*" ./src/LuceneIndexWriter.java ./src/SearchEngine.java

3. Indexing:

	java -classpath "./:./lib/*" searchengine.LuceneIndexWriter

The output are the indexed .json file names and the indexing times:

	../data/reviews_Musical_Instruments_5.json
	../data/reviews_Patio_Lawn_and_Garden_5.json
	Indexing Time: 706 ms
	Indexing Time: 836 ms

4. Run the search engine:
	
	java -classpath "./:./lib/*" searchengine.SearchEngine

The output is the search engine operation console:

	=====Welcome to our Search Engine!=====

	Please choose your query type: [1/2/3/4/5/q]

	1. Type '1' for term query on [asin/reviewText/reviewerName/reviewerID/summary].
	2. Type '2' for phrase query on [reviewText/reviewerName/summary].
	3. Type '3' for range search on [overall].
	4. Type '4' for other high-level query on [reviewText/reviewerName/summary].
	5. Type '5' for multiple field searching.
	Type 'q' to quit.

	Your choice:

Then just type in according to the operation instruction printed.

==========Review Summarizer==========

1. Enter the review_summarizer folder in command line mode.
2. Run the summarizer using the following command:

	python main.py --category [CATEGORY] --product_id [ASIN]

where CATEGORY is the class of product. For example, "Musical_Instruments" or "Patio_Lawn_and_Garden". ASIN is the asin of a product in the dataset such as "B000068NW5":
	
	python main.py --category Musical_Instruments --product_id B000068NW5

The output is the summary of the reviews:

The original review of product B000068NW5 is: Good quality cable and sounds very good Zero issues with this cable so far.  It feels fairly cheap and light weight but it has survived for months of plugging in, unplugging, and packing between practice spaces.I'll update this review if/when it breaks...



==========Application==========

1. Enter the Applications folder in command line mode.
2. Run the application with 

	python Setiment_analysis.py.

The output is the top 10 frequent positive and negative words. And the word clouds and pie charts will be output into the current folder.

Top 10 frequent Positive words: ['bonus', 'pleasing', 'great', 'Love', 'wins', 'Perfect', 'best', 'love', 'perfect', 'kind']
Top 10 frequent Negative words: ['harm', 'hurt', 'failed', 'terribly', 'fail', 38, 'abused', 'dead', 'crappy', 'die', 'degradation']
