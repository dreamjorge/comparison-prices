import re
from typing import Optional, Tuple


class NormalizationService:
    @staticmethod
    def clean_brand(brand: Optional[str]) -> str:
        if not brand:
            return ""
        # Lowercase and remove special characters/extra whitespace
        brand = brand.lower().strip()
        brand = re.sub(r'[^\w\s]', '', brand)
        return brand

    @staticmethod
    def normalize_unit(label: Optional[str]) -> Tuple[float, str]:
        """
        Normalizes unit labels to a standard base (kg, L, pieces).
        Returns (multiplier, standard_unit).
        """
        if not label:
            return 1.0, "unit"

        label = label.lower().strip()

        # Volume
        if re.search(r'(\d+)\s*(ml|mililitros)', label):
            match = re.search(r'(\d+)\s*(ml|mililitros)', label)
            return float(match.group(1)) / 1000.0, "L"
        if re.search(r'(\d+)\s*(l|litro)', label):
            match = re.search(r'(\d+)\s*(l|litro)', label)
            return float(match.group(1)), "L"

        # Weight
        if re.search(r'(\d+)\s*(g|gramos)', label):
            match = re.search(r'(\d+)\s*(g|gramos)', label)
            return float(match.group(1)) / 1000.0, "kg"
        if re.search(r'(\d+)\s*(kg|kilos|kilogramos)', label):
            match = re.search(r'(\d+)\s*(kg|kilos|kilogramos)', label)
            return float(match.group(1)), "kg"

        # Count
        if re.search(r'(\d+)\s*(pz|piezas|pzs|unidades)', label):
            match = re.search(r'(\d+)\s*(pz|piezas|pzs|unidades)', label)
            return float(match.group(1)), "pz"

        return 1.0, label

    @classmethod
    def get_normalized_search_key(cls, name: str, brand: Optional[str]) -> str:
        clean_name = name.lower().strip()
        clean_brand = cls.clean_brand(brand)
        return f"{clean_brand} {clean_name}".strip()
