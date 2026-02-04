from services.normalization import NormalizationService


def test_normalization():
    print("Running Normalization Tests...")

    # Test Brand Cleaning
    assert NormalizationService.clean_brand("Lala!") == "lala"
    assert NormalizationService.clean_brand(" Bimbo ") == "bimbo"
    assert NormalizationService.clean_brand(None) == ""
    print("✅ Brand cleaning OK")

    # Test Unit Normalization
    val, unit = NormalizationService.normalize_unit("500ml")
    assert val == 0.5 and unit == "L"

    val, unit = NormalizationService.normalize_unit("2 litros")
    assert val == 2.0 and unit == "L"

    val, unit = NormalizationService.normalize_unit("1kg")
    assert val == 1.0 and unit == "kg"

    val, unit = NormalizationService.normalize_unit("250g")
    assert val == 0.25 and unit == "kg"

    val, unit = NormalizationService.normalize_unit("12 pz")
    assert val == 12.0 and unit == "pz"

    print("✅ Unit normalization OK")

    # Test Search Key
    key = NormalizationService.get_normalized_search_key("Leche Entera", "Lala")
    assert key == "lala leche entera"
    print("✅ Search key OK")

if __name__ == "__main__":
    try:
        test_normalization()
        print("\nALL TESTS PASSED!")
    except AssertionError as e:
        print("\nTEST FAILED!")
        raise e
