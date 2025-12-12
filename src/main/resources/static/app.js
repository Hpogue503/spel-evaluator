function formatJSON(textareaId) {
    const area = document.getElementById(textareaId);
    try {
        const parsed = JSON.parse(area.value);
        area.value = JSON.stringify(parsed, null, 2);
    } catch(e) {
        // Si no es JSON válido, no formatea
    }
}

// Formatea al pegar
document.getElementById("eval-json").addEventListener("input", () => formatJSON("eval-json"));
document.getElementById("find-json").addEventListener("input", () => formatJSON("find-json"));

async function evaluateExpression() {
    const expression = document.getElementById("eval-expression").value.trim();
    const jsonText = document.getElementById("eval-json").value.trim();
    const resultArea = document.getElementById("eval-result");

    if (!jsonText) {
        resultArea.textContent = "JSON is empty. Please enter a valid JSON object.";
        resultArea.style.color = "red";
        return;
    }
    if (!expression) {
        resultArea.textContent = "Expression is empty. Please enter a valid SpEL expression.";
        resultArea.style.color = "red";
        return;
    }

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch (e) {
        resultArea.textContent = "Invalid JSON. Please check your syntax.";
        resultArea.style.color = "red";
        return;
    }

    try {
        const res = await fetch("/evaluate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ expression, data })
        });
        const json = await res.json();

        if (json.error) {
            resultArea.textContent = `Error: ${json.error}`;
            resultArea.style.color = "red";
        } else if (json.result === null || json.result === undefined) {
            resultArea.textContent = "Expression not found in JSON. Try a safe path or check your keys.";
            resultArea.style.color = "red";
        } else {
            resultArea.textContent = JSON.stringify(json.result, null, 2);
            resultArea.style.color = "green";
        }
    } catch(err) {
        resultArea.textContent = `Unexpected error: ${err}`;
        resultArea.style.color = "red";
    }
}

async function findExpression() {
    const value = document.getElementById("find-value").value.trim();
    const jsonText = document.getElementById("find-json").value.trim();
    const resultArea = document.getElementById("find-result");

    if (!jsonText) {
        resultArea.textContent = "JSON is empty. Please enter a valid JSON object.";
        resultArea.style.color = "red";
        return;
    }
    if (!value) {
        resultArea.textContent = "Value is empty. Please enter a value to find.";
        resultArea.style.color = "red";
        return;
    }

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch(e) {
        resultArea.textContent = "Invalid JSON. Please check your syntax.";
        resultArea.style.color = "red";
        return;
    }

    try {
        const res = await fetch("/evaluate/find", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ value, data })
        });
        const json = await res.json();

        if (json.error) {
            resultArea.textContent = `Error: ${json.error}`;
            resultArea.style.color = "red";
        } else if (!json.results || json.results.length === 0) {
            resultArea.textContent = "Value not found in JSON.";
            resultArea.style.color = "red";
        } else {
            resultArea.textContent = JSON.stringify(json.results, null, 2);
            resultArea.style.color = "green";
        }
    } catch(err) {
        resultArea.textContent = `Unexpected error: ${err}`;
        resultArea.style.color = "red";
    }
}