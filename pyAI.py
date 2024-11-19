from flask import Flask, request, jsonify
import requests
import json
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

THIRD_PARTY_API_URL = "https://dify.aipfuture.com/v1/chat-messages"
API_KEY = "Bearer app-gkKREgep8UE0jMp9IVltbml7"  # 你的实际密钥

@app.route('/api/chat', methods=['POST'])
def chat_with_api():
    data = request.json
    if not data or 'query' not in data:
        return jsonify({"error": "Invalid request, 'query' is required"}), 400

    # 打印接收到的请求数据，便于调试
    print("Received data:", data)

    payload = {
        "query": data['query'],
        "inputs": data.get('inputs', {}),
        "response_mode": "blocking",
        "user": data.get('user', 'anonymous'),
        "auto_generate_name": data.get('auto_generate_name', True)
    }

    headers = {
        "Content-Type": "application/json",
        "Authorization": API_KEY
    }

    try:
        # 发送请求到第三方 API
        response = requests.post(THIRD_PARTY_API_URL, json=payload, headers=headers)
        response.raise_for_status()  # 检查 HTTP 请求是否成功

        # 打印第三方 API 返回的响应数据
        print("Third-party API response:", response.json())

        return jsonify(response.json())

    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True)
