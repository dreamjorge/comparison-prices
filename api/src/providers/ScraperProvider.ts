import type { DataProvider, PriceRecord } from "./DataProvider.js";

/**
 * Abstract base class for future web scraper providers.
 *
 * To implement a new scraper:
 * 1. Extend this class
 * 2. Implement scrapeStore() with the store-specific scraping logic
 * 3. Register the new provider in services/dataSync.ts
 *
 * Example:
 *   class WalmartMXScraper extends ScraperProvider {
 *     getName() { return "Walmart MX"; }
 *     async scrapeStore(storeUrl, state) { ... }
 *   }
 */
export abstract class ScraperProvider implements DataProvider {
    abstract getName(): string;

    abstract scrapeStore(storeUrl: string, state?: string): Promise<PriceRecord[]>;

    async fetchPrices(state?: string): Promise<PriceRecord[]> {
        const storeUrls = this.getStoreUrls(state);
        const results: PriceRecord[] = [];

        for (const url of storeUrls) {
            try {
                const records = await this.scrapeStore(url, state);
                results.push(...records);
            } catch (err) {
                console.error(`[${this.getName()}] Error scraping ${url}:`, err);
            }
        }

        return results;
    }

    /**
     * Override to return store URLs to scrape.
     * Can be filtered by state if the scraper supports region-specific pages.
     */
    protected getStoreUrls(_state?: string): string[] {
        return [];
    }
}
