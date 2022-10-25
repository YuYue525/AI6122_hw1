import textrank
import data_loader
import argparse

def get_args_parser():
    parser = argparse.ArgumentParser('A Review_Summarizer.', add_help=False)
    parser.add_argument('--category', default='', type=str)
    parser.add_argument('--product_id', default='', type=str)
    return parser

def Summary(category,product_id):
    Musical_Instruments_jsons,Patio_Lawn_and_Garden_jsons = data_loader.load_data()
    if category == 'Musical_Instruments':
        if product_id in list(Musical_Instruments_jsons.keys()):
            return textrank.get_summary(Musical_Instruments_jsons[product_id])
        else:
            return "Wrong product id!"
    elif category == 'Patio_Lawn_and_Garden':
        if product_id in list(Patio_Lawn_and_Garden_jsons.keys()):
            return textrank.get_summary(Patio_Lawn_and_Garden_jsons[product_id])
        else:
            return "Wrong product id!"
    else:
        return "Wrong category! --category ['Musical_Instruments','Patio_Lawn_and_Garden']"

if __name__ == '__main__':
    parser = argparse.ArgumentParser('A Review_Summarizer.', parents=[get_args_parser()])
    args = parser.parse_args()
    print("The review summary of product "+args.product_id+" is:"+Summary(args.category,args.product_id))
