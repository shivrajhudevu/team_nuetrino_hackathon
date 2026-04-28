class RAGEngine:
    """
    Hybrid Verification Engine (RAG) - Mock Implementation for Hackathon MVP
    Uses a simple heuristic matching instead of ChromaDB to avoid heavy ML dependencies.
    """
    def __init__(self):
        self._seed_database()

    def _seed_database(self):
        # Known scam patterns
        self.scams = [
            "your electricity connection will be cut at 9 pm tonight. please pay the bill immediately on this link",
            "dear customer, your bank account will be suspended. please update your kyc immediately",
            "congratulations! you have won 10 lakh rupees. send your account number and pin to claim your prize",
            "your debit card is blocked. tell me the otp to unblock it",
            "urgently send 5000 rs, i am in the hospital",
            "you have received a parcel but customs fee is required. click here to pay"
        ]

    def verify_message(self, text: str):
        text_lower = text.lower()
        
        best_match = None
        highest_confidence = 0
        
        # Simple word overlap similarity
        words1 = set(text_lower.split())
        
        for scam in self.scams:
            words2 = set(scam.split())
            intersection = words1.intersection(words2)
            union = words1.union(words2)
            if not union:
                continue
            
            # Jaccard similarity
            similarity = len(intersection) / len(union)
            confidence = int(similarity * 100)
            
            # Boost confidence for specific keywords
            if ("cut" in text_lower and "electricity" in text_lower) or \
               ("otp" in text_lower and "block" in text_lower) or \
               ("won" in text_lower and "prize" in text_lower) or \
               ("hospital" in text_lower and "urgent" in text_lower):
                confidence += 40
                
            confidence = min(100, confidence)
                
            if confidence > highest_confidence:
                highest_confidence = confidence
                best_match = scam
        
        is_scam = highest_confidence > 45
        matches = []
        
        if is_scam and best_match:
            matches.append({
                "matched_text": best_match,
                "type": "scam",
                "confidence": highest_confidence
            })
        
        return {
            "is_scam_match": is_scam,
            "confidence": highest_confidence,
            "matches": matches
        }
