class ToneAnalyzer:
    """
    Layer 2 (Linguistic Heuristics): Analyzes the tone for predatory patterns like 
    "Sense of Urgency" or "Threat of Service Cut".
    """
    def __init__(self):
        # In a full implementation, this could call an LLM like Llama-3 or OpenAI.
        # For the hackathon, we use keyword-based heuristics.
        self.urgency_keywords = ["urgent", "immediately", "today", "now", "tonight", "asap", "turant"]
        self.threat_keywords = ["cut", "block", "suspend", "police", "arrest", "fine", "penalty"]
        self.reward_keywords = ["won", "lottery", "prize", "free", "claim", "reward"]

    def analyze(self, text: str):
        text_lower = text.lower()
        
        urgency_score = sum(1 for word in self.urgency_keywords if word in text_lower)
        threat_score = sum(1 for word in self.threat_keywords if word in text_lower)
        reward_score = sum(1 for word in self.reward_keywords if word in text_lower)

        is_predatory = False
        reasons = []

        if urgency_score > 0:
            reasons.append("High sense of urgency detected.")
            is_predatory = True
        
        if threat_score > 0:
            reasons.append("Threatening language (e.g., service cutoff, blocking) detected.")
            is_predatory = True
            
        if reward_score > 0:
            reasons.append("Unsolicited reward or lottery claim detected.")
            is_predatory = True

        return {
            "is_predatory": is_predatory,
            "reasons": reasons
        }
