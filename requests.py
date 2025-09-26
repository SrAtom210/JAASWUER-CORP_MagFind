import os
import requests

API_URL = "https://router.huggingface.co/hf-inference/models/dccuchile/bert-base-spanish-wwm-cased"
headers = {
    "Authorization": f"Bearer {os.environ['HF_TOKEN']}",
}

def query(payload):
    response = requests.post(API_URL, headers=headers, json=payload)
    return response.json()

output = query({
    "inputs": "The answer to the universe is [MASK].",
})