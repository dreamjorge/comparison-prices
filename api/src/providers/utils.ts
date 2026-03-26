import { Product } from "../../../packages/contracts/src/types";

const STOPWORDS = new Set(["de", "la", "el", "los", "las", "y", "con"]);

export function normalizeText(value: string): string {
  return value
    .normalize("NFD")
    .replace(/\p{Diacritic}/gu, "")
    .toLowerCase()
    .replace(/[^a-z0-9\s]/g, " ")
    .split(/\s+/)
    .filter(Boolean)
    .filter((token) => !STOPWORDS.has(token))
    .join(" ")
    .trim();
}

export function normalizeBrand(brand: string | null | undefined): string {
  if (!brand) {
    return "";
  }
  return normalizeText(brand);
}

export function normalizeSizeLabel(sizeLabel: string | null | undefined): string {
  if (!sizeLabel) {
    return "";
  }
  const raw = sizeLabel.toLowerCase().trim();

  const mlMatch = raw.match(/(\d+(?:\.\d+)?)\s*(ml|mililitros?)/);
  if (mlMatch) {
    return `${(Number.parseFloat(mlMatch[1]) / 1000).toFixed(3)}l`;
  }

  const lMatch = raw.match(/(\d+(?:\.\d+)?)\s*(l|litros?)/);
  if (lMatch) {
    return `${Number.parseFloat(lMatch[1]).toFixed(3)}l`;
  }

  const gMatch = raw.match(/(\d+(?:\.\d+)?)\s*(g|gramos?)/);
  if (gMatch) {
    return `${(Number.parseFloat(gMatch[1]) / 1000).toFixed(3)}kg`;
  }

  const kgMatch = raw.match(/(\d+(?:\.\d+)?)\s*(kg|kilos?|kilogramos?)/);
  if (kgMatch) {
    return `${Number.parseFloat(kgMatch[1]).toFixed(3)}kg`;
  }

  return normalizeText(raw);
}

export function normalizedProductKey(product: Product): string {
  const name = normalizeText(product.name);
  const brand = normalizeBrand(product.brand);
  const size = normalizeSizeLabel(product.sizeLabel);
  return [brand, name, size].filter(Boolean).join(" ").trim();
}

export function createGoogleShoppingUrl(product: Product): string {
  const query = [product.name, product.brand ?? "", product.sizeLabel ?? ""].filter(Boolean).join(" ");
  return `https://www.google.com/search?tbm=shop&q=${encodeURIComponent(query)}`;
}
