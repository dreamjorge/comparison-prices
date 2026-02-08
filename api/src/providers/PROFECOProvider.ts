import { parse } from "csv-parse";
import { Readable } from "node:stream";
import type { DataProvider, PriceRecord } from "./DataProvider.js";

const STATE_ALIASES: Record<string, string> = {
    cdmx: "CIUDAD DE MEXICO",
    "ciudad de mexico": "CIUDAD DE MEXICO",
    "ciudad de méxico": "CIUDAD DE MEXICO",
    df: "CIUDAD DE MEXICO",
    "distrito federal": "CIUDAD DE MEXICO",
    jalisco: "JALISCO",
    "nuevo leon": "NUEVO LEON",
    "nuevo león": "NUEVO LEON",
    nl: "NUEVO LEON",
    edomex: "ESTADO DE MEXICO",
    "estado de mexico": "ESTADO DE MEXICO",
    "estado de méxico": "ESTADO DE MEXICO",
    veracruz: "VERACRUZ",
    puebla: "PUEBLA",
    guanajuato: "GUANAJUATO",
    chihuahua: "CHIHUAHUA",
    michoacan: "MICHOACAN",
    "michoacán": "MICHOACAN",
    oaxaca: "OAXACA",
    guerrero: "GUERRERO",
    tamaulipas: "TAMAULIPAS",
    baja: "BAJA CALIFORNIA",
    "baja california": "BAJA CALIFORNIA",
    "baja california norte": "BAJA CALIFORNIA",
    bc: "BAJA CALIFORNIA",
    bcs: "BAJA CALIFORNIA SUR",
    "baja california sur": "BAJA CALIFORNIA SUR",
    sonora: "SONORA",
    coahuila: "COAHUILA",
    sinaloa: "SINALOA",
    chiapas: "CHIAPAS",
    tabasco: "TABASCO",
    yucatan: "YUCATAN",
    "yucatán": "YUCATAN",
    queretaro: "QUERETARO",
    "querétaro": "QUERETARO",
    hidalgo: "HIDALGO",
    morelos: "MORELOS",
    durango: "DURANGO",
    zacatecas: "ZACATECAS",
    aguascalientes: "AGUASCALIENTES",
    slp: "SAN LUIS POTOSI",
    "san luis potosi": "SAN LUIS POTOSI",
    "san luis potosí": "SAN LUIS POTOSI",
    colima: "COLIMA",
    nayarit: "NAYARIT",
    campeche: "CAMPECHE",
    tlaxcala: "TLAXCALA",
    "quintana roo": "QUINTANA ROO",
};

function normalizeText(text: string): string {
    return text
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .trim();
}

function normalizeState(input: string): string {
    const normalized = normalizeText(input);
    return STATE_ALIASES[normalized] || normalized.toUpperCase();
}

export class PROFECOProvider implements DataProvider {
    private baseUrl: string;

    constructor(baseUrl?: string) {
        this.baseUrl = baseUrl ||
            process.env.PROFECO_BASE_URL ||
            "https://repodatos.atdt.gob.mx/api_update/profeco/programa_quien_es_quien_precios_2025";
    }

    getName(): string {
        return "PROFECO";
    }

    private buildUrl(year: number, month: number, part: 1 | 2): string {
        const mm = String(month).padStart(2, "0");
        return `${this.baseUrl}/${mm}-${year}_0${part}.csv`;
    }

    private mostRecentMonthYear(): { year: number; month: number } {
        const now = new Date();
        // PROFECO data lags about 1 month, try current month then fall back
        return { year: now.getFullYear(), month: now.getMonth() + 1 };
    }

    private async fetchCsv(url: string): Promise<string | null> {
        try {
            const controller = new AbortController();
            const timer = setTimeout(() => controller.abort(), 120_000); // 2 minutes for large files
            const response = await fetch(url, { signal: controller.signal });
            clearTimeout(timer);
            if (!response.ok) {
                return null;
            }
            return await response.text();
        } catch {
            return null;
        }
    }

    private parseCsvText(csvText: string, stateFilter?: string): Promise<PriceRecord[]> {
        return new Promise((resolve, reject) => {
            const records: PriceRecord[] = [];
            const stream = Readable.from([csvText]);

            stream.pipe(
                parse({
                    columns: (header) => header.map((col: string) => col.toUpperCase()),
                    skip_empty_lines: true,
                    trim: true,
                    relax_column_count: true,
                })
            )
                .on("data", (row: Record<string, string>) => {
                    const rowState = (row["ESTADO"] || row["ENTIDAD"] || "").trim().toUpperCase();

                    if (stateFilter && rowState !== stateFilter) {
                        return;
                    }

                    const priceStr = (row["PRECIO"] || "").replace(",", ".");
                    const price = parseFloat(priceStr);
                    if (Number.isNaN(price) || price <= 0) {
                        return;
                    }

                    const fechaRaw = row["FECHA_REGISTRO"] || row["FECHAREGISTRO"] || row["FECHA"] || "";
                    let capturedAt: string;
                    try {
                        capturedAt = new Date(fechaRaw).toISOString();
                    } catch {
                        capturedAt = new Date().toISOString();
                    }

                    records.push({
                        product: (row["PRODUCTO"] || "").trim(),
                        brand: (row["MARCA"] || "").trim() || null,
                        presentation: (row["PRESENTACION"] || row["PRESENTACI\u00D3N"] || "").trim() || null,
                        category: (row["CATEGORIA"] || row["CATEGOR\u00CDA"] || "").trim() || null,
                        store: (row["CADENA_COMERCIAL"] || row["CADENACOMERCIAL"] || row["NOMBRE_COMERCIAL"] || row["NOMBRECOMERCIAL"] || "").trim(),
                        address: (row["DIRECCION"] || row["DIRECCI\u00D3N"] || "").trim() || null,
                        price,
                        state: rowState,
                        municipality: (row["MUNICIPIO"] || "").trim().toUpperCase(),
                        capturedAt,
                    });
                })
                .on("error", reject)
                .on("end", () => resolve(records));
        });
    }

    async fetchPrices(state?: string): Promise<PriceRecord[]> {
        const stateFilter = state ? normalizeState(state) : undefined;
        const { year, month } = this.mostRecentMonthYear();

        const allRecords: PriceRecord[] = [];

        // Try current month and previous months as fallback (up to 4 months back)
        for (let monthOffset = 0; monthOffset <= 4; monthOffset++) {
            let targetMonth = month - monthOffset;
            let targetYear = year;
            if (targetMonth <= 0) {
                targetMonth += 12;
                targetYear -= 1;
            }

            let foundData = false;

            for (const part of [1, 2] as const) {
                const url = this.buildUrl(targetYear, targetMonth, part);
                console.log(`[PROFECO] Fetching ${url}`);
                const csvText = await this.fetchCsv(url);

                if (csvText) {
                    try {
                        const records = await this.parseCsvText(csvText, stateFilter);
                        console.log(`[PROFECO] Parsed ${records.length} records from part ${part}`);
                        // Use concat to avoid stack overflow with large arrays
                        for (const record of records) {
                            allRecords.push(record);
                        }
                        foundData = true;
                    } catch (err) {
                        console.error(`[PROFECO] Parse error for part ${part}:`, err);
                    }
                } else {
                    console.warn(`[PROFECO] No data at ${url}`);
                }
            }

            if (foundData) {
                break;
            }
        }

        return allRecords;
    }
}
