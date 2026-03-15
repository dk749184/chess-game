# Multimodal Question-Option Analysis Model (Hindi Guide)

Ye guide ek aise AI system ke liye hai jisme user PDF, DOC/DOCX, ya image upload kare aur system:
1. Question detect kare,
2. Options (A/B/C/D ya similar) detect kare,
3. Analysis de,
4. Fixed format me output kare.

## 1) End-to-End Architecture

1. **Input Layer**
   - Supported formats: `.pdf`, `.docx`, `.doc`, `.png`, `.jpg`, `.jpeg`.
   - Har file ko unique `document_id` assign karo.

2. **Extraction Layer**
   - PDF text extraction: `pdfplumber` / `pymupdf`.
   - DOCX extraction: `python-docx`.
   - Image OCR: `Tesseract` ya cloud OCR (Google Vision/Azure OCR).
   - Agar scan PDF hai, to OCR fallback use karo.

3. **Normalization Layer**
   - Unicode normalize karo.
   - Extra whitespace, page headers/footers remove karo.
   - Option markers standardize karo: `(A)`, `A.`, `1)` etc.

4. **Question-Option Parser**
   - Rule-based regex + LLM hybrid best rahega.
   - Regex se candidate question blocks nikalo.
   - LLM se validate karao ki kya block valid MCQ hai.

5. **Analysis Engine**
   - LLM ko prompt do:
     - question text
     - options
     - expected language
     - output JSON schema
   - Output: explanation + confidence + final answer.

6. **Formatter Layer**
   - Fixed JSON response.
   - Frontend ko tabular/card format me render karne ke liye standard fields do.

## 2) Recommended Tech Stack

- **Backend**: FastAPI (Python)
- **Model APIs**: OpenAI/Anthropic/Gemini (LLM + vision capabilities)
- **OCR**: Tesseract (low-cost) ya cloud OCR (high accuracy)
- **Storage**: PostgreSQL + object storage (S3 compatible)
- **Queue**: Celery/RQ (heavy files ke liye async processing)

## 3) Output Format (Stable JSON Schema)

```json
{
  "document_id": "doc_123",
  "language": "hi",
  "questions": [
    {
      "question_id": "q1",
      "question_text": "...",
      "options": [
        {"label": "A", "text": "..."},
        {"label": "B", "text": "..."},
        {"label": "C", "text": "..."},
        {"label": "D", "text": "..."}
      ],
      "predicted_answer": "B",
      "explanation": "...",
      "confidence": 0.87,
      "source_page": 2
    }
  ]
}
```

## 4) Parsing Strategy (Practical)

### Step A: Block Detection
- Text ko paragraph chunks me split karo.
- Patterns detect karo:
  - `Q1`, `1.`, `प्रश्न 1`, `Question 1`
  - options: `A)`, `B)`, `C)`, `D)` etc.

### Step B: Candidate Validation
- Har chunk par lightweight classifier run karo:
  - Kya ye actual question hai?
  - Kya minimum 2 options hain?

### Step C: LLM Structuring
- LLM se bolo: “Given raw text, return strict JSON only.”
- Invalid JSON par automatic retry + repair parser lagao.

## 5) Prompt Design Example

- **System Prompt**:
  - “You are an exam content parser. Extract MCQ questions and options exactly. Do not hallucinate.”
- **User Prompt**:
  - Raw extracted chunk + required JSON schema.

## 6) Accuracy Improvement Tips

1. OCR quality improve karne ke liye image pre-processing karo:
   - denoise,
   - contrast boost,
   - skew correction.
2. Multi-pass extraction:
   - first pass regex,
   - second pass LLM correction.
3. Human-in-the-loop mode:
   - low confidence (`<0.65`) items manual review queue me bhejo.

## 7) Security and Compliance

- File upload size limit set karo.
- Malware scan karo.
- PII redaction optional banao.
- Audit logs maintain karo (kis file se kya extract hua).

## 8) MVP Roadmap

1. PDF + image support
2. OCR + regex parser
3. LLM-based JSON formatter
4. Basic dashboard with download (JSON/CSV)
5. Confidence filtering and manual correction UI

## 9) User Workflow (Simple)

1. User file upload kare.
2. System text extract kare.
3. System question-options detect kare.
4. System analysis generate kare.
5. User ko final arranged format me result mile (table + JSON export).

---

Agar aap chahein to next step me isi guide ka **working FastAPI boilerplate** bhi banaya ja sakta hai jisme upload endpoint, OCR pipeline aur JSON response ready ho.
