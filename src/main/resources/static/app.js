async function evaluateExpression() {
    const exp = document.getElementById("expression").value;

    fetch(`/evaluate?exp=${encodeURIComponent(exp)}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(jsonData)  // tu JSON completo
    })
        .then(res => res.json())
        .then(result => console.log(result))
        .catch(err => console.error(err));

    const params = new URLSearchParams({ exp });
    try {
        const res = await fetch(`/evaluate?${params.toString()}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(json)
        });
        const data = await res.json();
        document.getElementById("eval-result").textContent = JSON.stringify(data, null, 2);
    } catch (err) {
        document.getElementById("eval-result").textContent = err;
    }
}

async function findExpression() {
    const value = document.getElementById("find-value").value;
    const jsonText = document.getElementById("find-json").value;
    let json;
    try {
        json = JSON.parse(jsonText);
    } catch(e) {
        document.getElementById("find-result").textContent = "Invalid JSON!";
        return;
    }

    try {
        const res = await fetch(`/evaluate/get-expression/${encodeURIComponent(value)}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(json)
        });
        const data = await res.json();
        document.getElementById("find-result").textContent = JSON.stringify(data, null, 2);
    } catch (err) {
        document.getElementById("find-result").textContent = err;
    }
}