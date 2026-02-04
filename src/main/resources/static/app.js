// =======================
// SpEL
// =======================

async function evaluateExpression() {
    const resultArea = document.getElementById("eval-result");
    resultArea.style.color = ""; // reset color

    const expression = document.getElementById("eval-expression").value;
    const jsonText = document.getElementById("eval-json").value;

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch (e) {
        resultArea.textContent = "Invalid JSON!";
        resultArea.style.color = "red";
        return;
    }

    try {
        const res = await fetch("/spel/evaluate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ expression, data })
        });

        const result = await res.json();
        resultArea.textContent = JSON.stringify(result, null, 2);
        resultArea.style.color = "#2e7d32"; // verde
    } catch (err) {
        resultArea.textContent = err;
        resultArea.style.color = "red";
    }
}

async function findExpression() {
    const resultArea = document.getElementById("find-result");
    resultArea.style.color = ""; // reset color

    const value = document.getElementById("find-value").value;
    const jsonText = document.getElementById("find-json").value;

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch (e) {
        resultArea.textContent = "Invalid JSON!";
        resultArea.style.color = "red";
        return;
    }

    if (!value || !value.trim()) {
        resultArea.textContent = "Value is empty!";
        resultArea.style.color = "red";
        return;
    }

    try {
        const res = await fetch("/spel/find", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ value, data })
        });

        const result = await res.json();
        resultArea.textContent = JSON.stringify(result, null, 2);
        resultArea.style.color = "#2e7d32"; // verde
    } catch (err) {
        resultArea.textContent = err;
        resultArea.style.color = "red";
    }
}

// =======================
// JSONPath
// =======================

async function evaluateJsonPath() {
    const resultArea = document.getElementById("jp-eval-result");
    resultArea.style.color = "";

    const path = document.getElementById("jp-eval-path").value;
    const jsonText = document.getElementById("jp-eval-json").value;

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch (e) {
        resultArea.textContent = "Invalid JSON!";
        resultArea.style.color = "red";
        return;
    }

    if (!path || !path.trim()) {
        resultArea.textContent = "JSONPath is empty!";
        resultArea.style.color = "red";
        return;
    }

    try {
        const res = await fetch("/jsonpath/evaluate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ path, data })
        });

        const result = await res.json();
        resultArea.textContent = JSON.stringify(result, null, 2);
        resultArea.style.color = "#2e7d32"; // verde
    } catch (err) {
        resultArea.textContent = err;
        resultArea.style.color = "red";
    }
}

async function findJsonPath() {
    const resultArea = document.getElementById("jp-find-result");
    resultArea.style.color = "";

    const value = document.getElementById("jp-find-value").value;
    const jsonText = document.getElementById("jp-find-json").value;

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch (e) {
        resultArea.textContent = "Invalid JSON!";
        resultArea.style.color = "red";
        return;
    }

    if (!value || !value.trim()) {
        resultArea.textContent = "Value is empty!";
        resultArea.style.color = "red";
        return;
    }

    try {
        const res = await fetch("/jsonpath/find-smart", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ value, data })
        });

        const result = await res.json();
        resultArea.textContent = JSON.stringify(result, null, 2);
        resultArea.style.color = "#2e7d32"; // verde
    } catch (err) {
        resultArea.textContent = err;
        resultArea.style.color = "red";
    }
}

// =======================
// Util
// =======================

function copy(btn) {
    const pre = btn.closest(".result-card").querySelector("pre");
    navigator.clipboard.writeText(pre.innerText);

    btn.textContent = "✅";
    setTimeout(() => (btn.textContent = "📋"), 1000);
}