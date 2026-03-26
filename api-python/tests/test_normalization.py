from services.normalization import NormalizationService


class TestBrandCleaning:
    def test_clean_brand_normal(self):
        assert NormalizationService.clean_brand("Lala") == "lala"

    def test_clean_brand_special_chars(self):
        assert NormalizationService.clean_brand("Coca-Cola!") == "cocacola"

    def test_clean_brand_none(self):
        assert NormalizationService.clean_brand(None) == ""

    def test_clean_brand_empty(self):
        assert NormalizationService.clean_brand("") == ""

    def test_clean_brand_whitespace(self):
        assert NormalizationService.clean_brand("  Verde Valle  ") == "verde valle"


class TestUnitNormalization:
    def test_milliliters(self):
        value, unit = NormalizationService.normalize_unit("500ml")
        assert unit == "L"
        assert abs(value - 0.5) < 0.001

    def test_liters(self):
        value, unit = NormalizationService.normalize_unit("2l")
        assert unit == "L"
        assert value == 2.0

    def test_grams(self):
        value, unit = NormalizationService.normalize_unit("500g")
        assert unit == "kg"
        assert abs(value - 0.5) < 0.001

    def test_kilograms(self):
        value, unit = NormalizationService.normalize_unit("1kg")
        assert unit == "kg"
        assert value == 1.0

    def test_pieces(self):
        value, unit = NormalizationService.normalize_unit("30 piezas")
        assert unit == "pz"
        assert value == 30.0

    def test_none_input(self):
        value, unit = NormalizationService.normalize_unit(None)
        assert value == 1.0
        assert unit == "unit"


class TestSearchKey:
    def test_search_key_with_brand(self):
        key = NormalizationService.get_normalized_search_key("Leche Entera 1L", "Lala")
        assert "lala" in key
        assert "leche entera 1l" in key

    def test_search_key_no_brand(self):
        key = NormalizationService.get_normalized_search_key("Arroz Blanco", None)
        assert key == "arroz blanco"
