use strsim::jaro_winkler;
use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct ProductCandidate {
    pub id: String,
    pub name: String,
    pub brand: Option<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct MatchResult {
    pub id: String,
    pub score: f64,
}

pub fn clean_text(text: &str) -> String {
    text.to_lowercase()
        .chars()
        .filter(|c| c.is_alphanumeric() || c.is_whitespace())
        .collect::<String>()
        .split_whitespace()
        .collect::<Vec<_>>()
        .join(" ")
}

pub fn calculate_score(query: &str, candidate: &ProductCandidate) -> f64 {
    let clean_query = clean_text(query);
    let clean_name = clean_text(&candidate.name);
    let clean_brand = candidate.brand.as_ref().map(|b| clean_text(b)).unwrap_or_default();
    
    let full_name = format!("{} {}", clean_brand, clean_name).trim().to_string();
    
    // Base score using Jaro-Winkler on combined brand + name
    let mut score = jaro_winkler(&clean_query, &full_name);
    
    // Exact brand match boost
    if !clean_brand.is_empty() && clean_query.contains(&clean_brand) {
        score = (score + 0.1).min(1.0);
    }
    
    // Exact word match bonus
    let query_words: Vec<&str> = clean_query.split_whitespace().collect();
    let name_words: Vec<&str> = full_name.split_whitespace().collect();
    let matches = query_words.iter().filter(|w| name_words.contains(w)).count();
    
    if !query_words.is_empty() {
        let word_score = matches as f64 / query_words.len() as f64;
        // Jaro-Winkler is better for fuzzy, Word match is better for precise.
        score = (score * 0.8) + (word_score * 0.2);
    }

    score
}

pub fn match_candidates(query: &str, candidates: &[ProductCandidate], top_n: usize) -> Vec<MatchResult> {
    let mut results: Vec<MatchResult> = candidates
        .iter()
        .map(|c| MatchResult {
            id: c.id.clone(),
            score: calculate_score(query, c),
        })
        .filter(|r| r.score > 0.4)
        .collect();

    results.sort_by(|a, b| b.score.partial_cmp(&a.score).unwrap());
    results.into_iter().take(top_n).collect()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_clean_text() {
        assert_eq!(clean_text("Leche Lala!"), "leche lala");
        assert_eq!(clean_text("  ARROZ   blanco  "), "arroz blanco");
    }

    #[test]
    fn test_brand_match() {
        let candidate = ProductCandidate {
            id: "1".to_string(),
            name: "Leche Entera".to_string(),
            brand: Some("Lala".to_string()),
        };
        let query = "Lala Leche";
        let score = calculate_score(query, &candidate);
        assert!(score > 0.8, "Score should be high for brand + name match, got {}", score);
    }

    #[test]
    fn test_fuzzy_match() {
        let candidate = ProductCandidate {
            id: "1".to_string(),
            name: "Arroz Blanco 1kg".to_string(),
            brand: Some("Schettino".to_string()),
        };
        let query = "Aros Blanket"; // Very fuzzy
        let score = calculate_score(query, &candidate);
        assert!(score > 0.5, "Score should be reasonable for fuzzy match, got {}", score);
    }
}
