function simulateMessage(text) {
    const chatArea = document.getElementById('chatArea');
    const loadingSpinner = document.getElementById('loadingSpinner');
    const loadingText = document.getElementById('loadingText');
    const overlay = document.getElementById('eikosOverlay');
    
    // Ensure overlay is hidden initially
    overlay.classList.remove('active');

    // Create and append the new message bubble
    const msgDiv = document.createElement('div');
    msgDiv.className = 'message msg-received';
    msgDiv.textContent = text;
    chatArea.appendChild(msgDiv);
    chatArea.scrollTop = chatArea.scrollHeight;

    // Show parsing animation
    loadingSpinner.style.display = 'block';
    loadingText.style.display = 'block';

    // Simulate accessibility service triggering after rendering the UI node
    setTimeout(() => {
        analyzeWithEikos(text);
    }, 600);
}

async function analyzeWithEikos(message) {
    try {
        const response = await fetch('/api/analyze', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: message })
        });

        const data = await response.json();
        
        // Hide loader
        document.getElementById('loadingSpinner').style.display = 'none';
        document.getElementById('loadingText').style.display = 'none';

        if (data.is_threat) {
            showOverlay(data);
        } else {
            console.log("EIKOS: Message deemed safe.");
        }
    } catch (error) {
        console.error("Error connecting to EIKOS backend:", error);
        document.getElementById('loadingSpinner').style.display = 'none';
        document.getElementById('loadingText').style.display = 'none';
    }
}

function showOverlay(data) {
    const overlay = document.getElementById('eikosOverlay');
    const reasonList = document.getElementById('reasonList');
    const ragConfidence = document.getElementById('ragConfidence');
    const latencyStat = document.getElementById('latencyStat');

    // Clear previous reasons
    reasonList.innerHTML = '';

    // Populate reasons
    data.reasons.forEach(reason => {
        const li = document.createElement('li');
        li.className = 'reason-item';
        li.textContent = reason;
        reasonList.appendChild(li);
    });

    // Populate stats
    ragConfidence.textContent = data.rag_confidence + '%';
    latencyStat.textContent = (data.latency_seconds * 1000).toFixed(0) + ' ms';

    // Slide in overlay
    overlay.classList.add('active');
}

function dismissOverlay(action) {
    const overlay = document.getElementById('eikosOverlay');
    overlay.classList.remove('active');
    
    if (action === 'block') {
        const chatArea = document.getElementById('chatArea');
        const blockMsg = document.createElement('div');
        blockMsg.style.textAlign = 'center';
        blockMsg.style.color = 'var(--accent-red)';
        blockMsg.style.fontSize = '0.8rem';
        blockMsg.style.marginTop = '10px';
        blockMsg.textContent = 'Contact blocked by EIKOS.';
        chatArea.appendChild(blockMsg);
        chatArea.scrollTop = chatArea.scrollHeight;
    }
}
