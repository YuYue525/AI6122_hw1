import json

def load_data():
    f = open("../json_files/reviews_Musical_Instruments_5.json","r")
    Musical_Instruments = f.readlines()
    f.close()
    Musical_Instruments_jsons = {"a":213,"b":342}
    for index,each in enumerate(Musical_Instruments):
        if index == 0:
            if json.loads(each[1:-2])['asin'] not in list(Musical_Instruments_jsons.keys()):
                Musical_Instruments_jsons[json.loads(each[1:-2])['asin']]=[json.loads(each[1:-2])['reviewText']]
            else:
                Musical_Instruments_jsons[json.loads(each[1:-2])['asin']].append(json.loads(each[1:-2])['reviewText'])
        elif index == len(Musical_Instruments)-1:
            if json.loads(each[:-1])['asin'] not in list(Musical_Instruments_jsons.keys()):
                Musical_Instruments_jsons[json.loads(each[:-1])['asin']] = [json.loads(each[:-1])['reviewText']]
            else:
                Musical_Instruments_jsons[json.loads(each[:-1])['asin']].append(json.loads(each[:-1])['reviewText'])
        else:
            if json.loads(each[:-2])['asin'] not in list(Musical_Instruments_jsons.keys()):
                Musical_Instruments_jsons[json.loads(each[:-2])['asin']] = [json.loads(each[:-2])['reviewText']]
            else:
                Musical_Instruments_jsons[json.loads(each[:-2])['asin']].append(json.loads(each[:-2])['reviewText'])

    f = open("../json_files/reviews_Patio_Lawn_and_Garden_5.json","r")
    Patio_Lawn_and_Garden = f.readlines()
    f.close()
    Patio_Lawn_and_Garden_jsons = {"a":213,"b":342}
    for index,each in enumerate(Patio_Lawn_and_Garden):
        if index == 0:
            if json.loads(each[1:-2])['asin'] not in list(Patio_Lawn_and_Garden_jsons.keys()):
                Patio_Lawn_and_Garden_jsons[json.loads(each[1:-2])['asin']]=[json.loads(each[1:-2])['reviewText']]
            else:
                Patio_Lawn_and_Garden_jsons[json.loads(each[1:-2])['asin']].append(json.loads(each[1:-2])['reviewText'])
        elif index == len(Patio_Lawn_and_Garden)-1:
            if json.loads(each[:-1])['asin'] not in list(Patio_Lawn_and_Garden_jsons.keys()):
                Patio_Lawn_and_Garden_jsons[json.loads(each[:-1])['asin']] = [json.loads(each[:-1])['reviewText']]
            else:
                Patio_Lawn_and_Garden_jsons[json.loads(each[:-1])['asin']].append(json.loads(each[:-1])['reviewText'])
        else:
            if json.loads(each[:-2])['asin'] not in list(Patio_Lawn_and_Garden_jsons.keys()):
                Patio_Lawn_and_Garden_jsons[json.loads(each[:-2])['asin']] = [json.loads(each[:-2])['reviewText']]
            else:
                Patio_Lawn_and_Garden_jsons[json.loads(each[:-2])['asin']].append(json.loads(each[:-2])['reviewText'])
    return Musical_Instruments_jsons,Patio_Lawn_and_Garden_jsons