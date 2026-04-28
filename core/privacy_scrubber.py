import re

class PrivacyScrubber:
    """
    On-Device Anonymization layer: A local Regex-based layer masks PII 
    such as 10-digit phone numbers, bank account numbers, and UPI IDs 
    before data leaves the device (or reaches the AI layer).
    """
    def __init__(self):
        self.patterns = {
            'phone': r'\b(?:\+?91[\-\s]?)?[6789]\d{9}\b',
            'upi': r'[a-zA-Z0-9.\-_]{2,256}@[a-zA-Z]{2,64}',
            'bank_account': r'\b\d{9,18}\b',
            'aadhaar': r'\b\d{4}\s?\d{4}\s?\d{4}\b'
        }

    def scrub(self, text: str) -> str:
        scrubbed_text = text
        for key, pattern in self.patterns.items():
            if key == 'phone':
                scrubbed_text = re.sub(pattern, '[MASKED_PHONE]', scrubbed_text)
            elif key == 'upi':
                scrubbed_text = re.sub(pattern, '[MASKED_UPI]', scrubbed_text)
            elif key == 'bank_account':
                scrubbed_text = re.sub(pattern, '[MASKED_BANK_ACC]', scrubbed_text)
            elif key == 'aadhaar':
                scrubbed_text = re.sub(pattern, '[MASKED_AADHAAR]', scrubbed_text)
        return scrubbed_text
