import nltk
from textblob import TextBlob
import numpy as np
import json
import pandas as pd
from PIL import Image
import collections
import matplotlib.pyplot as plt
from nltk.sentiment.vader import SentimentIntensityAnalyzer
from nltk.sentiment.util import *
from nltk.tokenize import sent_tokenize, word_tokenize
from nltk.corpus import stopwords
from wordcloud import WordCloud


Musical_Instruments_file = "../json_files/reviews_Musical_Instruments_5.json"
Patio_Lawn_and_Garden_file = "../json_files/reviews_Patio_Lawn_and_Garden_5.json"

stop_words = set(stopwords.words("english"))
sid = SentimentIntensityAnalyzer()

background_Image_MI = np.array(Image.open('Music_instruments_background.jpg'))
background_Image_PLG = np.array(Image.open('Patio_Lawn_and_Garden_background.jpg'))


def import_reviews(product_type="Music instruments", target_asin=None):
    file_path = './'
    if product_type == "Music instruments":
        file_path = Musical_Instruments_file
    else:
        file_path = Patio_Lawn_and_Garden_file
    review_dataset = []
    f = open(file_path, "r")
    dataset = json.load(f)
    if target_asin is None:
        for i in dataset:
            review_dataset.append(i['reviewText'])
        f.close()
    else:
        for i in dataset:
            if i['asin'] in target_asin:
                review_dataset.append(i['reviewText'])
        f.close()
    return review_dataset


# Generate a pie chart summary of required product reviews' sentiments
# can work on certain products with target asin numbers
def sentiment_analysis(product_type="Music instruments", target_asin=None):
    review_set = import_reviews(product_type, target_asin)
    neg_count, positive_count, neu_count = 0, 0, 0
    sentiment_score_list = []
    tag_list = []
    for review in review_set:
        tag = ''
        obj = TextBlob(review)
        sentiment_score = obj.sentiment.polarity
        if sentiment_score < -0.0:
            tag = 'Negative'
            neg_count += 1
        elif sentiment_score > 0.0:
            tag = 'Positive'
            positive_count += 1
        else:
            tag = 'Neutral'
            neu_count += 1
        sentiment_score_list.append(sentiment_score)
        tag_list.append(tag)
    tag_collect = dict(collections.Counter(tag_list))
    plt.pie(list(tag_collect.values()), labels=(list(tag_collect.keys())))
    plt.title(product_type + 'Review sentiment pie chart')
    plt.savefig(product_type + 'Review sentiment pie chart.png')
    plt.show()


# Draw wordCloud with positive words and negative words respectively over the required product review
# can work on certain products with target asin numbers
def word_cloud_for_target_asin_product(product_type="Music instruments", target_asin=None, title="00"):
    review_set = import_reviews(product_type, target_asin)

    all_words = []
    for review in review_set:
        review_re = re.sub(r'[^\w\s]', '', review)
        words = word_tokenize(review_re)
        all_words += words
    # print(all_words)
    filtered_sentences = [w for w in all_words if w not in stop_words]
    # print(filtered_sentences)
    pos_word_list = []
    neu_word_list = []
    neg_word_list = []

    for word in filtered_sentences:
        if (sid.polarity_scores(word)['compound']) >= 0.5:
            pos_word_list.append(word)
        elif (sid.polarity_scores(word)['compound']) <= -0.5:
            neg_word_list.append(word)
        else:
            neu_word_list.append(word)
    print('Top 10 frequent Positive words:', list(nltk.FreqDist(pos_word_list).keys())[:10])
    print('Top 10 frequent Negative words:', list(nltk.FreqDist(neg_word_list).keys())[:10])

    word_counts_pos = dict(collections.Counter(pos_word_list))
    word_counts_neg = dict(collections.Counter(neg_word_list))

    background_image = background_Image_MI if product_type == "Music instruments" else background_Image_PLG

    wordcloud_pos = WordCloud(background_color='white', stopwords=stopwords,
                              mask=background_image,
                              max_words=150, max_font_size=60,
                              scale=3, random_state=1).generate_from_frequencies(word_counts_pos)
    wordcloud_pos.to_file(product_type + "pos_wordcloud" + title + ".png")
    plt.imshow(wordcloud_pos)
    plt.axis('off')
    plt.show()

    wordcloud_neg = WordCloud(background_color='white', stopwords=stopwords,
                              mask=background_image,
                              max_words=150, max_font_size=60,
                              scale=3, random_state=1).generate_from_frequencies(word_counts_neg)
    wordcloud_neg.to_file(product_type + "neg_wordcloud" + title + ".png")
    plt.imshow(wordcloud_neg)
    plt.axis('off')
    plt.show()


if __name__ == "__main__":
    sentiment_analysis()
    sentiment_analysis("Patio_Lawn_and_Garden")
    word_cloud_for_target_asin_product()
    word_cloud_for_target_asin_product(product_type="Patio_Lawn_and_Garden", title="01")





























