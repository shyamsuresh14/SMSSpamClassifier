from flask import Flask, request, jsonify
from nltk.stem import WordNetLemmatizer

app = Flask(__name__)

def lemmatize(sentence):
    lemmatizer = WordNetLemmatizer()
    sentence = " ".join([lemmatizer.lemmatize(word) for word in sentence.split(" ")])
    return sentence

@app.route('/lemmatize', methods=["GET"])
def lemmatizer():
    input_txt = request.args["text"]
    info_dict = {
        "Received": input_txt,
        "Result": lemmatize(input_txt)
    }
    return jsonify(info_dict)

@app.route("/")
def index():
    return "hello world!"

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0")
