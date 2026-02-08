// Test different months to find available PROFECO data

const baseUrl = "https://repodatos.atdt.gob.mx/api_update/profeco/programa_quien_es_quien_precios_2025";

async function checkUrl(url) {
  try {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), 10000);
    const response = await fetch(url, {
      method: 'HEAD',
      signal: controller.signal
    });
    clearTimeout(timer);
    return response.ok;
  } catch {
    return false;
  }
}

console.log("🔍 Searching for available PROFECO data...\n");

// Try months from Jan 2025 backwards
const months = [];
for (let year = 2025; year >= 2024; year--) {
  for (let month = 12; month >= 1; month--) {
    if (year === 2025 && month > 12) continue;
    months.push({ year, month });
  }
}

let foundAny = false;

for (const { year, month } of months.slice(0, 15)) {
  const mm = String(month).padStart(2, "0");

  for (const part of [1, 2]) {
    const url = `${baseUrl}/${mm}-${year}_0${part}.csv`;
    process.stdout.write(`Testing ${mm}-${year}_0${part}.csv ... `);

    const exists = await checkUrl(url);

    if (exists) {
      console.log("✅ FOUND");
      foundAny = true;

      // Try to fetch actual data
      const response = await fetch(url);
      const text = await response.text();
      const lines = text.split('\n').slice(0, 3);
      console.log(`   Preview (first 3 lines):`);
      lines.forEach(line => console.log(`   ${line.substring(0, 100)}...`));
      console.log();
    } else {
      console.log("❌ Not found");
    }
  }

  if (foundAny) break;
}

if (!foundAny) {
  console.log("\n⚠️  No data found. Trying alternative URLs...");

  // Try 2024 data format
  const url2024 = "https://repodatos.atdt.gob.mx/api_update/profeco/programa_quien_es_quien_precios_2024/12-2024_01.csv";
  console.log(`\nTesting 2024 format: ${url2024}`);
  const exists = await checkUrl(url2024);
  console.log(exists ? "✅ FOUND" : "❌ Not found");
}
