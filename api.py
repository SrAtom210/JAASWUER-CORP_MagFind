from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline
import torch
import re
import requests

app = FastAPI()

# Modelo BETO para clasificación zero-shot
modelo = pipeline(
    "zero-shot-classification",
    model="Recognai/bert-base-spanish-wwm-cased-xnli",
    device=0 if torch.cuda.is_available() else -1
)

OCI_ENDPOINT = "https://gf8cee49287ea17-magfindgps.adb.us-ashburn-1.oraclecloudapps.com/ords/admin/"

class EmailRequest(BaseModel):
    texto: str

class ClasificacionResponse(BaseModel):
    keyword: str
    category: str
    confidence: float

def get_categorias():
    """Llama a tu API OCI para obtener categorías (predefinidas + personalizadas)."""
    try:
        resp = requests.get(OCI_ENDPOINT)
        if resp.status_code == 200:
            data = resp.json()
            return [c["nombre"] for c in data.get("items", [])]
    except:
        pass
    return ["escuela", "trabajo", "personal", "spam", "otros"]

def extraer_keyword(texto: str):
    palabras = re.findall(r"\b\w+\b", texto.lower())
    return max(set(palabras), key=palabras.count) if palabras else "indefinido"

@app.post("/clasificar", response_model=ClasificacionResponse)
def clasificar_email(request: EmailRequest):
    categorias = get_categorias()
    resultado = modelo(
        request.texto,
        candidate_labels=categorias,
        hypothesis_template="Este texto está relacionado con {}."
    )
    categoria_pred = resultado["labels"][0]
    confianza = float(resultado["scores"][0])
    keyword = extraer_keyword(request.texto)

    return ClasificacionResponse(
        keyword=keyword,
        category=categoria_pred,
        confidence=round(confianza, 2)
    )