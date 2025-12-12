async function evaluateExpression() {
    const expression = document.getElementById("eval-expression").value;
    const jsonText = document.getElementById("eval-json").value;
    let data;
    try {
        data = JSON.parse(jsonText);
    } catch(e) {
        document.getElementById("eval-result").textContent = "JSON inválido!";
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
        document.getElementById("find-result").textContent = "JSON inválido!";
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