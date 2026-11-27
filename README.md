# Лаборатори №11: Mutation testing — PIT-ээр тестийн чанараа хэмжих

**Овог нэр:** Ц. Тодбилэг
**Оюутны код:** B232270045

## Үр дүнгийн товч

| | Эхлэл | B1-ийн дараа | B2-ийн дараа (эцсийн) |
|---|---|---|---|
| Line coverage (JaCoCo / PIT) | 100% (25/25) | 100% (25/25) | 100% (25/25) |
| Mutation coverage (PIT) | 27% (17/63) | 71% (45/63) | **100% (63/63)** |

## Хэрхэн ажиллуулах

```bash
git clone <энэ репо>
cd <репо>
mvn clean verify                                          # тест + JaCoCo тайлан (target/site/jacoco/index.html)
mvn test-compile org.pitest:pitest-maven:mutationCoverage # PIT тайлан (target/pit-reports/index.html)
```

## Репозиторийн бүтэц

```
├── pom.xml                         # JUnit 5 + JaCoCo + PIT (STRONGER операторууд) — өөрчлөөгүй
├── src/main/.../ScholarshipCalculator.java     # өөрчлөөгүй
├── src/test/.../ScholarshipCalculatorTest.java # B1 + B2-оор хүчтэй болгосон тестүүд
├── results/                        # хэмжилт бүрийн бүтэн текст гаралт
│   ├── mvn-verify-start.txt, jacoco-start.csv
│   ├── pit-start.txt, pit-B1.txt, pit-B2.txt
│   └── mvn-test-manual-mutant.txt
└── images/                         # JaCoCo болон PIT тайлангийн зураг
```

## Ажлын орчин

```
> java -version
java version "21.0.9" 2025-10-21 LTS
Java(TM) SE Runtime Environment (build 21.0.9+7-LTS-338)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.9+7-LTS-338, mixed mode, sharing)

> mvn -version
Apache Maven 3.9.12 (848fbb4bf2d427b72bdb2471c22fced7ebd9a7a1)
Maven home: C:\Program Files\apache-maven-3.9.12
Java version: 21.0.9, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-21
```

Үйлдлийн систем: Windows 11. PIT-ийг ажиллуулахдаа төслийн замд хоосон зай, хаалт (`starter (1)`) байхад
"No mutations found" алдаа гарсан тул төслийг хоосон зайгүй зам руу (`test_lab_11/lab11-pit`) задалж ажилласан.
`pom.xml` болон `src/main`-д ямар ч өөрчлөлт хийгээгүй.

## Эхний хэмжилт

```
mvn clean verify                                   -> results/mvn-verify-start.txt, results/jacoco-start.csv
mvn test-compile org.pitest:pitest-maven:mutationCoverage -> results/pit-start.txt
```

**JaCoCo:** Line 100% (25/25), Branch 38/41 (92%)

![JaCoCo эхний хэмжилт](images/jacoco-start.png)

**PIT (results/pit-start.txt):**

```
>> Line Coverage (for mutated classes only): 25/25 (100%)
>> Generated 63 mutations Killed 17 (27%)
>> Mutations with no coverage 0. Test strength 27%
```

**Ажиглалт:** Line coverage 100% боловч mutation coverage ердөө 27% (63-аас 17 мутант алагдсан) байна.
Энэ нь тестүүд кодын мөр бүрийг *ажиллуулж* байгаа ч гаралтыг нь бараг *шалгахгүй* байгааг харуулж байна —
`assertNotNull`, `assertTrue(0 <= x <= 100)` зэрэг сул assertion-ууд кодын утга буруу болсон ч тэнцсээр байна.
Өөрөөр хэлбэл хамралт нь "код ажилласан эсэх"-ийг, mutation score нь "алдааг тест барьж чадах эсэх"-ийг хэмждэг.

## A: Амьд мутантуудын ангилал

Эхний хэмжилтэд 63 мутантаас **46** нь амьд үлдсэн (SURVIVED). Мөрийн дугаар нь
`ScholarshipCalculator.java`-ийн мөр.

| Мөр | Код | Мутаци (PIT-ийн тайлбар) | Тоо | Ангилал | Арга хэмжээ |
|---|---|---|---|---|---|
| 20 | `score < 0 \|\| score > 100` | changed conditional boundary (`<` → `<=`, `>` → `>=`) | 2 | Тест дутуу | `letterGrade(0)`, `letterGrade(100)` хүчинтэй гэж шалгах зааг тест |
| 23 | `score >= 90` | changed conditional boundary; removed conditional (true/false); replaced return value with "" | 4 | Тест дутуу | `assertEquals("A", ...)`; 90 / 89.99 зааг тест |
| 24 | `score >= 80` | changed conditional boundary; removed conditional (true/false); replaced return value with "" | 4 | Тест дутуу | `assertEquals("B", ...)`; 80 / 79.99 зааг тест |
| 25 | `score >= 70` | changed conditional boundary; removed conditional (true/false); replaced return value with "" | 4 | Тест дутуу | `assertEquals("C", ...)`; 70 / 69.99 зааг тест |
| 26 | `score >= 60` | changed conditional boundary; removed conditional (true/false); replaced return value with "" | 4 | Тест дутуу | `assertEquals("D", ...)`; 60 / 59.99 зааг тест |
| 27 | `return "F"` | replaced return value with "" | 1 | Тест дутуу | `assertEquals("F", ...)` |
| 36 | `switch (letterGrade(s))` | Changed switch default to be first case | 1 | Тест дутуу | Зөвхөн F дүнтэй `gpa` = 0.0 гэж яг утгаар шалгах |
| 38 | `total += 3` (B) | Replaced double addition with subtraction | 1 | Тест дутуу | `gpa({85}) == 3.0` яг утга |
| 40 | `total += 1` (D) | Replaced double addition with subtraction | 1 | Тест дутуу | `gpa({65}) == 1.0` яг утга |
| 44 | `return total / scores.length` | replaced double return with 0.0d | 1 | Тест дутуу | `gpa`-г `assertEquals(..., 1e-9)`-ээр яг утгаар шалгах |
| 48 | `gpa < 0 \|\| gpa > 4 \|\| credits < 0` | changed conditional boundary ×2 (`gpa < 0`, `credits < 0`); removed conditional ×1 | 3 | Тест дутуу | gpa = 0, credits = 0 хүчинтэй; gpa = -0.1 алдаа шиддэг гэж шалгах |
| 51 | `if (hasViolation) return 0` | removed conditional (equality → true / false) | 2 | Тест дутуу | Зөрчилтэй → яг 0, зөрчилгүй → яг 100 гэж шалгах |
| 52 | `gpa >= 3.8 && credits >= 15` → 100 | changed conditional boundary ×2; removed conditional ×4; replaced int return with 0 | 7 | Тест дутуу | `assertEquals(100, ...)`; 3.8/15, 3.79/15, 3.8/14 зааг тест |
| 53 | `gpa >= 3.4 && credits >= 12` → 70 | changed conditional boundary ×2; removed conditional ×4; replaced int return with 0 | 7 | Тест дутуу | `assertEquals(70, ...)`; 3.4/12, 3.39/12, 3.4/11 зааг тест |
| 54 | `gpa >= 3.0` → 50 | changed conditional boundary; removed conditional (true/false); replaced int return with 0 | 4 | Тест дутуу | `assertEquals(50, ...)`; 3.0 / 2.99 зааг тест |
| | | **Нийт** | **46** | | |

**Ангиллын үндэслэл:**

- **Мөр 20, 48 (оролтын хязгаар) — тест дутуу:** `<` → `<=` өөрчлөлт нь 0 оноо, 0 голч, 0 кредит гэх хүчинтэй оролтод
  алдаа шидэх болгож бодит зан төлөвийг өөрчилдөг тул тест дутуу; одоогийн тестүүд эдгээр зааг утгыг огт өгдөггүй.
- **Мөр 23–27 (letterGrade) — тест дутуу:** "" буцаах, нөхцөлийг true/false болгох нь буцаах үсгэн дүнг өөрчилдөг ч
  `assertNotNull` зөвхөн null биш эсэхийг шалгадаг тул тест дутуу (сул assertion).
- **Мөр 36–44 (gpa) — тест дутуу:** нэмэхийг хасах болгох, 0.0 буцаах нь голчийн утгыг өөрчилдөг ч
  `assertTrue(0 <= g <= 4)` муж шалгалт тэр утгуудыг зөвшөөрдөг тул тест дутуу.
  Мөр 36-ийн switch мутант нь String switch-ийн аль хэсэгт хийгдсэнээс хамаарч equivalent байж магадгүй гэж
  эхэндээ эргэлзсэн боловч B1-д `gpa({40}) == 0.0` гэж яг утгаар шалгахад алагдсан — тэгэхээр equivalent биш, тест дутуу байсан.
- **Мөр 51–54 (scholarship) — тест дутуу:** 100/70/50-г 0 болгох, нөхцөлийг солих нь тэтгэлгийн хувийг өөрчилдөг ч
  `assertTrue(0 <= s <= 100)` бүх боломжит хариуг зөвшөөрдөг, мөн 3.8/15, 3.4/12, 3.0 зааг утгууд тестлэгдээгүй тул тест дутуу.

Эхний шинжилгээгээр **equivalent мутант олдсонгүй** — 46 амьд мутант бүгд бодит зан төлөвийг өөрчилдөг тул сул assertion
эсвэл зааг утгын тест дутуугаас үүдэлтэй.

## B: Тестүүдээ хүчтэй болгох

### B1 — Assertion-уудыг яг утгын болгох

Оролтуудыг өөрчлөлгүй, зөвхөн шалгалтуудыг солисон:

| Өмнө (сул) | Дараа (яг утга) |
|---|---|
| `assertNotNull(calc.letterGrade(95))` | `assertEquals("A", calc.letterGrade(95))` (B, C, D, F мөн адил) |
| `assertTrue(g1 >= 0 && g1 <= 4)` | `assertEquals(3.5, calc.gpa({95, 85}), 1e-9)`; `{75, 65}` → 1.5; `{40}` → 0.0 |
| `assertTrue(s1 >= 0 && s1 <= 100)` | `assertEquals(100, calc.scholarship(3.9, 16, false))`; 70, 50, 0, 0 |

**Хэмжилт (results/pit-B1.txt):**

```
>> Generated 63 mutations Killed 45 (71%)
>> Mutations with no coverage 0. Test strength 71%
```

Зөвхөн assertion-ыг чангалснаар 28 мутант нэмж алагдсан: "replaced return value", "addition → subtraction",
"replaced int return with 0", `hasViolation`-ийн мутантууд, мөр 36-ийн switch мутант бүгд үхсэн.
Үлдсэн 18 амьд мутант нь бүгд **changed conditional boundary** болон зарим **removed conditional** (мөр 20, 23–26, 48, 52–54) —
эдгээр нь зөвхөн зааг утгын оролтоор алагдана, тиймээс B2 хэрэгтэй.

### B2 — Зааг утгын тестүүд нэмэх

Нэмсэн тестүүд (`@ParameterizedTest` + `@CsvSource`):

| Тест | Оролтууд | Алсан мутантууд |
|---|---|---|
| `letterGrade_boundaries` | 100, 90/89.99, 80/79.99, 70/69.99, 60/59.99, 0 | Мөр 20, 23–26-ийн conditional boundary |
| `letterGrade_justOutsideRange_throws` | -0.01, 100.01 | Хязгаарын шалгалтыг бататгана |
| `gpa_eachLetterGrade`, `gpa_allLettersTogether` | A, B, C, D, F тус бүр + бүгд хамт (= 2.0) | switch-ийн бүх салаа |
| `scholarship_boundaries` | 3.8/15 → 100, 3.79/15 → 70, 3.8/14 → 70, 3.4/12 → 70, 3.39/12 → 50, 3.4/11 → 50, 3.0/0 → 50, 2.99/20 → 0 | Мөр 52–54-ийн boundary ба removed conditional |
| `scholarship_inputLimits_areValid` | gpa 0, gpa 4, credits 0 | Мөр 48-ийн boundary ба removed conditional |
| `scholarship_justOutsideLimits_throws` | gpa -0.01, 4.01 | Мөр 48 |
| `scholarship_violation_alwaysZero` | Зөрчилтэй 3 тохиолдол | Мөр 51 |

**Хэмжилт (results/pit-B2.txt, эцсийн):**

```
>> Line Coverage (for mutated classes only): 25/25 (100%)
>> Generated 63 mutations Killed 63 (100%)
>> Mutations with no coverage 0. Test strength 100%
```

![PIT эцсийн тайлан](images/pit-final.png)

### Хэмжилтийн явц

| Хэмжилт | Файл | Line coverage | Killed / Generated | Mutation coverage | Test strength |
|---|---|---|---|---|---|
| Эхлэл | results/pit-start.txt | 25/25 (100%) | 17 / 63 | **27%** | 27% |
| B1-ийн дараа | results/pit-B1.txt | 25/25 (100%) | 45 / 63 | **71%** | 71% |
| B2-ийн дараа | results/pit-B2.txt | 25/25 (100%) | 63 / 63 | **100%** | 100% |

Line coverage гурван хэмжилтэд ижил 100% байсан ч mutation coverage 27% → 71% → 100% болж өссөн.
Бүх 63 мутант алагдсан тул equivalent гэж хасах мутант үлдээгүй; Test Strength 100%.

## C: Гараар нэг мутант — PIT-гүйгээр

`scholarship()`-ийн 52-р мөрийг гараар өөрчилсөн:

```diff
-        if (gpa >= 3.8 && credits >= 15) return 100;
+        if (gpa > 3.8 && credits >= 15) return 100;
```

`mvn test 2>&1 | tee results/mvn-test-manual-mutant.txt` ажиллуулахад:

```
[ERROR] Tests run: 34, Failures: 1, Errors: 0, Skipped: 0
[ERROR]   ScholarshipCalculatorTest.scholarship_boundaries:138 expected: <100> but was: <70>
[INFO] BUILD FAILURE
```

**Унасан тест:** `scholarship_boundaries` — эхний мөр `[1]` (`gpa = 3.8, credits = 15, хүлээгдэх = 100`).
Мутацитай кодод 3.8 нь `> 3.8` нөхцөлийг хангахгүй тул 100-ын оронд 70 буцаасан — яг B2-д нэмсэн зааг тест мутантыг барьсан.
Дараа нь `git restore src/main`-ээр кодыг буцаасан (мутацитай код commit хийгдээгүй; `git diff HEAD -- src/main` хоосон).
Эхний сул багц энэ мутантыг барихгүй байсан: тэр багцад 3.8 гэсэн оролт байгаагүй бөгөөд `assertTrue(0 <= s <= 100)` 70-ыг ч зөвшөөрнө.

### Өөрийн зохиосон equivalent мутант

```diff
-        if (scores == null || scores.length == 0) {
+        if (scores == null || scores.length <= 0) {
```

Java-д массивын урт (`length`) хэзээ ч сөрөг байж чаддаггүй тул `length == 0` ба `length <= 0` нь ямар ч оролтод яг ижил үр дүн өгнө.
Програмын зан төлөв огт өөрчлөгдөхгүй учраас аль ч тест энэ мутантыг "алах" боломжгүй — энэ нь тест дутуу биш, жинхэнэ **equivalent** мутант юм.

## Дүгнэлт

Line coverage нь тестийн явцад кодын аль мөр ажилласныг хэмждэг бол mutation score нь кодонд жижиг алдаа оруулахад тест түүнийг *барьж чадах эсэхийг* хэмждэг.
Энэ лабд эхний тестийн багц Line 100%-тай байсан ч 63 мутантаас ердөө 17-г (27%) алсан нь өндөр хамралт сайн тестийг баталдаггүйг тод харуулсан.
Гол шалтгаан нь `assertNotNull`, `assertTrue(муж)` зэрэг сул assertion-ууд байсан бөгөөд тэдгээрийг яг утгын `assertEquals` болгоход л score 71% хүрсэн.
Үлдсэн мутантууд бүгд зааг утгын (90/89.99, 3.8/15 гэх мэт) мутантууд байсан тул зааг утгын тестүүд нэмснээр 100%-д хүрсэн; энэ хугацаанд line coverage огт өөрчлөгдөөгүй.
Мөн бүх амьд мутант эцэстээ "тест дутуу" байсан нь equivalent гэж ангилахдаа болгоомжтой байх ёстойг харуулсан — мөр 36-ийн switch мутантыг эхэндээ equivalent байж магадгүй гэж бодсон ч яг утгын тест түүнийг алсан.
AI-аар тест бичүүлэхэд AI ихэвчлэн кодыг ажиллуулдаг, хамралт өндөр харагддаг боловч assertion нь сул эсвэл зааг утгыг алгасдаг тестүүд гаргах эрсдэлтэй.
Ийм тестүүд JaCoCo дээр "төгс" харагдах ч бодит алдааг барихгүй тул AI-ийн бичсэн тестийг mutation score-оор шалгаж, амьд мутант бүрийг нь хянах хэрэгтэй.
Иймээс хамралт нь тестийн хамгийн доод шаардлага, харин mutation score нь тестийн чанарын илүү бодит хэмжүүр юм.
