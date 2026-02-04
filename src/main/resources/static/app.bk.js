async function evaluateExpression() {
    const expression = document.getElementById("eval-expression").value;
    const jsonText = document.getElementById("eval-json").value;
    let data;
    try {
        data = JSON.parse(jsonText);
    } catch(e) {
        document.getElementById("eval-result").textContent = "Invalid JSON!";
        return;
    }
    try {
        const res = await fetch("/evaluate", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ expression, data })
        });
        const result = await res.json();
        document.getElementById("eval-result").textContent = JSON.stringify(result, null, 2);
    } catch(err) {
        document.getElementById("eval-result").textContent = err;
    }
}

async function findExpression() {
    const value = document.getElementById("find-value").value;
    const jsonText = document.getElementById("find-json").value;

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch(e) {
        document.getElementById("find-result").textContent = "Invalid JSON!";
        return;
    }

    try {
        const res = await fetch("/evaluate/find", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ value, data })
        });
        const result = await res.json();
        document.getElementById("find-result").textContent = JSON.stringify(result, null, 2);
    } catch(err) {
        document.getElementById("find-result").textContent = err;
    }

}

async function evaluateJsonPath() {
    const path = document.getElementById("jp-eval-path").value;
    const jsonText = document.getElementById("jp-eval-json").value;

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch (e) {
        document.getElementById("jp-eval-result").textContent = "Invalid JSON!";
        return;
    }

    if (!path || !path.trim()) {
        document.getElementById("jp-eval-result").textContent = "JSONPath is empty!";
        return;
    }

    try {
        const res = await fetch("/evaluate/jsonpath", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ path, data })
        });

        const result = await res.json();
        document.getElementById("jp-eval-result").textContent =
            JSON.stringify(result, null, 2);
    } catch (err) {
        document.getElementById("jp-eval-result").textContent = err;
    }
}

async function findJsonPath() {
    const value = document.getElementById("jp-find-value").value;
    const jsonText = document.getElementById("jp-find-json").value;

    let data;
    try {
        data = JSON.parse(jsonText);
    } catch {
        document.getElementById("jp-find-result").textContent = "Invalid JSON!";
        return;
    }

    if (!value.trim()) {
        document.getElementById("jp-find-result").textContent = "Value is empty!";
        return;
    }

    const res = await fetch("/evaluate/jsonpath/find-smart", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ value, data })
    });

    const result = await res.json();
    document.getElementById("jp-find-result").textContent =
        JSON.stringify(result, null, 2);
}

/// PRUEBA :

function copy(btn) {
    const pre = btn.closest('.result-card').querySelector('pre');
    navigator.clipboard.writeText(pre.innerText);

    btn.textContent = "✅";
    setTimeout(() => btn.textContent = "📋", 1000);
}