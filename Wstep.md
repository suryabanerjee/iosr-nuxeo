# Obszar i przedmiot projektowania #

## Opis dziedziny problemu ##

Zadaniem projektowym jest integracja usługi google translator z systemem CMS Nuxeo. Do zadań docelowego systemu należy tłumaczenie jednego lub więcej plików zgodnie z wybranymi ustawieniami użytkownika. Możliwa jest obsługa więcej niż jednego języka naraz oraz plików o różnych formatach. Planowana jest niezależność systemu od silnika tłumaczenia.

## Zakres systemu ##

System będzie warstwą pośredniczącą między CMS Nuxeo a danym silnikiem tłumaczenia. Zakres systemu obejmuje:
  * **Tłumaczenie asynchroniczne**
opcja pozwalająca na asynchroniczne wysłanie danego pliku (lub plików) do tłumaczenia i oznaczenia jego statusu jako pliku w trakcie tłumaczenia. Rezultat operacji jest zapisywany jako wskazany wcześniej, osobny plik. Obsługa tego rodzaju tłumaczenia jest wymagana wobec wszystkich wspieranych silników.
  * **Tłumaczenie synchroniczne**
opcja pozwala na szybkie wygenerowanie podglądu danego pliku w wybranym języku. Ten rodzaj tłumaczenia nie jest obsługiwany przez wszystkie silniki.
  * **Tłumaczenie do wielu języków naraz**
użytkownik może zażądać tłumaczeń pliku w kilku językach. Opcja wspierana tylko w czasie tłumaczenia asynchronicznego. Tłumaczenie w każdym z języków generuje osobny plik wynikowy.
  * **Obsługę różnych silników tłumaczenia**
system docelowo jest niezależny od silnika (domyślny to google translator). Administrator może dodać dowolnie wiele silników, a użytkownik do tłumaczenia wybrać dowolny z nich.
  * **Obsługę plików w różnych formatach**
tłumaczenie obejmuje pliki w różnych formatach, nie tylko tekstowym. Obsługiwane są m.in.pliki .pdf i .doc.

## Kontekst systemu ##

### Silnik tłumaczenia (domyslnie: google translator) ###

wymagana obsługa co najmniej 2 języków i akceptowalny czas zwrócenia rezultatu;

### CMS Nuxeo ###

  1. WPROWADZENIE DO NUXEO
Nuxeo jest open-sourceową platformą do rozwijania systemów klasy CMS
oparta o J2EE. Zapewnia szkielet systemu zarządzania treścią, definiuje podstawowe
widoki i funkcjonalności CMS (pliki, fora), zapewnia kontrolę dostępu (autentykacja,
autoryzacja), definiuje model danych i metadanych, implementuje środowisko do
wdrażania dedykowanych rozszerzeń platformy (extension points), komponenty
wspomagające integrację (serwisy wewnętrzne), silnik wyszukiwania, obsługę
zdarzeń, zarządzanie cyklem życia i wiele innych.

  1. ARCHITEKTURA
Nuxeo opiera się na architekturze komponentowej, skierowanej na standardy
(modele programowania: J2EE, OSGi; usługi sieciowe: SOAP; definiowanie treści:
XML Schemas). Z racji pewnych wad architektury J2EE (brak mechanizmów rozszerzania
modelu komponentów, mechanizmów deklarowania zależności), Nuxeo definiuje
warstwę środowiska uruchomieniowego (runtime layer), której zadaniem jest
wyeliminowanie tych problemów.
Warstwa ta między innymi definiuje mechanizm extension points,
najciekawsze z punktu widzenia developerów. Komponenty podlegające temu
mechanizmowi, mogą być aktywowane, bądź dezaktywowane przez zewnętrzną
konfigurację. Poprzez dodawane zasoby, podobnie do pluginów, można modyfikować
wygląd oraz zachowanie serwisu. Zasobem takim zazwyczaj jest XML, może być
ponadto kod javy (POJO, Seam), xhtml (JSF), jpg, bundle zasobów, itd. Dodawane
zasoby, zwane kontrybucjami, mogą nadpisywać konfigurację komponentów
wystawiających extension points.
Na warstwie środowiska uruchomieniowego bazuje Nuxeo Core,
udostępniające serwisy wewnętrzne platformy, min: usługi repozytorium,
wersjonowania, bezpieczeństwa, zarządzania cyklem życia, obsługi zdarzeń.
Ostatnią warstwą jest warstwa prezentacji, podobno w funkcjonalności do
odpowiedniej warstwy klasycznych systemów klasy Enterprise. Wyświetla ona
zawartość treści, możliwe opcje oraz definiuje nawigację między widokami.

  1. TECHNOLOGIE PLATFORMY
Nuxeo domyślnie dostarczane jest z serwerem aplikacji JBoss. Implementacja
opiera się o framework Seam z rozbudowaną warstwą prezentacji JSF (własne
biblioteki tagów) wzbogaconą przez Facelets oraz (od najnowszej wersji 5.2)
RichFaces. Ponadto wykorzystywany jest ajax4jsf. Nuxeo domyślnie korzysta z
implementacji warstwy persystencji opartej o bazę danych Hipersonic, dostarczana
wraz z serwerem JBoss.

### Kodery / dekodery tekstu do tłumaczenia – standard Xliff ###

  1. IDEA
Xliff - XML Localization Interchange File Format to specjalny format plików oparty na XML. Został stworzony, aby ułatwić ekstrakcję tekstu, który ma zostać przetłumaczony. Translator może skupić się na samym tekście, nie biorąc pod uwagę jego formatowania. Co najważniejsze, użycie takiego standardu stwarza uniwersalne narzędzie do obsługi różnych formatów plików. Używamy biblioteki file2xliff4j, której główną zaletą jest szeroka gama obsługiwanych rodzajów plików (xml, html, txt, pliki open office'owe oraz microsoftowe).

  1. TŁUMACZENIE
Proces tłumaczenia za pomocą formatu xliff wygląda następująco:
  1. Ekstrakcja tekstu do tłumaczenia z oryginalnego pliku;
  1. Konwersja pliku do formatu xliff;
  1. Wysłanie pliku xliff do tłumaczenia;
  1. Pobranie przetłumaczonego tekstu ze zwrotnego pliku i zmapowanie go w odpowiednie miejsce do oryginalnego dokumentu;

Oryginalny plik, oprócz tekstu, zawiera także znaczniki dotyczące formatowania tekstu (np. '< b >' w pliku html). Wszystkie te nietłumaczone detale dotyczące formatowania i wyglądu tekstu zapisane zostaną w specjalnym pliku 'skeleton', gdzie tłumaczone zdania zostają zastąpione specjalnymi znacznikami: '%%%n%%%' (gdzie 'n' to kolejny numer fragmentu tekstu - będzie to ważne przy mapowaniu przetłumaczonego tekstu z powrotem do pliku). Każdy fragment tłumaczonego tekstu natomiast znajdzie się w pliku xliff pomiędzy tagami 'trans-unit'. Tagi te posiadają również atrybuty id (oznaczające odpowiednie miejsce do późniejszego wstawienia w pliku skeleton) oraz zagnieżdżone tagi 'source' z atrybutem 'xml:lang' (oznaczającymi język, w którym napisany jest tekst). Cały mechanizm nie jest skomplikowany, problemem jest tylko implementacja odpowiednich filtrów, co w przypadku plików xml czy html jest dość proste, może natomiast rodzić problemy przy gorzej udokumentowanych formatach (np. windowsowych).

W trakcie tłumaczenia w tagach 'trans-unit' zostają zagnieżdżone tagi 'alt-trans' zawierające przetłumaczone odpowiednie kawałki tekstu. Ten właśnie przetłumaczony tekst zostanie następnie zmapowany z plikiem skeleton, dając w rezultacie poprawnie sformatowany plik.