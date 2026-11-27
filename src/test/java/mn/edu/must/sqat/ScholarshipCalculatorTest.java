package mn.edu.must.sqat;

// ЛАБ 11 — B1: сул assertion-уудыг (assertNotNull, assertTrue(муж))
// ЯГ УТГЫН assertEquals болгосон. Оролтууд нь эхний багцынхтай ижил.
// ЛАБ 11 — B2: зааг утгын тестүүд нэмсэн (файлын төгсгөлд).

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ScholarshipCalculatorTest {

    private static final double EPS = 1e-9;

    ScholarshipCalculator calc;

    @BeforeEach
    void setUp() { calc = new ScholarshipCalculator(); }

    // ---- letterGrade: муж бүр ЯГ тодорхой үсэг буцаана ----

    @Test
    void letterGrade_returnsExactLetterForAllRanges() {
        assertEquals("A", calc.letterGrade(95));
        assertEquals("B", calc.letterGrade(85));
        assertEquals("C", calc.letterGrade(75));
        assertEquals("D", calc.letterGrade(65));
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    // ---- gpa: ЯГ голчийн утга ----

    @Test
    void gpa_returnsExactAverage() {
        assertEquals(3.5, calc.gpa(new double[]{95, 85}), EPS); // (4 + 3) / 2
        assertEquals(1.5, calc.gpa(new double[]{75, 65}), EPS); // (2 + 1) / 2
        assertEquals(0.0, calc.gpa(new double[]{40}), EPS);     // F = 0
    }

    @Test
    void gpa_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    // ---- scholarship: дүрэм бүр ЯГ тодорхой хувь буцаана ----

    @Test
    void scholarship_returnsExactPercent() {
        assertEquals(100, calc.scholarship(3.9, 16, false));
        assertEquals(70, calc.scholarship(3.5, 13, false));
        assertEquals(50, calc.scholarship(3.1, 5, false));
        assertEquals(0, calc.scholarship(2.0, 1, false));
        assertEquals(0, calc.scholarship(4.0, 20, true));
    }

    @Test
    void scholarship_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }

    // =====================================================================
    // B2: ЗААГ УТГЫН ТЕСТҮҮД
    // =====================================================================

    // ---- letterGrade: үсгэн дүн бүрийн зааг (x / x-0.01) ба 0, 100 ----

    @ParameterizedTest(name = "letterGrade({0}) = {1}")
    @CsvSource({
            "100,   A",
            "90,    A",
            "89.99, B",
            "80,    B",
            "79.99, C",
            "70,    C",
            "69.99, D",
            "60,    D",
            "59.99, F",
            "0,     F"
    })
    void letterGrade_boundaries(double score, String expected) {
        assertEquals(expected, calc.letterGrade(score));
    }

    @Test
    void letterGrade_justOutsideRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-0.01));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(100.01));
    }

    // ---- gpa: switch-ийн бүх үсгэн дүн ----

    @ParameterizedTest(name = "gpa([{0}]) = {1}")
    @CsvSource({
            "95, 4.0",   // A
            "85, 3.0",   // B
            "75, 2.0",   // C
            "65, 1.0",   // D
            "40, 0.0"    // F (default)
    })
    void gpa_eachLetterGrade(double score, double expected) {
        assertEquals(expected, calc.gpa(new double[]{score}), EPS);
    }

    @Test
    void gpa_allLettersTogether() {
        // (4 + 3 + 2 + 1 + 0) / 5 = 2.0
        assertEquals(2.0, calc.gpa(new double[]{95, 85, 75, 65, 40}), EPS);
    }

    // ---- scholarship: 3.8/15, 3.4/12, 3.0 заагууд ----

    @ParameterizedTest(name = "scholarship({0}, {1}) = {2}")
    @CsvSource({
            // 100% дүрэм: голч >= 3.8 ба кредит >= 15
            "3.8,  15, 100",
            "3.79, 15, 70",
            "3.8,  14, 70",
            // 70% дүрэм: голч >= 3.4 ба кредит >= 12
            "3.4,  12, 70",
            "3.39, 12, 50",
            "3.4,  11, 50",
            // 50% дүрэм: голч >= 3.0
            "3.0,  0,  50",
            "2.99, 20, 0"
    })
    void scholarship_boundaries(double gpa, int credits, int expected) {
        assertEquals(expected, calc.scholarship(gpa, credits, false));
    }

    // ---- scholarship: оролтын хязгаар (gpa 0 ба 4, credits 0) ----

    @Test
    void scholarship_inputLimits_areValid() {
        assertEquals(0, calc.scholarship(0.0, 0, false));
        assertEquals(100, calc.scholarship(4.0, 15, false));
        assertEquals(50, calc.scholarship(4.0, 0, false));
    }

    @Test
    void scholarship_justOutsideLimits_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.01, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.01, 10, false));
    }

    @Test
    void scholarship_violation_alwaysZero() {
        assertEquals(0, calc.scholarship(4.0, 30, true));
        assertEquals(0, calc.scholarship(3.5, 12, true));
        assertEquals(0, calc.scholarship(3.0, 0, true));
    }
}
