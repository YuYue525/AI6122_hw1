import json
import gzip
import os
import random
import nltk
from nltk.tokenize import word_tokenize, sent_tokenize
from nltk.stem import PorterStemmer
import spacy
import matplotlib.pyplot as plt

## spaCy model
nlp = spacy.load("en_core_web_sm")

## downloaded .json.gz files are in gz_files_dir
## transform .json.gz to .json and save in json_files_dir
gz_files_dir = "./gz_files"
json_files_dir = "./json_files"

gz_file_names = os.listdir(gz_files_dir)
json_file_names = [x[:-3] for x in gz_file_names]


#-----Dataset Download and Sampling-----#

## .json.gz -> .json
## generate strict json files from the downloaded files
def generate_json(in_path, out_path):
    def parse(path):
        g = gzip.open(path, 'r')
        for l in g:
            yield json.dumps(eval(l))

    f = open(out_path, 'w')
    flag_first = True
    for l in parse(in_path):
        if flag_first:
            f.write('[' + l)
            flag_first = False
        else:
            f.write(',\n' + l)
    f.write("]")
    f.close()

## file_path -> list
## read json file and return the result list of reviews
def read_json(in_path):
    with open(in_path, 'r') as f:
        reviews = json.load(f)
    return reviews
    
## reviews list -> list with 200 product asins
## randomly select 200 products from the review data
def random_select_products(reviews_list):
    products = set()
    for review in reviews_list:
        products.add(review["asin"])
    return random.sample(list(products), 200)


#-----POS Tagging-----#

## NLTK POS
## sentence text -> list POS sentence
def nltk_pos(text):
    tokens = word_tokenize(text)
    return nltk.pos_tag(tokens)
    
## spaCy POS
## sentence text -> list POS sentence
def spacy_pos(text):
    doc = nlp(text)
    return [(token.text, token.pos_) for token in doc]
    

#-----Sentence Segmentation-----#

## NLTK sentence tokenization
## text -> list of sentences
def nltk_sent_tokenize(text):
    return sent_tokenize(text)
    
## spacy sentence tokenization
## text -> list of sentences
def spacy_sent_tokenize(text):
    doc = nlp(text)
    return [x.text for x in doc.sents]
    
## return the number of sentences in a review
## review text, sent tokenize fun -> num of sentences
def count_sent(review_text, tokenize_fun):
    return len(tokenize_fun(review_text))
   
## plot distributions of the sentence numbers of reviews in two datasets
def plot_sent_num_distribution(datasets, tokenize_fun):
    sent_num = []
    
    for name, dataset in datasets.items():
        sent_num_tmp = []
        
        for data in dataset:
            sent_num_tmp.append(count_sent(data['reviewText'], tokenize_fun))
        
        sent_num.append(sent_num_tmp)
        
    max_num = max(sent_num[0] + sent_num[1])
        
    plt.hist(sent_num[0], bins = max_num, color = "red", alpha = 0.9)
    plt.hist(sent_num[1], bins = max_num, color = "blue", alpha = 0.5)
    plt.xlim(0, max_num)
    
    plt.title("distribution of sentence number in two datasets")
    plt.xlabel("length of a review in number of sentences")
    plt.ylabel("number of reviews")
    plt.savefig("sentence_distribution.png")


#-----Tokenization and Stemming-----#

### word tokenization

## nltk word tokenization
## text -> list of tokens
def nltk_word_tokenize(text):
    return word_tokenize(text)
    
## spacy word tokenization
## text -> list of tokens
def spacy_word_tokenize(text):
    doc = nlp(text)
    return [x.text for x in doc]
    

def count_token(review_text, tokenize_fun):
    return len(tokenize_fun(review_text))
    
## plot distributions of the token numbers of reviews in two datasets
def plot_token_num_distribution(datasets, tokenize_fun):
    token_num = []
    
    for name, dataset in datasets.items():
        token_num_tmp = []
        
        for data in dataset:
            token_num_tmp.append(count_token(data['reviewText'], tokenize_fun))
        
        token_num.append(token_num_tmp)
        
    max_num = max(token_num[0] + token_num[1])
        
    plt.hist(token_num[0], bins = max_num // 5, color = "red", alpha = 0.9)
    plt.hist(token_num[1], bins = max_num // 5, color = "blue", alpha = 0.5)
    plt.xlim(0, max_num)
    
    plt.title("distribution of token number in two datasets")
    plt.xlabel("length of a review in number of tokens")
    plt.ylabel("number of reviews")
    plt.savefig("token_distribution.png")
    
### Stemming

## NLTK stemming
## text -> list of stemming results
def nltk_stem(text):
    ps = PorterStemmer()
    return [ps.stem(x) for x in nltk_word_tokenize(text)]
    
## spacy stemming
## text -> list of stemming results
# def spacy_stem(text):
    
    
## NLTK stemming

if __name__ == '__main__':
    
    datasets = {}
    
    ## for each file in the download .json.gz files
    for i in range(len(gz_file_names)):
        
        ## generate strict json files from .json.gz download files
        # generate_json(os.path.join(gz_files_dir, gz_file_names[i]), os.path.join(json_files_dir, json_file_names[i]))
        
        ## read json files and store the reviews in a list
        reviews = read_json(os.path.join(json_files_dir, json_file_names[i]))
        # print(reviews[0])
        
        ## randomly select 200 products and store asins in a list
        sampled_products_asin = random_select_products(reviews)
        
        ## the reviews on the selected products
        sampled_reviews = [ x for x in reviews if x["asin"] in sampled_products_asin]
        ## add to datasets dict
        datasets[json_file_names[i]] = sampled_reviews
    
    ## pos tagging
    # test_sentence = "The quick brown fox jumps over the lazy dog"
    # print(nltk_pos(test_sentence))
    # print(spacy_pos(test_sentence))
    
    ## randomly select five sentences from two datasets
    
    ## create a list storing all sentences from two sampled datasets
    all_sentences = []
    for name, dataset in datasets.items():
        sampled_dataset = random.sample(dataset, 5)
        for data in sampled_dataset:
            all_sentences += nltk_sent_tokenize(data["reviewText"])
            #all_sentences += spacy_sent_tokenize(data["reviewText"])
            
    sampled_sentences = random.sample(all_sentences, 5)
    
    ## POS tagging of five sampled sentences
    for i in range(len(sampled_sentences)):
        print("sentence", i+1, ":", sampled_sentences[i])
        print("NLTK POS:", nltk_pos(sampled_sentences[i]))
        print("spaCy POS:", spacy_pos(sampled_sentences[i]))
        
    plot_sent_num_distribution(datasets, nltk_sent_tokenize)
    plot_token_num_distribution(datasets, nltk_word_tokenize)
    
    
    
        
    
        
    
        
    
        
        
        
